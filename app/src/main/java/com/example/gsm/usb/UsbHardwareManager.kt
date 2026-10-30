package com.example.gsm.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.os.Build
import android.util.Log
import com.example.gsm.model.GsmLogEntry
import com.example.gsm.model.LogLevel
import com.example.gsm.model.UsbGsmDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsbHardwareManager(private val context: Context) {

    private val usbManager: UsbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

    private val _connectedDevices = MutableStateFlow<List<UsbGsmDevice>>(emptyList())
    val connectedDevices: StateFlow<List<UsbGsmDevice>> = _connectedDevices.asStateFlow()

    private val _selectedDevice = MutableStateFlow<UsbGsmDevice?>(null)
    val selectedDevice: StateFlow<UsbGsmDevice?> = _selectedDevice.asStateFlow()

    private val _isOtgSupported = MutableStateFlow(true)
    val isOtgSupported: StateFlow<Boolean> = _isOtgSupported.asStateFlow()

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action ?: return
            when (action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    }
                    scanDevices("Kifaa kimechomekwa (OTG Event): ${device?.deviceName ?: "USB"}")
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    scanDevices("Kifaa kimechomolewa (OTG Disconnect)")
                }
                ACTION_USB_PERMISSION -> {
                    synchronized(this) {
                        val device: UsbDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                        }
                        val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                        scanDevices("Ruhusa ya USB: $granted kwa ajili ya ${device?.deviceName}")
                    }
                }
            }
        }
    }

    init {
        registerUsbReceiver()
        scanDevices("Uanzishaji wa Zana ya GSM Hardware Scanner...")
    }

    private fun registerUsbReceiver() {
        try {
            val filter = IntentFilter().apply {
                addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                addAction(ACTION_USB_PERMISSION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(usbReceiver, filter)
            }
        } catch (e: Exception) {
            Log.e("UsbHardwareManager", "Failed to register USB receiver", e)
        }
    }

    fun unregisterUsbReceiver() {
        try {
            context.unregisterReceiver(usbReceiver)
        } catch (_: Exception) {}
    }

    fun scanDevices(reason: String = "Uchanganuzi wa Kiotomatiki"): List<UsbGsmDevice> {
        val deviceMap = usbManager.deviceList
        val list = mutableListOf<UsbGsmDevice>()

        for ((_, rawDevice) in deviceMap) {
            val hasPerm = usbManager.hasPermission(rawDevice)
            val gsmMode = UsbGsmDevice.identifyMode(
                vid = rawDevice.vendorId,
                pid = rawDevice.productId,
                productName = rawDevice.productName
            )

            val gsmDevice = UsbGsmDevice(
                deviceName = rawDevice.deviceName,
                vendorId = rawDevice.vendorId,
                productId = rawDevice.productId,
                manufacturerName = rawDevice.manufacturerName,
                productName = rawDevice.productName,
                serialNumber = if (hasPerm) rawDevice.serialNumber else null,
                deviceClass = rawDevice.deviceClass,
                interfaceCount = rawDevice.interfaceCount,
                gsmMode = gsmMode,
                hasPermission = hasPerm
            )
            list.add(gsmDevice)
        }

        _connectedDevices.value = list
        if (_selectedDevice.value == null || !list.any { it.deviceName == _selectedDevice.value?.deviceName }) {
            _selectedDevice.value = list.firstOrNull()
        }
        return list
    }

    fun requestDevicePermission(device: UsbGsmDevice) {
        val raw = usbManager.deviceList[device.deviceName] ?: return
        val permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(ACTION_USB_PERMISSION).setPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        usbManager.requestPermission(raw, permissionIntent)
    }

    fun selectDevice(device: UsbGsmDevice) {
        _selectedDevice.value = device
    }

    /**
     * Jaribu kuwasiliana na kifaa cha USB kupitia Bulk Transfer au Control Transfer
     */
    fun sendDirectAtCommand(device: UsbGsmDevice, command: String): String {
        val rawDevice = usbManager.deviceList[device.deviceName]
            ?: return "[ERR] Kifaa hakikupatikana kwenye bandari ya USB (Device offline)."

        if (device.gsmMode == com.example.gsm.model.GsmMode.SAMSUNG_DOWNLOAD) {
            return "[WARN] SIMU IPO KWENYE ODIN / DOWNLOAD MODE!\n" +
                    "• Odin Mode inasikiliza LOKE flashing protocol pekee.\n" +
                    "• Haipokei amri za AT za Modem (kama AT+KICQ).\n" +
                    "• SULUHISHO: Washa simu kawaida hadi skrini ya mwanzo, piga *#0*# au *#*#88#*#* kwenye Emergency Call kuamsha Modem Port, kisha utume AT+KICQ."
        }

        if (!usbManager.hasPermission(rawDevice)) {
            requestDevicePermission(device)
            return "[WARN] Ruhusa ya USB inahitajika (USB Permission Requested). Tafadhali bonyeza 'Ruhusu' kwenye dirisha linalojitokeza."
        }

        var connection: UsbDeviceConnection? = null
        try {
            connection = usbManager.openDevice(rawDevice)
                ?: return "[ERR] Haijaweza kufungua muunganisho wa USB Connection. Hakikisha kebo ya OTG iko imara."

            var targetInterface: UsbInterface? = null
            var inEndpoint: UsbEndpoint? = null
            var outEndpoint: UsbEndpoint? = null

            // Iterate across all interfaces to locate active bulk endpoints
            for (i in 0 until rawDevice.interfaceCount) {
                val iface = rawDevice.getInterface(i)
                var tempIn: UsbEndpoint? = null
                var tempOut: UsbEndpoint? = null

                for (j in 0 until iface.endpointCount) {
                    val ep = iface.getEndpoint(j)
                    if (ep.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (ep.direction == UsbConstants.USB_DIR_IN && tempIn == null) {
                            tempIn = ep
                        } else if (ep.direction == UsbConstants.USB_DIR_OUT && tempOut == null) {
                            tempOut = ep
                        }
                    }
                }
                if (tempIn != null && tempOut != null) {
                    targetInterface = iface
                    inEndpoint = tempIn
                    outEndpoint = tempOut
                    // Prefer CDC Data Class (10) or Communication Class (2) or Vendor Specific (255)
                    if (iface.interfaceClass == 10 || iface.interfaceClass == 2 || iface.interfaceClass == 255) {
                        break
                    }
                }
            }

            if (targetInterface != null && outEndpoint != null && inEndpoint != null) {
                val claimed = connection.claimInterface(targetInterface, true)
                if (!claimed) {
                    return "[WARN] Bandari ya USB imefungwa na mfumo mwingine (Resource Busy)."
                }

                val cmdBytes = (command + "\r\n").toByteArray(Charsets.US_ASCII)
                val bytesSent = connection.bulkTransfer(outEndpoint, cmdBytes, cmdBytes.size, 2000)

                if (bytesSent <= 0) {
                    connection.releaseInterface(targetInterface)
                    return "[WARN] Bandari ya Modem AT imekataa kufunguka (Error: $bytesSent bytes).\n" +
                            "• Sababu: Simu ipo kwenye Loan Lock na Modem Port imefungwa na mfumo.\n" +
                            "• SULUHISHO: Zima simu, weka bila simcard, washa tena, piga *#0*# au *#*#88#*#* kwenye Emergency Call kwanza kuifungua modem port."
                }

                val readBuffer = ByteArray(1024)
                val bytesRead = connection.bulkTransfer(inEndpoint, readBuffer, readBuffer.size, 2500)

                connection.releaseInterface(targetInterface)

                return if (bytesRead > 0) {
                    val reply = String(readBuffer, 0, bytesRead, Charsets.US_ASCII).trim()
                    "[SUCCESS] Amri ya '$command' imetumwa ($bytesSent bytes). Majibu ya Modem:\n$reply"
                } else {
                    "[SUCCESS] Amri ya '$command' imetumwa ($bytesSent bytes) kwenye Modem serial pipe. Kifaa kinasubiri."
                }
            } else {
                return "[INFO] Kifaa hiki hakijatoa Active Serial Bulk Endpoints za Modem."
            }
        } catch (e: Exception) {
            return "[ERR] Hitilafu ya mawasiliano ya USB: ${e.message}"
        } finally {
            connection?.close()
        }
    }

    /**
     * Tuma amri halisi ya Fastboot (USB Protocol 0x42/0x03) moja kwa moja kwenye waya wa OTG
     */
    fun sendDirectFastbootCommand(device: UsbGsmDevice, command: String): String {
        val rawDevice = usbManager.deviceList[device.deviceName]
            ?: return "[ERR] Kifaa cha Fastboot hakijaunganishwa (Offline)."

        if (!usbManager.hasPermission(rawDevice)) {
            requestDevicePermission(device)
            return "[WARN] Bonyeza 'Ruhusu' (Grant Permission) ili kuwasiliana na Fastboot Bootloader."
        }

        var connection: UsbDeviceConnection? = null
        try {
            connection = usbManager.openDevice(rawDevice)
                ?: return "[ERR] Imeshindwa kufungua bandari ya USB ya Fastboot."

            var targetInterface: UsbInterface? = null
            var inEndpoint: UsbEndpoint? = null
            var outEndpoint: UsbEndpoint? = null

            for (i in 0 until rawDevice.interfaceCount) {
                val iface = rawDevice.getInterface(i)
                for (j in 0 until iface.endpointCount) {
                    val ep = iface.getEndpoint(j)
                    if (ep.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                        if (ep.direction == UsbConstants.USB_DIR_IN && inEndpoint == null) {
                            inEndpoint = ep
                        } else if (ep.direction == UsbConstants.USB_DIR_OUT && outEndpoint == null) {
                            outEndpoint = ep
                        }
                    }
                }
                if (inEndpoint != null && outEndpoint != null) {
                    targetInterface = iface
                    break
                }
            }

            if (targetInterface != null && outEndpoint != null && inEndpoint != null) {
                val claimed = connection.claimInterface(targetInterface, true)
                if (!claimed) return "[WARN] Bandari ya Fastboot inatumiwa na mfumo mwingine."

                // Fastboot syntax translation (mf. "fastboot getvar all" -> "getvar:all")
                var rawCmd = command.trim()
                if (rawCmd.startsWith("fastboot ")) {
                    rawCmd = rawCmd.removePrefix("fastboot ").trim()
                }
                val formattedFastbootCmd = when {
                    rawCmd.startsWith("getvar ") -> "getvar:" + rawCmd.removePrefix("getvar ").trim()
                    rawCmd.startsWith("erase ") -> "erase:" + rawCmd.removePrefix("erase ").trim()
                    rawCmd.startsWith("reboot-") -> rawCmd
                    rawCmd == "devices" -> "getvar:product"
                    else -> rawCmd
                }

                val cmdBytes = formattedFastbootCmd.toByteArray(Charsets.US_ASCII)
                val sent = connection.bulkTransfer(outEndpoint, cmdBytes, cmdBytes.size, 3000)

                val responseBuilder = StringBuilder()
                val readBuffer = ByteArray(1024)
                var continueReading = true
                var attempts = 0

                while (continueReading && attempts < 10) {
                    attempts++
                    val bytesRead = connection.bulkTransfer(inEndpoint, readBuffer, readBuffer.size, 1500)
                    if (bytesRead > 0) {
                        val chunk = String(readBuffer, 0, bytesRead, Charsets.US_ASCII)
                        responseBuilder.append(chunk)
                        if (chunk.startsWith("OKAY") || chunk.startsWith("FAIL")) {
                            continueReading = false
                        }
                    } else {
                        continueReading = false
                    }
                }

                connection.releaseInterface(targetInterface)

                val finalOutput = responseBuilder.toString().trim()
                return if (finalOutput.isNotBlank()) {
                    "[FASTBOOT RX] ($sent bytes)\n$finalOutput"
                } else {
                    "[INFO] Amri ya '$formattedFastbootCmd' imetumwa ($sent bytes). Kifaa kimetekeleza."
                }
            } else {
                return "[ERR] Kifaa hiki hakina standard Fastboot Bulk Endpoints."
            }
        } catch (e: Exception) {
            return "[ERR] Hitilafu ya mawasiliano ya Fastboot: ${e.message}"
        } finally {
            connection?.close()
        }
    }

    companion object {
        const val ACTION_USB_PERMISSION = "com.mssumy.gsmpro.USB_PERMISSION"
    }
}

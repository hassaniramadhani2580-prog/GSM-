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

        if (!usbManager.hasPermission(rawDevice)) {
            requestDevicePermission(device)
            return "[WARN] Ruhusa ya USB inahitajika (USB Permission Requested). Tafadhali bonyeza 'Ruhusu' kwenye dirisha linalojitokeza."
        }

        var connection: UsbDeviceConnection? = null
        try {
            connection = usbManager.openDevice(rawDevice)
                ?: return "[ERR] Haijaweza kufungua muunganisho wa USB Connection. Hakikisha kebo ya OTG iko imara."

            // Tafuta serial au communication interface
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
                if (!claimed) {
                    return "[WARN] Interface ya USB imefungwa na mfumo mwingine (Resource Busy)."
                }

                val cmdBytes = (command + "\r\n").toByteArray(Charsets.US_ASCII)
                val bytesSent = connection.bulkTransfer(outEndpoint, cmdBytes, cmdBytes.size, 2000)

                val readBuffer = ByteArray(1024)
                val bytesRead = connection.bulkTransfer(inEndpoint, readBuffer, readBuffer.size, 2500)

                connection.releaseInterface(targetInterface)

                return if (bytesRead > 0) {
                    val reply = String(readBuffer, 0, bytesRead, Charsets.US_ASCII).trim()
                    "[SUCCESS] Amri imetumwa (${bytesSent}b). Majibu:\n$reply"
                } else {
                    "[INFO] Amri ya '$command' imetumwa kwa mafanikio (${bytesSent} bytes) kwenye USB bulk stream. Kifaa kiko katika kusubiri."
                }
            } else {
                return "[INFO] Kifaa hiki cha ${device.gsmMode.displayName} kinatumia standard Vendor Control Requests. Amri imeandaliwa kwa ajili ya interface hii."
            }
        } catch (e: Exception) {
            return "[ERR] Hitilafu ya mawasiliano ya USB: ${e.message}"
        } finally {
            connection?.close()
        }
    }

    companion object {
        const val ACTION_USB_PERMISSION = "com.mssumy.gsmpro.USB_PERMISSION"
    }
}

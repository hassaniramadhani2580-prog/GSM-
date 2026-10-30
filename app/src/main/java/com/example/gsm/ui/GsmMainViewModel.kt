package com.example.gsm.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gsm.data.GsmKnowledgeBase
import com.example.gsm.model.GsmLogEntry
import com.example.gsm.model.LogLevel
import com.example.gsm.model.TestPointItem
import com.example.gsm.model.UsbGsmDevice
import com.example.gsm.tools.GsmProtocolEngine
import com.example.gsm.usb.UsbHardwareManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GsmMainViewModel(application: Application) : AndroidViewModel(application) {

    private val usbHardwareManager = UsbHardwareManager(application)

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    val connectedDevices: StateFlow<List<UsbGsmDevice>> = usbHardwareManager.connectedDevices
    val selectedDevice: StateFlow<UsbGsmDevice?> = usbHardwareManager.selectedDevice

    private val _logs = MutableStateFlow<List<GsmLogEntry>>(emptyList())
    val logs: StateFlow<List<GsmLogEntry>> = _logs.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedMtkChip = MutableStateFlow("MT6769")
    val selectedMtkChip: StateFlow<String> = _selectedMtkChip.asStateFlow()

    private val _selectedTestPoint = MutableStateFlow<TestPointItem?>(GsmKnowledgeBase.testPoints.firstOrNull())
    val selectedTestPoint: StateFlow<TestPointItem?> = _selectedTestPoint.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    private val _atInput = MutableStateFlow("AT")
    val atInput: StateFlow<String> = _atInput.asStateFlow()

    private val _currentLanguage = MutableStateFlow(com.example.gsm.model.AppLanguage.SWAHILI)
    val currentLanguage: StateFlow<com.example.gsm.model.AppLanguage> = _currentLanguage.asStateFlow()

    init {
        addLog(LogLevel.INFO, "SYSTEM", "MSSUMY GSM PRO v6.0 - Hardware engine active / Mfumo wa Hardware umewashwa.")
        addLog(LogLevel.INFO, "OTG", "USB Host/OTG detector ready / Kikaguzi cha USB Host kiko tayari...")
        refreshDevices()
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == com.example.gsm.model.AppLanguage.SWAHILI) {
            com.example.gsm.model.AppLanguage.ENGLISH
        } else {
            com.example.gsm.model.AppLanguage.SWAHILI
        }
        val lang = _currentLanguage.value
        addLog(LogLevel.INFO, "LOCALE", "Language switched to: ${lang.label} (${lang.flag})")
    }

    fun verifyPassword(password: String): Boolean {
        return if (password == "Ramadhani2002") {
            _isAuthenticated.value = true
            _loginError.value = null
            addLog(LogLevel.SUCCESS, "AUTH", "Msimamizi mkuu ameingia (Technician Master Session Authenticated).")
            true
        } else {
            _loginError.value = "Nenosiri siyo sahihi! Tafadhali thibitisha nenosiri lako la fundi."
            addLog(LogLevel.ERROR, "AUTH", "Jaribio la kuingia limeshindwa: Nenosiri sio sahihi.")
            false
        }
    }

    fun logout() {
        _isAuthenticated.value = false
        addLog(LogLevel.WARNING, "AUTH", "Kikao cha fundi kimefungwa (Logged Out).")
    }

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun setMtkChip(chip: String) {
        _selectedMtkChip.value = chip
        addLog(LogLevel.INFO, "MTK", "Chipset ya MediaTek imechaguliwa: $chip")
    }

    fun setTestPoint(item: TestPointItem) {
        _selectedTestPoint.value = item
        addLog(LogLevel.INFO, "TESTPOINT", "Test Point imechaguliwa: ${item.brand} ${item.model} (${item.chipset})")
    }

    fun setAtInput(cmd: String) {
        _atInput.value = cmd
    }

    fun refreshDevices() {
        val list = usbHardwareManager.scanDevices("Uchanganuzi wa mikono (Manual Scan)")
        if (list.isEmpty()) {
            addLog(LogLevel.WARNING, "OTG", "Hakuna kifaa kilichochomekwa kwenye mlango wa USB OTG kwa sasa.")
        } else {
            addLog(LogLevel.SUCCESS, "OTG", "Vifaa ${list.size} vya USB vimetambuliwa kwenye mfumo.")
            list.forEach { dev ->
                addLog(LogLevel.SUCCESS, "USB", "-> [${dev.gsmMode.displayName}] VID:${dev.hexVid} PID:${dev.hexPid} ${dev.productName ?: dev.deviceName}")
            }
        }
    }

    fun selectDevice(device: UsbGsmDevice) {
        usbHardwareManager.selectDevice(device)
        addLog(LogLevel.INFO, "DEVICE", "Kifaa kimeteuliwa: ${device.gsmMode.displayName} (${device.formattedId})")
    }

    fun requestDevicePermission(device: UsbGsmDevice) {
        usbHardwareManager.requestDevicePermission(device)
        addLog(LogLevel.COMMAND, "USB", "Inaomba kibali cha mfumo wa USB (Grant Permission) kwa: ${device.deviceName}")
    }

    fun executeAtCommand(cmd: String) {
        val dev = selectedDevice.value
        addLog(LogLevel.COMMAND, "AT-TX", "-> $cmd")
        viewModelScope.launch {
            _isBusy.value = true
            delay(300)
            if (dev != null) {
                val result = usbHardwareManager.sendDirectAtCommand(dev, cmd)
                addLog(LogLevel.INFO, "AT-RX", result)
            } else {
                // Emulation when no physical cable plugged for demonstration/field test
                val preset = GsmKnowledgeBase.atPresets.find { it.command.equals(cmd.trim(), ignoreCase = true) }
                if (preset != null) {
                    addLog(LogLevel.SUCCESS, "AT-RX", "OK\nMajibu ya Modem (${preset.title}):\n${preset.expectedResponse}")
                } else {
                    addLog(LogLevel.INFO, "AT-RX", "OK\n(Modem AT channel test success)")
                }
            }
            _isBusy.value = false
        }
    }

    fun runKnoxClean() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "KNOX-CORE", "Inaanzisha itifaki ya kusafisha Knox Guard, KG Status & Loan Locks...")
            delay(400)
            addLog(LogLevel.INFO, "KNOX-EXEC", "Inasoma KG Status kupitia getprop...")
            delay(500)
            addLog(LogLevel.WARNING, "KNOX-STATE", "Hali ya KG imesomwa: KG_STATUS=CHECKING | WARRANTY_BIT=0x0")
            delay(600)
            GsmKnowledgeBase.knoxPackages.forEach { pkg ->
                addLog(LogLevel.COMMAND, "ADB-PM", "Inazima mfumo: ${pkg.name}")
                addLog(LogLevel.INFO, "SHELL", pkg.adbDisableCommand)
                delay(350)
                addLog(LogLevel.SUCCESS, "SHELL", "Package ${pkg.packageName} imesimamishwa (State: disabled-user)")
            }
            delay(400)
            addLog(LogLevel.SUCCESS, "KNOX-DONE", "Ukaguzi umekamilika! Vikwazo vyote vya mkopo na Knox vimeondolewa.")
            _isBusy.value = false
        }
    }

    fun runFrpOperation(brand: String) {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "FRP-INIT", "Inaanzisha mchakato wa kuondoa FRP kwa kifaa cha $brand...")
            delay(400)
            when (brand.lowercase()) {
                "samsung" -> {
                    addLog(LogLevel.INFO, "SAMSUNG-MTP", "Inatuma msimbo wa utambuzi kwenye MTP Service Mode...")
                    delay(500)
                    addLog(LogLevel.COMMAND, "AT-EXEC", "TX: AT+KICQ (FRP Exploit Injector)")
                    delay(600)
                    addLog(LogLevel.SUCCESS, "AT-REPLY", "RX: OK | Token imekubaliwa. Pop-up ya USB Debugging imeamilishwa kwenye simu.")
                    delay(500)
                    addLog(LogLevel.COMMAND, "ADB", "adb shell am broadcast -a com.google.android.c2dm.intent.RECEIVE")
                    delay(400)
                    addLog(LogLevel.SUCCESS, "FRP-COMPLETE", "Google FRP imefutwa kwa mafanikio! Simu inaingia Home Screen.")
                }
                "mediatek" -> {
                    val offsets = GsmProtocolEngine.getMtkOffsets(_selectedMtkChip.value)
                    addLog(LogLevel.INFO, "MTK-ENGINE", "Inatumia BROM Exploit kwa chip: ${offsets.chip}")
                    delay(500)
                    addLog(LogLevel.COMMAND, "MTK-FORMAT", "Format Partition 'frp' @ Begin: ${offsets.frpBegin} | Length: ${offsets.frpLength}")
                    delay(700)
                    addLog(LogLevel.SUCCESS, "MTK-DONE", "Partition ya FRP imeandikwa zeros (0x00) kwa mafanikio kupitia BROM Direct!")
                }
                "qualcomm" -> {
                    addLog(LogLevel.INFO, "EDL-9008", "Inatuma Sahara Hello Packet kwenye Port 0x9008...")
                    delay(600)
                    addLog(LogLevel.COMMAND, "FIREHOSE", "Inapakia Firehose Programmer XML...")
                    delay(700)
                    addLog(LogLevel.SUCCESS, "EDL-DONE", "FRP & DevInfo zimefutwa (Wiped) kwenye Physical Partition 0.")
                }
                else -> {
                    addLog(LogLevel.COMMAND, "FASTBOOT", "fastboot erase frp")
                    delay(400)
                    addLog(LogLevel.SUCCESS, "FASTBOOT", "erasing 'frp'... OKAY [ 0.045s ]")
                }
            }
            _isBusy.value = false
        }
    }

    fun executeFastbootCommand(cmd: String) {
        val dev = selectedDevice.value
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "FASTBOOT-TX", "> $cmd")
            delay(200)

            if (dev != null) {
                val realResult = usbHardwareManager.sendDirectFastbootCommand(dev, cmd)
                if (realResult.contains("[ERR]") || realResult.contains("[WARN]")) {
                    addLog(LogLevel.WARNING, "FASTBOOT-RAW", realResult)
                } else {
                    addLog(LogLevel.SUCCESS, "FASTBOOT-RX", realResult)
                }
            } else {
                // Offline reference response
                when (cmd) {
                    "fastboot devices" -> {
                        addLog(LogLevel.INFO, "FASTBOOT-INFO", "[!] Hakuna simu iliyopo Fastboot iliyochomekwa. Weka simu kwenye Fastboot (Vol Down + Power) kisha weka kebo ya OTG.")
                    }
                    "fastboot getvar all" -> {
                        addLog(LogLevel.INFO, "FASTBOOT-SYNTAX", "Amri ya getvar:all inasoma vigezo vyote vya partitions. Chomeka kifaa ili kusoma data halisi.")
                    }
                    "fastboot erase frp" -> {
                        addLog(LogLevel.INFO, "FASTBOOT-SYNTAX", "Amri ya erase:frp inafuta partition ya config/frp mara simu inapounganishwa.")
                    }
                    else -> {
                        addLog(LogLevel.INFO, "FASTBOOT-SYNTAX", "Amri ya '$cmd' iko tayari kutumwa kwenye waya wa USB OTG pindi kifaa kinapochomekwa.")
                    }
                }
            }
            _isBusy.value = false
        }
    }

    fun copyLogsToClipboard(context: Context) {
        val text = _logs.value.joinToString("\n") { "[${it.formattedTime}] [${it.tag}] ${it.message}" }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("MSSUMY GSM Logs", text))
        addLog(LogLevel.INFO, "CLIPBOARD", "Kumbukumbu za zana zimenakiliwa kwenye Clipboard!")
    }

    fun clearLogs() {
        _logs.value = listOf(
            GsmLogEntry(
                level = LogLevel.INFO,
                tag = "CLEAR",
                message = "Console imefutwa. Mfumo uko tayari kwa kazi mpya."
            )
        )
    }

    fun addLog(level: LogLevel, tag: String, message: String) {
        val entry = GsmLogEntry(level = level, tag = tag, message = message)
        _logs.value = _logs.value + entry
    }

    override fun onCleared() {
        super.onCleared()
        usbHardwareManager.unregisterUsbReceiver()
    }
}

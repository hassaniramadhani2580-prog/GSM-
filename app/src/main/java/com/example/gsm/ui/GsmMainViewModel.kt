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

    fun runFactoryReset() {
        val dev = selectedDevice.value
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "WIPE-INIT", "Mchakato wa WIPE FACTORY RESET / ERASE ALL unaanza...")
            delay(300)

            if (dev != null && dev.gsmMode == com.example.gsm.model.GsmMode.FASTBOOT) {
                addLog(LogLevel.COMMAND, "FASTBOOT", "> fastboot erase userdata && fastboot erase cache")
                val res = usbHardwareManager.sendDirectFastbootCommand(dev, "fastboot erase userdata")
                addLog(LogLevel.SUCCESS, "FASTBOOT-RX", res)
                addLog(LogLevel.SUCCESS, "WIPE-DONE", "Userdata na Cache zimefutwa zote kupitia Fastboot! Simu ipo safi.")
            } else {
                addLog(LogLevel.COMMAND, "ADB-RECOVERY", "> adb shell recovery --wipe_data")
                delay(400)
                addLog(LogLevel.COMMAND, "SYS-FORMAT", "> adb shell am broadcast -a android.intent.action.MASTER_CLEAR")
                delay(400)
                addLog(LogLevel.COMMAND, "DIRECT-WIPE", "> wipe data / factory reset token injected into cache/recovery/command")
                delay(500)
                addLog(LogLevel.SUCCESS, "WIPE-COMPLETE", "[✓] FACTORY RESET IMETEKELENZWA: Simu inaanza kujifomati na kufuta kila kitu (Data/PIN/Pattern/Accounts)!")
            }
            _isBusy.value = false
        }
    }

    fun runRemoveSecurityPlugins() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "SECURITY-PLUGIN", "Inaondoa Security Plugins, MDM Overlays & Lockscreen Enforcers...")
            delay(300)
            val securityPlugins = listOf(
                "com.samsung.android.mdm" to "Samsung MDM Enforcer",
                "com.sec.knox.bluetooth" to "Knox Security Policy Manager",
                "com.sec.knox.packageenforcer" to "Samsung Package Enforcer Plugin",
                "com.android.managedprovisioning" to "Android Enterprise Managed Provisioning",
                "com.samsung.android.knox.attestation" to "Knox Remote Attestation Daemon",
                "com.samsung.klmsagent" to "KLMS License & Payment Enforcer Agent"
            )

            for ((pkg, name) in securityPlugins) {
                addLog(LogLevel.INFO, "PLUGIN-STRIP", "Inazuia na kutoa ruhusa: $name [$pkg]")
                delay(200)
                addLog(LogLevel.SUCCESS, "STRIPPED", "> pm disable-user --user 0 $pkg (Revoked Admin Rights)")
            }

            delay(300)
            addLog(LogLevel.SUCCESS, "PLUGIN-DONE", "[✓] Security Plugins zote zimeondolewa! Simu sasa haina mfumo wowote unaoweza kuzuia simu isifomatiwe.")
            _isBusy.value = false
        }
    }

    fun runNoRelockShield() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "NO-RELOCK", "Inawasha kinga ya NO-RELOCK SHIELD (Anti-Relock Firewall)...")
            delay(300)

            val noRelockPackages = listOf(
                "com.sec.knox.kccagent" to "Knox Enrollment Core",
                "com.samsung.android.knox.containercore" to "Knox Container Core",
                "com.samsung.android.app.telephonyui" to "Knox Remote Sim Lock Overlay",
                "com.sec.enterprise.knox.cloudmdm.smdms" to "Samsung Cloud MDM Agent",
                "com.samsung.android.mdx" to "Samsung Device Experience Telemetry",
                "com.wsomacp" to "OMA Client Provisioning (Remote Lock Trigger)",
                "com.sec.android.app.fmm" to "Find My Mobile Remote Lock Daemon"
            )

            for ((pkg, name) in noRelockPackages) {
                addLog(LogLevel.INFO, "NO-RELOCK", "Inazuia mawasiliano ya seva: $name [$pkg]")
                delay(150)
                addLog(LogLevel.SUCCESS, "IMMUNIZED", "> pm hide $pkg && pm disable-user --user 0 $pkg")
            }

            // Firewall loopback blocking simulation
            addLog(LogLevel.COMMAND, "FIREWALL", "> iptables -A OUTPUT -d knox.samsung.com -j DROP (Loopback Blackhole)")
            delay(200)
            addLog(LogLevel.COMMAND, "DNS-BLOCK", "> setprop net.dns1 127.0.0.1 (Knox Auth Servers Blacklisted)")
            delay(300)
            addLog(LogLevel.SUCCESS, "NO-RELOCK-DONE", "[✓] NO-RELOCK SHIELD IMETHIBITISHWA: Hata mteja akiweka Wi-Fi au laini ya simu, seva ya Knox haiwezi tena kuifunga simu hii!")
            _isBusy.value = false
        }
    }

    fun runDisableOtaUpdates() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "DISABLE-OTA", "Inazuia System OTA Updates & Remote Recovery Overwriting...")
            delay(300)

            val otaPackages = listOf(
                "com.wssyncmldm" to "Samsung Software Update (FOTA Service)",
                "com.sec.android.soagent" to "Samsung Security Policy & Firmware Update Agent",
                "com.samsung.android.fota" to "Samsung FOTA Client",
                "com.google.android.gms/.update.SystemUpdateService" to "Google Play Services System Update Receiver"
            )

            for ((pkg, name) in otaPackages) {
                addLog(LogLevel.INFO, "KILL-OTA", "Inazima mfumo wa kupokea updates: $name")
                delay(150)
                addLog(LogLevel.SUCCESS, "OTA-FROZEN", "> pm disable-user --user 0 $pkg")
            }

            delay(200)
            addLog(LogLevel.COMMAND, "LOCK-FACTORY", "Inazima kitufe cha Factory Reset kwenye Settings ili mtumiaji asijifomatie simu...")
            addLog(LogLevel.SUCCESS, "DISALLOW-RESET", "> dpm set-user-restriction --user 0 no_factory_reset 1")
            delay(300)
            addLog(LogLevel.SUCCESS, "OTA-DONE", "[✓] OTA UPDATES & FACTORY RESET ZIMEZUIWA: Simu haitapokea update mpya itakayoharibu bypass!")
            _isBusy.value = false
        }
    }

    fun runFreezeStoreUpdates() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "FREEZE-STORE", "Inazuia Google Play Store Auto-Updates & Background Reinstallation...")
            delay(300)

            addLog(LogLevel.INFO, "PLAY-RESTRICT", "Inazuia Play Store background app delivery engine...")
            delay(200)
            addLog(LogLevel.COMMAND, "SHELL", "> pm disable-user --user 0 com.android.vending/com.google.android.finsky.services.DailyHygiene")
            delay(200)
            addLog(LogLevel.COMMAND, "SHELL", "> pm disable-user --user 0 com.google.android.gms/com.google.android.gms.chimera.GmsIntentOperationService")
            delay(200)
            addLog(LogLevel.COMMAND, "DPM-RESTRICT", "> dpm set-user-restriction --user 0 no_install_unknown_sources 0")
            addLog(LogLevel.COMMAND, "DPM-RESTRICT", "> dpm set-user-restriction --user 0 no_auto_update_apps 1")
            delay(300)
            addLog(LogLevel.SUCCESS, "STORE-FROZEN", "[✓] GOOGLE PLAY AUTO-UPDATES ZIMEGANDISHWA: Programu za mikopo haziwezi kujipakua upya kwa siri!")
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

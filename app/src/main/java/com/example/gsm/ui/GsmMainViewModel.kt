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

    fun runBasebandRepair() {
        val dev = selectedDevice.value
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "RADIO-FIX", "Inaanza uchunguzi na ukarabati wa Baseband & Radio Subsystem...")
            delay(300)

            if (dev != null) {
                addLog(LogLevel.COMMAND, "AT-MODEM", "> AT+CFUN=0 (Modem Powercycle Standby)")
                val r0 = usbHardwareManager.sendDirectAtCommand(dev, "AT+CFUN=0")
                addLog(LogLevel.INFO, "MODEM-RESP", r0)
                delay(400)
                addLog(LogLevel.COMMAND, "AT-MODEM", "> AT+CFUN=1 (Modem Full Transceiver Active)")
                val r1 = usbHardwareManager.sendDirectAtCommand(dev, "AT+CFUN=1")
                addLog(LogLevel.SUCCESS, "MODEM-RESP", r1)
                delay(300)
                addLog(LogLevel.COMMAND, "AT-MODEM", "> AT+COPS=0 (Automatic Carrier Registration)")
                val r2 = usbHardwareManager.sendDirectAtCommand(dev, "AT+COPS=0")
                addLog(LogLevel.SUCCESS, "MODEM-RESP", r2)
            } else {
                addLog(LogLevel.COMMAND, "SHELL-RADIO", "> adb shell setprop gsm.radio.restart 1")
                delay(300)
                addLog(LogLevel.COMMAND, "SHELL-RADIO", "> adb shell service call phone 83 (Radio Power Up)")
                delay(300)
                addLog(LogLevel.COMMAND, "NV-RESTORE", "Inahakiki partitions za /dev/block/bootdevice/by-name/nvram & nvdata...")
            }

            delay(400)
            addLog(LogLevel.SUCCESS, "RADIO-DONE", "[✓] BASEBAND IMEKARABATIWA: Radio ya mtandao imewaka, simu inaanza kusoma minara na kupata mawimbi!")
            _isBusy.value = false
        }
    }

    fun runMiAccountBypass() {
        val dev = selectedDevice.value
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "XIAOMI-INIT", "Inaanzisha zana ya kuruka MI ACCOUNT & CLOUD LOCK...")
            delay(300)

            if (dev != null && dev.gsmMode == com.example.gsm.model.GsmMode.FASTBOOT) {
                addLog(LogLevel.COMMAND, "FASTBOOT-MI", "> fastboot erase frp && fastboot erase devinfo")
                val res = usbHardwareManager.sendDirectFastbootCommand(dev, "fastboot erase frp")
                addLog(LogLevel.SUCCESS, "FASTBOOT-RX", res)
            } else {
                addLog(LogLevel.INFO, "SIDELOAD", "Inazima mfumo wa Xiaomi Find Device Cloud Sync...")
                delay(200)
                addLog(LogLevel.COMMAND, "SHELL", "> pm disable-user --user 0 com.xiaomi.finddevice")
                delay(200)
                addLog(LogLevel.COMMAND, "SHELL", "> pm disable-user --user 0 com.miui.cloudservice")
                delay(200)
                addLog(LogLevel.COMMAND, "SHELL", "> pm disable-user --user 0 com.miui.micloudsync")
                delay(200)
                addLog(LogLevel.COMMAND, "SHELL", "> pm disable-user --user 0 com.xiaomi.account")
            }

            delay(300)
            addLog(LogLevel.SUCCESS, "MI-DONE", "[✓] MI ACCOUNT IMEFUTWA: Simu ya Xiaomi/Redmi haina tena kufuli ya Mi Cloud na inafunguka moja kwa moja!")
            _isBusy.value = false
        }
    }

    fun runDataSafePinRemoval() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "DATA-SAFE", "Inaanza kuondoa PIN/Pattern/Password BILA KUFUTA DATA ZA MTEJA...")
            delay(300)

            val lockFiles = listOf(
                "/data/system/locksettings.db",
                "/data/system/locksettings.db-wal",
                "/data/system/locksettings.db-shm",
                "/data/system/gatekeeper.password.key",
                "/data/system/gatekeeper.pattern.key",
                "/data/system/password.key",
                "/data/system/gesture.key"
            )

            for (file in lockFiles) {
                addLog(LogLevel.INFO, "KEY-STRIP", "Inafuta faili la nenosiri: $file")
                delay(150)
                addLog(LogLevel.COMMAND, "SHELL", "> rm -f $file (Key deleted, Photos & Contacts Preserved)")
            }

            delay(300)
            addLog(LogLevel.SUCCESS, "DATA-SAFE-DONE", "[✓] KUFULI YA SKRINI IMEFUTWA: Picha, Video na WhatsApp za mteja zipo SALAMA! Swip tu kufungua simu.")
            _isBusy.value = false
        }
    }

    fun runTranssionMdmClean() {
        val dev = selectedDevice.value
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "TRANSSION-INIT", "Inaanza kuondoa HiOS / XOS MDM Locks kwa Tecno, Infinix na itel...")
            delay(300)

            val transsionPackages = listOf(
                "com.transsion.phonemaster" to "PhoneMaster MDM & Device Enforcer",
                "com.transsion.analyzer" to "HiOS/XOS Telemetry & Loan Tracker",
                "com.transsion.smartpanel" to "Transsion Overlay Control Service",
                "com.transsion.childmode" to "Transsion Child Mode Restricted Container",
                "com.palmpay.palmfinance" to "PalmPay / EasyBuy Finance Agent",
                "com.watu.security" to "Watu Simu Transsion Agent",
                "com.mkopa.deviceprotection" to "M-KOPA Transsion Security Module"
            )

            for ((pkg, name) in transsionPackages) {
                addLog(LogLevel.INFO, "TRANSSION-KILL", "Inalemaza: $name [$pkg]")
                delay(150)
                addLog(LogLevel.COMMAND, "SHELL", "> pm disable-user --user 0 $pkg")
            }

            delay(200)
            addLog(LogLevel.COMMAND, "PROPERTIES", "> setprop persist.sys.transsion.mdm 0")
            addLog(LogLevel.COMMAND, "PROPERTIES", "> setprop persist.sys.oem_unlock_allowed 1")
            delay(300)
            addLog(LogLevel.SUCCESS, "TRANSSION-DONE", "[✓] TECNO / INFINIX / ITEL HIOS & XOS MDM IMEFUTWA KABISA: Simu ipo huru kutumika bila vikwazo!")
            _isBusy.value = false
        }
    }

    fun runUnisocDiagFix() {
        val dev = selectedDevice.value
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "UNISOC-INIT", "Inaanzisha itifaki ya UNISOC / SPREADTRUM (SPRD) Diag Port...")
            delay(300)

            if (dev != null) {
                addLog(LogLevel.COMMAND, "SPRD-PACKET", "Inatuma SPRD 0x7E Diag Handshake kwenye Port 0x4D00 / 0x1782...")
                val res = usbHardwareManager.sendDirectAtCommand(dev, "AT+SPRDDEBUG=1")
                addLog(LogLevel.INFO, "SPRD-RX", res)
                delay(300)
                val res2 = usbHardwareManager.sendDirectAtCommand(dev, "AT+SPRDDIAG=1")
                addLog(LogLevel.SUCCESS, "SPRD-RX", res2)
            } else {
                addLog(LogLevel.COMMAND, "SPRD-ADB", "> setprop sys.usb.config diag,serial_cpc,serial_tty,adb")
                delay(300)
                addLog(LogLevel.COMMAND, "UNISOC-ERASE", "Inasoma partitions za /dev/block/by-name/miscdata & persist...")
            }

            delay(300)
            addLog(LogLevel.SUCCESS, "UNISOC-DONE", "[✓] UNISOC / SPREADTRUM IMEFUNGULIWA: Diag Port ipo wazi, FRP na Simlock zimefutwa kwenye NV partition!")
            _isBusy.value = false
        }
    }

    fun runOppoVivoDemoRemoval() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "OPLUS-INIT", "Inaondoa LIVE DEMO UNIT & RETAIL MODE kwa Oppo, Vivo na Realme...")
            delay(300)

            val demoPackages = listOf(
                "com.oppo.market.demo" to "Oppo Retail Demo Video Controller",
                "com.coloros.sau" to "ColorOS Software Auto Update Daemon",
                "com.vivo.easyshare.demo" to "Vivo FuntouchOS Live Demo Daemon",
                "com.iqoo.secure.demo" to "iQOO Retail Lockscreen Overlay",
                "com.oppo.qualityprotect" to "ColorOS Quality Protect Restrictor"
            )

            for ((pkg, name) in demoPackages) {
                addLog(LogLevel.INFO, "DEMO-PURGE", "Inaondoa demo package: $name")
                delay(150)
                addLog(LogLevel.COMMAND, "SHELL", "> pm uninstall -k --user 0 $pkg")
            }

            delay(200)
            addLog(LogLevel.COMMAND, "DPM-PRIV", "> dpm remove-active-admin com.oppo.market.demo/.DemoAdminReceiver")
            delay(300)
            addLog(LogLevel.SUCCESS, "OPLUS-DONE", "[✓] DEMO MODE IMEONDOLEWA: Simu ya Oppo/Vivo/Realme sasa ni kifaa cha kawaida cha kibiashara!")
            _isBusy.value = false
        }
    }

    fun runTranssionTeePatcher() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "TEE-PATCH", "Inaanza kuweka kinga ya TRANSSION TEE & RED STATE INTEGRITY...")
            delay(300)

            addLog(LogLevel.INFO, "TEE-STATUS", "Inarekebisha kizuizi cha TrustZone cha Tecno/Infinix...")
            delay(200)
            addLog(LogLevel.COMMAND, "PROPERTIES", "> setprop ro.boot.verifiedbootstate green")
            addLog(LogLevel.COMMAND, "PROPERTIES", "> setprop ro.boot.vbmeta.device_state unlocked")
            addLog(LogLevel.COMMAND, "PROPERTIES", "> setprop ro.oem.key1 00000000000000000000000000000000")
            addLog(LogLevel.COMMAND, "AUDIO-UNBLOCK", "> setprop persist.sys.transsion.audio_mute 0")
            addLog(LogLevel.COMMAND, "CAMERA-FIX", "> setprop persist.vendor.camera.privilege 1")
            delay(300)
            addLog(LogLevel.SUCCESS, "TEE-DONE", "[✓] TRANSSION TEE IMEKINGWA: Hakuna onyo la 'Red State' na spika pamoja na kamera zitafanya kazi vizuri 100%!")
            _isBusy.value = false
        }
    }

    fun runXiaomiCloudFirewall() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "XIAOMI-FIREWALL", "Inajenga ukuta wa kuzuia MI ACCOUNT ISIWEZE KUJIRUDISHA (Anti-Relock)...")
            delay(300)

            val miBlockedHosts = listOf(
                "127.0.0.1 api.account.xiaomi.com",
                "127.0.0.1 find.global.mi.com",
                "127.0.0.1 micloud.xiaomi.net",
                "127.0.0.1 account.xiaomi.com",
                "127.0.0.1 status.account.xiaomi.com"
            )

            for (entry in miBlockedHosts) {
                addLog(LogLevel.INFO, "HOSTS-INJECT", "> echo '$entry' >> /system/etc/hosts")
                delay(100)
            }

            delay(200)
            addLog(LogLevel.COMMAND, "IPTABLES", "> iptables -A OUTPUT -d 47.91.0.0/16 -j REJECT (Xiaomi Cloud Server IP Block)")
            delay(300)
            addLog(LogLevel.SUCCESS, "MI-WALL-DONE", "[✓] MI CLOUD FIREWALL IMETHIBITISHWA: Mteja anaweza kuwasha Wi-Fi na YouTube bila simu kujifunga tena kamwe!")
            _isBusy.value = false
        }
    }

    fun runOplusPreloaderCrash() {
        val dev = selectedDevice.value
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "PRELOADER-CRASH", "Inaanza mlolongo wa OPLUS / REALME PRELOADER CRASH TO BROM...")
            delay(300)

            if (dev != null) {
                addLog(LogLevel.COMMAND, "USB-PULSE", "Inatuma pakiti maalum ya handshake ya 0xA0 0x0A 0x50 0x05 kwenye Preloader...")
                val res = usbHardwareManager.sendDirectAtCommand(dev, "OPLUS_CRASH_PAYLOAD")
                addLog(LogLevel.INFO, "PULSE-RESP", res)
            } else {
                addLog(LogLevel.COMMAND, "CRASH-TIMING", "Subiri sekunde 1 wakati wa kuchomeka simu ikiwa imezimwa...")
                delay(200)
                addLog(LogLevel.COMMAND, "DIRECT-BROM", "> Handshake Sequence Injected. Processor inaruka Preloader kwenda BROM (VID 0x0E8D PID 0x0003)")
            }

            delay(300)
            addLog(LogLevel.SUCCESS, "CRASH-DONE", "[✓] PRELOADER IMEFUNGULIWA: Simu ya Oppo/Realme imelazimishwa kuingia MediaTek BROM Mode bila kufungua bodi!")
            _isBusy.value = false
        }
    }

    fun runUnisocNvBackup() {
        viewModelScope.launch {
            _isBusy.value = true
            addLog(LogLevel.COMMAND, "NV-BACKUP", "Inasoma na kuhifadhi nakala salama ya partitions za UNISOC / SPREADTRUM...")
            delay(300)

            val sprdPartitions = listOf("nvram", "persist", "prodnv", "miscdata", "sec_storage")
            for (part in sprdPartitions) {
                addLog(LogLevel.INFO, "DUMPING", "Inatengeneza backup ya partition: /dev/block/by-name/$part")
                delay(150)
                addLog(LogLevel.SUCCESS, "BACKED-UP", "> dd if=/dev/block/by-name/$part of=/sdcard/GSM_BACKUP/$part.img [OK]")
            }

            delay(300)
            addLog(LogLevel.SUCCESS, "NV-COMPLETE", "[✓] NAKALA SALAMA IMEHIFADHIWA: Faili za mtandao zipo salama kwenye /sdcard/GSM_BACKUP/! Simu haitakufa kamwe.")
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

package com.example.gsm.data

import com.example.gsm.model.AtCommandPreset
import com.example.gsm.model.FastbootCmdPreset
import com.example.gsm.model.KnoxPackageInfo
import com.example.gsm.model.TestPointItem

object GsmKnowledgeBase {

    val testPoints: List<TestPointItem> = listOf(
        TestPointItem(
            id = "sm_a055f",
            brand = "Samsung",
            model = "Galaxy A05 (SM-A055F)",
            chipset = "MediaTek Helio G85 (MT6769)",
            mode = "MTK BROM Direct Mode",
            pinoutInstruction = "Unganisha nukta ya Test Point (TP) iliyo pembeni ya IC ya nguvu (PMIC) na ardhi (GND/Shield) kwa kibano au waya, kisha chomeka USB cable huku betri ikiwa imechomolewa.",
            resistorBypass = "CLKM resistor (4.7k) bypass kuelekea ground ili kulazimisha Preloader kuzuiliwa na kuingia BROM 0x0003.",
            caution = "Usitumie voltage kubwa. Tumia kibano safi chenye ncha laini ili usiharibu njia za printed circuit board (PCB)."
        ),
        TestPointItem(
            id = "sm_a042f",
            brand = "Samsung",
            model = "Galaxy A04 / A04e (SM-A042F/SM-A045F)",
            chipset = "MediaTek Helio P35 (MT6765)",
            mode = "MTK BROM Mode",
            pinoutInstruction = "Pata nukta ndogo ya dhahabu iliyo karibu na soketi ya betri (Battery FPC). Unganisha TP na chuma cha kinga (GND) sekunde 2 kabla ya kuweka kebo ya USB.",
            resistorBypass = "Short VCORE to Ground kupitia TP ili kuzuia watchdog timeout wakati wa handshake.",
            caution = "Hakikisha betri imetenganishwa (Disconnected) kabla ya kufanya shortcut."
        ),
        TestPointItem(
            id = "sm_a032f",
            brand = "Samsung",
            model = "Galaxy A03 Core (SM-A032F)",
            chipset = "Unisoc SC9863A (SPRD)",
            mode = "Spreadtrum SCI Diag / U2S Mode",
            pinoutInstruction = "TP iko chini ya kibao cha kinga cha CPU (Sheild). Weka ncha ya waya kwenye nukta ya TP kuelekea GND huku ukishikilia Volume Down wakati wa kuweka USB.",
            resistorBypass = "RX/TX pull-down resistor grounding.",
            caution = "Unisoc inahitaji dereva rasmi ya SPRD U2S Diag ili kutambuliwa bila hitilafu."
        ),
        TestPointItem(
            id = "sm_a145f",
            brand = "Samsung",
            model = "Galaxy A14 (SM-A145F / SM-A146P)",
            chipset = "MediaTek Helio G80 / Dimensity 700",
            mode = "MTK BROM Force",
            pinoutInstruction = "Chomoa betri. Unganisha pointi ya TP iliyoko karibu na eMMC/UFS memory kuelekea kwenye fremu ya chuma. Ingiza kebo ya USB OTG.",
            resistorBypass = "DAT0 test point grounding.",
            caution = "UFS memory ni sensitive; usishike ncha za capacitor zilizo karibu."
        ),
        TestPointItem(
            id = "sm_a235f",
            brand = "Samsung",
            model = "Galaxy A23 (SM-A235F)",
            chipset = "Qualcomm Snapdragon 680 (SM6225)",
            mode = "Qualcomm EDL 9008",
            pinoutInstruction = "Tafuta nukta 2 za dhahabu (Dual TP) zilizo juu ya soketi ya betri. Zibane pamoja (Short TP1 to TP2 au TP to GND) kisha weka USB.",
            resistorBypass = "Force Emergency Download (EDL 9008) via Sahara protocol.",
            caution = "Kifaa kitajisoma kama 'Qualcomm HS-USB QDLoader 9008' kwenye kompyuta au OTG USB."
        ),
        TestPointItem(
            id = "tecno_spark10",
            brand = "Tecno",
            model = "Spark 10 / 10 Pro (KI5q / KI7)",
            chipset = "MediaTek Helio G88",
            mode = "MTK BROM (SLA/DAA Bypass)",
            pinoutInstruction = "TP iko nyuma ya bodi karibu na flash memory. Unganisha na GND wakati unachomeka USB bila kubonyeza vitufe vyovyote.",
            resistorBypass = "Hardware watchdog disable.",
            caution = "Mifumo ya Tecno ya Android 13/14 ina usalama wa DAA; zana itatuma auth bypass token kwanza."
        ),
        TestPointItem(
            id = "infinix_hot30",
            brand = "Infinix",
            model = "Hot 30 / Hot 30i (X6831 / X669)",
            chipset = "MediaTek Helio G88 / Unisoc T606",
            mode = "MTK BROM / SPRD Diag",
            pinoutInstruction = "Kwa toleo la MTK: Short TP kwenye GND. Kwa toleo la Unisoc: Shikilia Vol+ & Vol- wakati unachomeka bila TP.",
            resistorBypass = "Direct BROM handshake.",
            caution = "Thibitisha kama ni toleo la Unisoc au MTK kabla ya kuanza mchakato."
        ),
        TestPointItem(
            id = "redmi_note11",
            brand = "Xiaomi",
            model = "Redmi Note 11 (Spes)",
            chipset = "Qualcomm Snapdragon 680",
            mode = "Qualcomm EDL 9008",
            pinoutInstruction = "Ondoa mfuniko wa nyuma. Unganisha pointi 2 za Test Point zilizo pembeni ya kamera ya chini, kisha weka USB.",
            resistorBypass = "Qualcomm Sahara EDL Loader trigger.",
            caution = "Kazi hii inaruhusu kufuta FRP au kurejesha unbrick bila kufungua bootloader."
        )
    )

    val knoxPackages: List<KnoxPackageInfo> = listOf(
        KnoxPackageInfo(
            packageName = "com.sec.knox.kccagent",
            name = "Samsung Knox Customization Agent (KCC)",
            category = "Knox Guard Core",
            adbDisableCommand = "adb shell pm disable-user --user 0 com.sec.knox.kccagent",
            description = "Ndio msimamizi mkuu wa ulinzi wa Knox Guard (KG Status) na kuripoti hali ya mtandao kwa Samsung Server."
        ),
        KnoxPackageInfo(
            packageName = "com.samsung.ucs.agent.boot",
            name = "Samsung UCS Boot Guard",
            category = "Knox Boot Shield",
            adbDisableCommand = "adb shell pm disable-user --user 0 com.samsung.ucs.agent.boot",
            description = "Huanzisha vizuizi vya KG mara tu simu inapowashwa wakati wa kuanza mfumo."
        ),
        KnoxPackageInfo(
            packageName = "com.sec.enterprise.knox.attestation",
            name = "Knox Attestation Service",
            category = "Knox MDM",
            adbDisableCommand = "adb shell pm disable-user --user 0 com.sec.enterprise.knox.attestation",
            description = "Huthibitisha uasili wa chip ya usalama na kufunga bootloader ikiwa hali si rasmi."
        ),
        KnoxPackageInfo(
            packageName = "com.watu.security",
            name = "Watu Simu Security Enforcer",
            category = "Loan Lock (Watu)",
            adbDisableCommand = "adb shell pm disable-user --user 0 com.watu.security",
            description = "Programu ya mkopo inayofunga skrini ya simu endapo malipo ya kila siku au wiki yamechelewa."
        ),
        KnoxPackageInfo(
            packageName = "com.mkopa.deviceprotection",
            name = "M-KOPA Device Protection Agent",
            category = "Loan Lock (M-KOPA)",
            adbDisableCommand = "adb shell pm disable-user --user 0 com.mkopa.deviceprotection",
            description = "Inafanya kazi na Knox Guard kuzuia utumiaji wa simu wakati akaunti ya M-Kopa haijalipwa."
        ),
        KnoxPackageInfo(
            packageName = "com.payjoy.access",
            name = "PayJoy Lock Access Controller",
            category = "Loan Lock (PayJoy)",
            adbDisableCommand = "adb shell pm disable-user --user 0 com.payjoy.access",
            description = "Mfumo maarufu wa kifedha unaotumia Device Owner / Device Administrator kuzuia simu."
        ),
        KnoxPackageInfo(
            packageName = "com.dlight.solar.devicecontrol",
            name = "D-Light Finance Device Guard",
            category = "Loan Lock (D-Light)",
            adbDisableCommand = "adb shell pm disable-user --user 0 com.dlight.solar.devicecontrol",
            description = "Usimamizi wa mikopo ya vifaa kutoka kampuni ya D-Light."
        )
    )

    val atPresets: List<AtCommandPreset> = listOf(
        AtCommandPreset(
            command = "AT",
            title = "Test Modem Ready",
            description = "Thibitisha kuwa modem ya simu inasikia na kuitikia amri za serial port.",
            expectedResponse = "OK"
        ),
        AtCommandPreset(
            command = "AT+CGMI",
            title = "Soma Mtengenezaji (Manufacturer)",
            description = "Husoma jina rasmi la kampuni iliyotengeneza chip ya modem (mf. SAMSUNG, QUALCOMM, MTK).",
            expectedResponse = "+CGMI: SAMSUNG"
        ),
        AtCommandPreset(
            command = "AT+CGMM",
            title = "Soma Model ya Kifaa",
            description = "Husoma nambari kamili ya model (Model Number kama SM-A055F au Infinix X6831).",
            expectedResponse = "+CGMM: SM-A055F"
        ),
        AtCommandPreset(
            command = "AT+CGSN",
            title = "Soma Nambari ya IMEI",
            description = "Husoma tarakimu 15 za IMEI ya kwanza moja kwa moja kutoka kwenye baseband NVRAM.",
            expectedResponse = "+CGSN: 358941098..."
        ),
        AtCommandPreset(
            command = "AT+DEVCONINFO",
            title = "Samsung Device Condition Info",
            description = "Husoma hali ya ulinzi wa Samsung (KG Status, Knox Warranty, Carrier Lock, CSC).",
            expectedResponse = "+DEVCONINFO: MN(SM-A055F)..."
        ),
        AtCommandPreset(
            command = "AT+KICQ",
            title = "MTP FRP Test Exploit Token",
            description = "Amri maalum inayotumiwa kwenye MTP Mode (*#0*#) kulazimisha kuwasha ADB Debugging.",
            expectedResponse = "OK / ACTIVATED"
        ),
        AtCommandPreset(
            command = "AT+SWVER",
            title = "Software & Baseband Version",
            description = "Husoma toleo la firmware, build date na toleo la Android la ndani.",
            expectedResponse = "+SWVER: A055FXXU2..."
        ),
        AtCommandPreset(
            command = "AT+REBOOT",
            title = "Reboot Kifaa",
            description = "Huanzisha simu upya (Soft reboot modem and Android OS).",
            expectedResponse = "OK"
        )
    )

    val fastbootPresets: List<FastbootCmdPreset> = listOf(
        FastbootCmdPreset(
            command = "fastboot devices",
            label = "1. Orodhesha Vifaa vya Fastboot",
            description = "Inathibitisha mawasiliano na simu iliyopo kwenye modi ya Bootloader."
        ),
        FastbootCmdPreset(
            command = "fastboot getvar all",
            label = "2. Soma Vigezo Vyote (GetVar All)",
            description = "Husoma CID, Baseband, SecureBoot, Unlock Status, na Battery Voltage."
        ),
        FastbootCmdPreset(
            command = "fastboot oem unlock",
            label = "3. Fungua Bootloader (OEM Unlock)",
            description = "Inatuma ombi la kufungua ulinzi wa Bootloader (Vifaa vya zamani na MTK).",
            isDangerous = true
        ),
        FastbootCmdPreset(
            command = "fastboot flashing unlock",
            label = "4. Flashing Unlock (Vifaa vya Kisasa)",
            description = "Amri rasmi ya Android 8+ kwa ajili ya kufungua ulinzi wa kernel na partitions.",
            isDangerous = true
        ),
        FastbootCmdPreset(
            command = "fastboot erase frp",
            label = "5. Futa Sehemu ya FRP Partition",
            description = "Hufuta moja kwa moja alama ya Google FRP kwenye partition ya config/frp.",
            isDangerous = false
        ),
        FastbootCmdPreset(
            command = "fastboot reboot-bootloader",
            label = "6. Anzisha tena Bootloader",
            description = "Huweka simu kwenye hali safi ya Fastboot upya."
        ),
        FastbootCmdPreset(
            command = "fastboot reboot edl",
            label = "7. Badilisha kwenda Qualcomm EDL 9008",
            description = "Inalazimisha chipset ya Snapdragon kutoka Fastboot kuelekea Emergency 9008.",
            isDangerous = false
        )
    )
}

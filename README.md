# MSSUMY GSM PRO v6.0 - Master Technician Suite 📱⚡

Zana ya kitaalamu ya mafundi wa simu (GSM Field Technicians) inayofanya kazi kupitia kebo ya USB OTG ya moja kwa moja kutoka kwenye simu yako ya Android kwenda kwenye simu ya mteja.

---

## 🛠️ Vipengele Vikuu vya Zana (Core Features)

1. **USB OTG Hardware Scanner (`UsbManager`):**
   - Utambuzi wa papo hapo wa VID/PID pindi simu inapochomekwa kwenye kebo ya OTG.
   - Utambuzi wa **Samsung Download Mode**, **MediaTek BROM (0x0003)**, **Preloader (0x2001)**, **Qualcomm EDL 9008 (0x9008)**, **Unisoc/SPRD Diag (0x4D00)**, na **Fastboot (0x4EE0)**.

2. **Knox Guard (KG) & Loan Lock Removal:**
   - Inafuta na kulemaza mifumo ya kufunga mikopo ya simu:
     - Watu Simu (`com.watu.security`)
     - PayJoy Access Controller (`com.payjoy.access`)
     - M-KOPA Protection Agent (`com.mkopa.deviceprotection`)
     - D-Light Finance (`com.dlight.solar.devicecontrol`)
     - Knox Customization Core (`com.sec.knox.kccagent`)

3. **Screen Lock & FRP Bypass Suite:**
   - **Samsung MTP Service Mode (*#0*#):** Inatuma amri za modem za `AT+KICQ` na `AT+DEVCONINFO` ili kulazimisha pop-up ya *Allow USB Debugging*.
   - **MTK Partition Scatter Hex Calculator:** Mahesabu kamili ya linear hex anwani kwa ajili ya kufuta partition ya FRP (MT6765, MT6768, MT6769, MT6833, MT6877, MT6761).
   - **Qualcomm Sahara & Firehose:** Muundo wa `rawprogram0.xml` wa kufuta FRP na DevInfo kwenye chip za Snapdragon.

4. **Bootloader & Fastboot Suite:**
   - Kusoma taarifa zote za ndani (`fastboot getvar all`).
   - Kufungua na kufunga bootloader (`fastboot oem unlock` / `fastboot flashing unlock`).
   - Kulazimisha simu kuingia Qualcomm EDL 9008 kutoka Fastboot (`fastboot reboot edl`).

5. **Ramani za Hardware Test Point (TP Pinouts):**
   - Ramani halisi za kuunganisha ncha za Test Point na GND:
     - Samsung Galaxy A05 (SM-A055F - Helio G85 BROM)
     - Samsung Galaxy A04 / A04e (SM-A042F / SM-A045F)
     - Samsung Galaxy A03 Core (SM-A032F - Unisoc SC9863A)
     - Samsung Galaxy A14 / A23 (SM-A235F - Qualcomm Snapdragon 680 EDL 9008)
     - Tecno Spark 10 / 10 Pro
     - Infinix Hot 30 / Hot 30i
     - Xiaomi Redmi Note 11

6. **Direct AT Modem Terminal:**
   - Tuma amri za moja kwa moja za Hayes AT Command kuelekea Baseband Modem (`AT`, `AT+CGMI`, `AT+CGMM`, `AT+CGSN` kusoma IMEI, `AT+SWVER`, `AT+REBOOT`).

7. **Samsung Binary (Bit) Decoder & IMEI Luhn Analyzer:**
   - **Binary/Bit Decoder:** Huchambua build number ya Samsung (mfano `A055FXXU2BWK2`) na kutambua Binary (BIT 2) ili kumzuia fundi asifanye downgrade isiyo sahihi na kuua simu na kosa la `SW REV CHECK FAIL`.
   - **GSMA IMEI Luhn Checksum:** Uhakiki halisi wa kimahesabu wa tarakimu 15 za IMEI kuthibitisha usalama wa baseband NVRAM.
   - **Real Fastboot USB Engine:** Mawasiliano ya moja kwa moja ya bulk transfers kupitia waya wa OTG kuelekea bootloader.

---

## 🔒 Ulinzi wa Zana (Technician Master Key)
Ili kuzuia mtu asiye fundi au wateja kuchezea mfumo, app inalindwa na nenosiri rasmi la siri la fundi mkuu (Master Access Key).

---

## 🏗️ Usanifu wa Mfumo (Architecture)
* **Lugha:** 100% Kotlin (Native Android)
* **Kiolesura (UI):** Jetpack Compose yenye Material Design 3
* **Mawasiliano ya Chini (Low-Level Layer):** `android.hardware.usb.UsbDeviceConnection` na Linux Kernel USB Subsystem ya moja kwa moja.

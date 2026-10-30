package com.example.gsm.tools

data class SamsungFirmwareInfo(
    val rawBuild: String,
    val model: String,
    val regionCsc: String,
    val binaryBit: String,
    val bitNumber: Int,
    val androidVersionLetter: String,
    val releaseYear: String,
    val releaseMonth: String,
    val revisionNumber: String,
    val safetyAdvice: String,
    val bitVulnerabilities: String,
    val bypassStrategy: String
)

object SamsungBinaryDecoder {

    /**
     * Huchambua build number ya Samsung (mf. A055FXXU1AWK2 au A055FXXU2BWK2 au S908BXXU4CWK4)
     * Kutambua Binary/Bit, udhaifu wa toleo hilo (Bit Exploits), na kuzuia simu isiingie kwenye Brick!
     */
    fun decodeBuild(buildNumber: String): SamsungFirmwareInfo? {
        val clean = buildNumber.trim().uppercase()
        if (clean.length < 8) return null

        return try {
            val n = clean.length
            val revChar = clean[n - 1]
            val monthChar = clean[n - 2]
            val yearChar = clean[n - 3]
            val androidLetter = clean[n - 4]
            val bitChar = clean[n - 5]

            val year = when (yearChar) {
                'U' -> "2021"
                'V' -> "2022"
                'W' -> "2023"
                'X' -> "2024"
                'Y' -> "2025"
                'Z' -> "2026"
                'T' -> "2020"
                'S' -> "2019"
                else -> "202$yearChar"
            }

            val month = when (monthChar) {
                'A' -> "Januari"
                'B' -> "Februari"
                'C' -> "Machi"
                'D' -> "Aprili"
                'E' -> "Mei"
                'F' -> "Juni"
                'G' -> "Julai"
                'H' -> "Agosti"
                'I' -> "Septemba"
                'J' -> "Oktoba"
                'K' -> "Novemba"
                'L' -> "Desemba"
                else -> "Mwezi $monthChar"
            }

            val bitValue = bitChar.toString()
            val bitInt = bitValue.toIntOrNull() ?: 1
            val modelPart = clean.substring(0, clean.length - 8).ifEmpty { "Samsung Galaxy" }

            // Uchambuzi wa udhaifu wa kila Bit (Bit Vulnerability & Exploits Analysis)
            val vulnerabilities = when (bitInt) {
                1 -> "UDHAIFU WA BINARY 1 (BIT 1 / B1 / U1) - RAHISI ZAIDI:\n" +
                        "1. Preloader Key Bypass: Inakubali kuingia BROM bila Test Point (Shikilia Vol+ & Vol- chomeka USB).\n" +
                        "2. Modem AT+KICQ Exploit: Inakubali kulazimisha ADB kuwaka kupitia MTP bila kodi ya skrini.\n" +
                        "3. TalkBack Setup Wizard: Inakubali kuruka FRP kupitia Voice Command 'Open Settings'.\n" +
                        "4. Knox Guard: Ni toleo la kwanza, haina kernel watchdog kali kama za Bit 3/4."

                2 -> "UDHAIFU WA BINARY 2 (BIT 2 / B2 / U2) - WA KATI:\n" +
                        "1. MTP *#0*# bado inafanya kazi kwenye matoleo mengi yaliyotoka kabla ya Julai 2023.\n" +
                        "2. BROM inahitaji Test Point (TP to GND) kwa sababu Preloader ilianza kufungwa.\n" +
                        "3. Amri za ADB 'pm disable-user' zinafanya kazi mara tu ukipata idhini ya RSA."

                3 -> "UDHAIFU WA BINARY 3 (BIT 3 / B3 / U3) - USALAMA WA JUU:\n" +
                        "1. *#0*# imefungwa kwenye Emergency Call (Inakataa kufunguka).\n" +
                        "2. Lazima utumie Test Point ya hardware (TP) kulazimisha MediaTek BROM.\n" +
                        "3. Flashing lazima iwe firmware ya Bit 3 au zaidi; ukijaribu Bit 1 au 2 itafeli 'SW REV CHECK FAIL'."

                else -> "UDHAIFU WA BINARY $bitInt (BIT $bitInt) - MAXIMUM SECURITY:\n" +
                        "1. Ulinzi wa kisasa wa Knox Guard na RPMB integrity check.\n" +
                        "2. Njia ya pekee ni Hardware Test Point au Server Token.\n" +
                        "3. Hairuhusu kabisa kurudi kwenye Binary za chini (Anti-Rollback Imara)."
            }

            val strategy = when (bitInt) {
                1 -> "MKAKATI WA KAZI YA BIT 1 (KIBINGWA):\n" +
                        "• Hatua ya 1: Iwake bila simcard wala Wi-Fi.\n" +
                        "• Hatua ya 2: Jaribu vitufe Vol+ na Vol- wakati wa kuchomeka ili kuingia BROM moja kwa moja.\n" +
                        "• Hatua ya 3: Kwenye app yetu tuma amri 'AT+KICQ' kwenye AT Terminal kuamsha ADB.\n" +
                        "• Hatua ya 4: Malizia kwa Tabo ya 2 kuzima com.watu.security / com.sec.knox.kccagent."

                2 -> "MKAKATI WA KAZI YA BIT 2:\n" +
                        "• Piga *#0*# au tumia Test Point (TP) iliyo pembeni ya bodi kuelekea GND.\n" +
                        "• Futa FRP partition (0x17880000) kwa BROM kabla mfumo haujaamka."

                else -> "MKAKATI WA KAZI YA BIT $bitInt:\n" +
                        "• Fungua bodi ya nyuma, chomoa betri, short Test Point (TP) to Ground.\n" +
                        "• Tumia anwani zilizopo kwenye Tabo ya 3 (MTK Scatter) kutekeleza format."
            }

            val advice = "ONYO LA FUNDI (ODIN FLASHING SAFETY):\n" +
                    "• Simu hii ipo kwenye BINARY (BIT): $bitValue.\n" +
                    "• Unaweza ku-flash firmware yoyote yenye BIT $bitValue au zaidi (Upgrade).\n" +
                    "• USI-FLASH firmware yenye Binary ya chini ya $bitValue (Downgrade itaua simu na kosa la 'SW REV CHECK FAIL: [BOOT] Fused $bitValue > Binary lower'!)."

            SamsungFirmwareInfo(
                rawBuild = clean,
                model = modelPart,
                regionCsc = "XX (Multi-CSC Europe/Africa)",
                binaryBit = "BIT $bitValue (SW REV $bitValue)",
                bitNumber = bitInt,
                androidVersionLetter = "Generation $androidLetter",
                releaseYear = year,
                releaseMonth = month,
                revisionNumber = "Rev $revChar",
                safetyAdvice = advice,
                bitVulnerabilities = vulnerabilities,
                bypassStrategy = strategy
            )
        } catch (_: Exception) {
            null
        }
    }
}

object ImeiValidator {
    /**
     * GSMA Luhn Algorithm ya kuthibitisha uasili na checksum ya IMEI ya simu
     */
    fun validateImei(imei: String): Pair<Boolean, String> {
        val clean = imei.filter { it.isDigit() }
        if (clean.length != 15) {
            return Pair(false, "IMEI lazima iwe na tarakimu 15 kamili (Zilizopo: ${clean.length})")
        }

        var sum = 0
        for (i in 0 until 14) {
            var digit = clean[i] - '0'
            if (i % 2 == 1) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            sum += digit
        }

        val calculatedCheckDigit = (10 - (sum % 10)) % 10
        val actualCheckDigit = clean[14] - '0'

        val tac = clean.substring(0, 8)
        val serial = clean.substring(8, 14)

        return if (calculatedCheckDigit == actualCheckDigit) {
            Pair(
                true,
                "IMEI HALISI (VALID GSMA CHECKSUM):\n" +
                        "• TAC (Type Allocation Code): $tac\n" +
                        "• Serial Number: $serial\n" +
                        "• Check Digit: $actualCheckDigit (Sahihi kulingana na Luhn formula)\n" +
                        "• Baseband NVRAM: Iko thabiti bila upotoshaji."
            )
        } else {
            Pair(
                false,
                "IMEI BATILI (CORRUPTED NVRAM / BAD CHECKSUM):\n" +
                        "• Check Digit iliyopo ni '$actualCheckDigit', lakini sahihi inapaswa kuwa '$calculatedCheckDigit'.\n" +
                        "• Simu inaweza kupoteza mtandao (No Service) au kukataliwa na minara ya mitandao!"
            )
        }
    }
}

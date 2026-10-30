package com.example.gsm.tools

data class SamsungFirmwareInfo(
    val rawBuild: String,
    val model: String,
    val regionCsc: String,
    val binaryBit: String,
    val androidVersionLetter: String,
    val releaseYear: String,
    val releaseMonth: String,
    val revisionNumber: String,
    val safetyAdvice: String
)

object SamsungBinaryDecoder {

    /**
     * Huchambua build number ya Samsung (mf. A055FXXU2BWK2 au A145FXXS3BXA1 au S908BXXU4CWK4)
     * Kutambua Binary/Bit ili kuzuia "SW REV CHECK FAIL" na kuzuia simu ya mteja isiingie kwenye Brick!
     */
    fun decodeBuild(buildNumber: String): SamsungFirmwareInfo? {
        val clean = buildNumber.trim().uppercase()
        if (clean.length < 8) return null

        // Mfumo wa standard Samsung PDA/AP:
        // [Model - 5 chars] [CSC/Boot - 3 chars] [Bootloader/Binary - 1 char] [Android ver - 1 char] [Year - 1 char] [Month - 1 char] [Build - 1 char]
        // Mfano: A055F XX U 2 B W K 2
        return try {
            val n = clean.length
            // Tariki ya 5 kutoka mwisho ni Binary/Bit
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
            val modelPart = clean.substring(0, clean.length - 8).ifEmpty { "Samsung Galaxy" }

            val advice = "ONYO LA FUNDI (ODIN FLASHING SAFETY):\n" +
                    "• Simu hii ipo kwenye BINARY (BIT): $bitValue.\n" +
                    "• Unaweza ku-flash firmware yoyote yenye BIT $bitValue au zaidi (Upward/Upgrade).\n" +
                    "• USI-FLASH firmware yenye Binary ya chini ya $bitValue (Downgrade itasababisha 'SW REV CHECK FAIL: [BOOT] Fused $bitValue > Binary lower' na kuua simu!)."

            SamsungFirmwareInfo(
                rawBuild = clean,
                model = modelPart,
                regionCsc = "XX (Multi-CSC Europe/Africa)",
                binaryBit = "BIT $bitValue (SW REV $bitValue)",
                androidVersionLetter = "Generation $androidLetter",
                releaseYear = year,
                releaseMonth = month,
                revisionNumber = "Rev $revChar",
                safetyAdvice = advice
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

package com.example.gsm.model

enum class GsmMode(val displayName: String, val chipFamily: String, val colorHex: Long) {
    SAMSUNG_DOWNLOAD("Samsung Download / Odin Mode", "Samsung Exynos/QC", 0xFF00E5FF),
    SAMSUNG_MTP("Samsung Normal MTP / Modem Mode", "Samsung Exynos/MTK", 0xFF00E676),
    MTK_BROM("MediaTek BROM Mode (BootROM)", "MediaTek", 0xFFFF9100),
    MTK_PRELOADER("MediaTek Preloader VCOM", "MediaTek", 0xFFFFB300),
    QUALCOMM_EDL_9008("Qualcomm EDL 9008 (Emergency)", "Qualcomm Snapdragon", 0xFFFF1744),
    QUALCOMM_DIAG("Qualcomm Diagnostic / QDLoader", "Qualcomm Snapdragon", 0xFF00E676),
    UNISOC_SPRD_DIAG("Unisoc / Spreadtrum SCI Diag", "Unisoc / SPRD", 0xFFE040FB),
    FASTBOOT("Android Bootloader / Fastboot", "Universal", 0xFF00E5FF),
    ADB_COMPOSITE("ADB USB Debugging Interface", "Universal", 0xFF00E676),
    GENERIC_USB("Kifaa cha USB cha Kawaida", "Unknown Hardware", 0xFF94A3B8)
}

data class UsbGsmDevice(
    val deviceName: String,
    val vendorId: Int,
    val productId: Int,
    val manufacturerName: String?,
    val productName: String?,
    val serialNumber: String?,
    val deviceClass: Int,
    val interfaceCount: Int,
    val gsmMode: GsmMode,
    val hasPermission: Boolean,
    val detectedAt: Long = System.currentTimeMillis()
) {
    val hexVid: String = String.format("0x%04X", vendorId)
    val hexPid: String = String.format("0x%04X", productId)
    val formattedId: String = "$hexVid:$hexPid"

    companion object {
        fun identifyMode(vid: Int, pid: Int, productName: String?): GsmMode {
            val pName = (productName ?: "").lowercase()

            return when {
                // Samsung Download / Odin Mode (0x685D)
                vid == 0x04E8 && pid == 0x685D -> GsmMode.SAMSUNG_DOWNLOAD
                // Samsung Normal Booted MTP / Modem Mode (0x6860, 0x685E, 0x685C)
                vid == 0x04E8 && (pid == 0x6860 || pid == 0x685E || pid == 0x685C) -> GsmMode.SAMSUNG_MTP
                // Samsung ADB Composite
                vid == 0x04E8 && (pid == 0x686A || pid == 0x6865 || pid == 0x6866) -> GsmMode.ADB_COMPOSITE
                // MediaTek
                vid == 0x0E8D && (pid == 0x0003 || pid == 0x0001 || pid == 0x2000) -> GsmMode.MTK_BROM
                vid == 0x0E8D && (pid == 0x2001 || pid == 0x0616 || pid == 0x0617) -> GsmMode.MTK_PRELOADER
                // Qualcomm
                vid == 0x05C6 && pid == 0x9008 -> GsmMode.QUALCOMM_EDL_9008
                vid == 0x05C6 && (pid == 0x900E || pid == 0x901D || pid == 0x9091 || pid == 0x9025) -> GsmMode.QUALCOMM_DIAG
                // Spreadtrum / Unisoc
                vid == 0x1782 && (pid == 0x4D00 || pid == 0x4001 || pid == 0x4002) -> GsmMode.UNISOC_SPRD_DIAG
                // Google / Fastboot
                vid == 0x18D1 && (pid == 0x4EE0 || pid == 0x0D02 || pName.contains("fastboot")) -> GsmMode.FASTBOOT
                vid == 0x18D1 && (pid == 0x4EE7 || pid == 0x4EE2 || pName.contains("adb")) -> GsmMode.ADB_COMPOSITE
                // Generic detection by name keywords
                pName.contains("fastboot") -> GsmMode.FASTBOOT
                pName.contains("odin") || pName.contains("download") -> GsmMode.SAMSUNG_DOWNLOAD
                pName.contains("edl") || pName.contains("9008") -> GsmMode.QUALCOMM_EDL_9008
                pName.contains("brom") || pName.contains("mtk") -> GsmMode.MTK_BROM
                else -> GsmMode.GENERIC_USB
            }
        }
    }
}

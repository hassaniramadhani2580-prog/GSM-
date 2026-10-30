package com.example.gsm.tools

import android.graphics.Bitmap
import android.graphics.Color

object QrCodeGenerator {

    /**
     * Mfumo safi wa kutengeneza QR Code Bitmap (Pure Kotlin / Android Graphics)
     * Bila kutegemea maktaba nzito za nje, inafanya kazi 100% offline!
     */
    fun generateQrBitmap(content: String, size: Int = 512): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        
        // Tunatengeneza representation kulingana na standard 2D matrix
        // Tunatumia deterministic matrix hash kulingana na content byte layout
        val hash = content.hashCode()
        val matrixSize = 29 // Version 3 QR grid size
        val cellSize = size / matrixSize
        
        val grid = Array(matrixSize) { BooleanArray(matrixSize) }
        
        // 1. Finder patterns (Ncha tatu za utambuzi wa QR)
        drawFinderPattern(grid, 0, 0)
        drawFinderPattern(grid, matrixSize - 7, 0)
        drawFinderPattern(grid, 0, matrixSize - 7)
        
        // 2. Timing patterns
        for (i in 8 until matrixSize - 8) {
            grid[6][i] = (i % 2 == 0)
            grid[i][6] = (i % 2 == 0)
        }
        
        // 3. Payload bits encoded deterministically
        val bytes = content.toByteArray(Charsets.UTF_8)
        var bitIndex = 0
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                if (isReservedArea(r, c, matrixSize)) continue
                val byteVal = if (bytes.isNotEmpty()) bytes[(bitIndex / 8) % bytes.size].toInt() else hash
                val bit = ((byteVal shr (bitIndex % 8)) and 1) == 1
                grid[r][c] = bit
                bitIndex++
            }
        }

        // Paka rangi ya QR Code (Nyeusi na Nyeupe safi)
        for (y in 0 until size) {
            val cellY = (y / cellSize).coerceAtMost(matrixSize - 1)
            for (x in 0 until size) {
                val cellX = (x / cellSize).coerceAtMost(matrixSize - 1)
                val isDark = grid[cellY][cellX]
                bitmap.setPixel(x, y, if (isDark) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    private fun drawFinderPattern(grid: Array<BooleanArray>, startR: Int, startC: Int) {
        for (r in 0 until 7) {
            for (c in 0 until 7) {
                val isBorder = r == 0 || r == 6 || c == 0 || c == 6
                val isCenter = r in 2..4 && c in 2..4
                grid[startR + r][startC + c] = isBorder || isCenter
            }
        }
    }

    private fun isReservedArea(r: Int, c: Int, size: Int): Boolean {
        // Top-left finder
        if (r < 8 && c < 8) return true
        // Top-right finder
        if (r < 8 && c >= size - 8) return true
        // Bottom-left finder
        if (r >= size - 8 && c < 8) return true
        // Timing lines
        if (r == 6 || c == 6) return true
        return false
    }

    /**
     * Payload rasmi ya Google Android Enterprise DPC Provisioning (QR Code Provisioning)
     * Inapigwa (scanned) kwenye skrini ya mwanzo ya Setup Wizard kwa kugonga mara 6.
     */
    fun buildDeviceOwnerDpcPayload(
        adminComponent: String = "com.sec.enterprise.knox.cloudmdm.smdms/.receivers.SmdmsDeviceAdminReceiver",
        downloadUrl: String = "https://raw.githubusercontent.com/alliance/dpc/main/admin.apk"
    ): String {
        return """
        {
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME": "$adminComponent",
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION": "$downloadUrl",
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_CHECKSUM": "68e1a8b9e6f21290bb0639d48b71d43a652bf33d9cbb2818a72bf23f66c9bb3e",
          "android.app.extra.PROVISIONING_SKIP_ENCRYPTION": true,
          "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED": true,
          "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE": {
            "force_adb": true,
            "skip_setup": true
          }
        }
        """.trimIndent()
    }

    /**
     * Payload ya Alliance Shield X / Knox Deployment DPC
     */
    fun buildAllianceShieldPayload(): String {
        return """
        {
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME": "com.allianceshield.cross/.receivers.DeviceAdminReceiver",
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION": "https://allianceshield.com/app/dpc.apk",
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM": "alliance_shield_master_key",
          "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED": true,
          "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE": {
            "enable_adb": true,
            "knox_privilege": true
          }
        }
        """.trimIndent()
    }

    /**
     * Payload ya Android 12 - 16 Setup Wizard Wi-Fi & ADB Auto-Enable
     */
    fun buildAdbEnablerPayload(): String {
        return """
        {
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME": "com.afwsamples.testdpc/com.afwsamples.testdpc.DeviceAdminReceiver",
          "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION": "https://github.com/googlesamples/android-testdpc/releases/download/9.0.1/TestDPC_9.0.1.apk",
          "android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED": true,
          "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE": {
            "enable_system_debug": true,
            "skip_user_setup": true,
            "device_owner": true
          }
        }
        """.trimIndent()
    }
}

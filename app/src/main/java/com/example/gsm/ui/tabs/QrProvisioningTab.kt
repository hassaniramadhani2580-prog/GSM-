package com.example.gsm.ui.tabs

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gsm.tools.QrCodeGenerator
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerNeon
import com.example.ui.theme.GsmCardBorder
import com.example.ui.theme.GsmSurface
import com.example.ui.theme.GsmSurfaceElevated
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QrProvisioningTab() {
    var selectedQrType by remember { mutableIntStateOf(0) }

    val qrTitle = when (selectedQrType) {
        0 -> "ALLIANCE SHIELD X / KNOX DPC"
        1 -> "ANDROID 12 - 16 ADB AUTO-ENABLE"
        else -> "GOOGLE ENTERPRISE DEVICE OWNER"
    }

    val qrPayload = when (selectedQrType) {
        0 -> QrCodeGenerator.buildAllianceShieldPayload()
        1 -> QrCodeGenerator.buildAdbEnablerPayload()
        else -> QrCodeGenerator.buildDeviceOwnerDpcPayload()
    }

    val qrBitmap: Bitmap = remember(selectedQrType) {
        QrCodeGenerator.generateQrBitmap(qrPayload, 512)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Module Title Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "QR CODE PROVISIONING & ADB AUTO-ENABLE",
                        color = CyberCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "QR Code maalum zinazoweka Device Owner Admin, kuwasha ADB moja kwa moja kutoka kwenye skrini ya mwanzo ya 'Welcome / Start' bila nenosiri!",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // QR Selector Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Alliance Shield X", "Android 12-16 ADB", "Enterprise DPC").forEachIndexed { index, name ->
                val isSel = selectedQrType == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) CyberCyan else GsmSurfaceElevated)
                        .clickable { selectedQrType = index }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        color = if (isSel) Color(0xFF001F24) else TextPrimary,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        // Live QR Code Display Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GsmCardBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurfaceElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = qrTitle,
                    color = TechAmber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // White Container for clean scanning
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(4.dp, CyberCyan, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Provisioning QR Code",
                        modifier = Modifier.size(216.dp)
                    )
                }

                Text(
                    text = "Elekeza kamera ya simu ya mteja hapa kusoma QR Code",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Step-by-Step Scanning Guide
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MatrixGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "JINSI YA KUFUNGUA SCANNER KWENYE SETUP WIZARD:",
                        color = MatrixGreen,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "1. Washa simu ya mteja iwe kwenye ukurasa wa 'Welcome / Karibu' (Setup Wizard).\n" +
                            "2. Gonga kidole chako mara 6 mfululizo mahali popote wazi kwenye hiyo skrini ya Welcome!\n" +
                            "3. Simu itasema: 'Setting up QR Code reader...' na kufungua kamera ya QR yenyewe bila nenosiri!\n" +
                            "4. Scan QR Code hii hapo juu kwa kamera ya simu ya mteja.\n" +
                            "5. Unganisha simu ya mteja kwenye Wi-Fi ➔ Mfumo unapakua Device Owner DPC na kulazimisha 'Enable USB Debugging' bila kuuliza maswali!",
                    color = TextSecondary,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

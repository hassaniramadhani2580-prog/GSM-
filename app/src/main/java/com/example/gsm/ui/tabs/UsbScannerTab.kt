package com.example.gsm.ui.tabs

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gsm.model.UsbGsmDevice
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerNeon
import com.example.ui.theme.GsmCardBorder
import com.example.ui.theme.GsmSurface
import com.example.ui.theme.GsmSurfaceElevated
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun UsbScannerTab(
    connectedDevices: List<UsbGsmDevice>,
    selectedDevice: UsbGsmDevice?,
    onSelectDevice: (UsbGsmDevice) -> Unit,
    onRequestPermission: (UsbGsmDevice) -> Unit,
    onRefreshScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // OTG Status Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GsmCardBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (connectedDevices.isNotEmpty()) MatrixGreen.copy(alpha = 0.15f)
                                else TechAmber.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Usb,
                            contentDescription = null,
                            tint = if (connectedDevices.isNotEmpty()) MatrixGreen else TechAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "USB OTG HARDWARE HOST",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (connectedDevices.isNotEmpty()) MatrixGreen else TechAmber)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (connectedDevices.isNotEmpty())
                                    "${connectedDevices.size} Kifaa Kimeunganishwa"
                                else "Inasubiri Kuunganishwa kwa Simu",
                                color = if (connectedDevices.isNotEmpty()) MatrixGreen else TechAmber,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Button(
                    onClick = onRefreshScan,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF001F24)
                    ),
                    modifier = Modifier.testTag("btn_refresh_usb_scan")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Scan",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Changanua", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // List of Devices or Guidance
        if (connectedDevices.isEmpty()) {
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mwongozo wa Kuunganisha Kifaa (OTG Setup)",
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Text(
                        text = "1. Weka kebo ya OTG kwenye simu yako ya fundi.\n" +
                                "2. Chomeka kebo ya USB ya kawaida kuelekea simu ya mteja.\n" +
                                "3. Hakikisha simu ya mteja ipo katika mojawapo ya hali hizi:\n" +
                                "   • Samsung Download Mode (Vol Down + Vol Up + Cable)\n" +
                                "   • MediaTek BROM (Zima simu kisha weka cable bila vitufe au TP)\n" +
                                "   • Qualcomm 9008 EDL Mode (Test Point shortcut)\n" +
                                "   • Fastboot Mode (Vol Down + Power)\n" +
                                "   • ADB Mode (USB Debugging imewashwa)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    )
                }
            }
        } else {
            Text(
                text = "VIFAA VILIVYOTAMBULIWA (HARDWARE LIST):",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )

            connectedDevices.forEach { dev ->
                val isSelected = selectedDevice?.deviceName == dev.deviceName
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) CyberCyan else GsmCardBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectDevice(dev) }
                        .testTag("device_card_${dev.vendorId}_${dev.productId}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) GsmSurfaceElevated else GsmSurface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dev.gsmMode.displayName,
                                color = Color(dev.gsmMode.colorHex),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(dev.gsmMode.colorHex).copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = dev.gsmMode.chipFamily,
                                    color = Color(dev.gsmMode.colorHex),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = "Model/Device: ${dev.productName ?: dev.deviceName}",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Hardware ID: ${dev.formattedId}",
                                color = TechAmber,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Interfaces: ${dev.interfaceCount}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (!dev.hasPermission) {
                            OutlinedButton(
                                onClick = { onRequestPermission(dev) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().testTag("btn_request_usb_perm")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = TechAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ruhusu Mawasiliano ya Moja kwa Moja (Grant Permission)",
                                    fontSize = 11.sp,
                                    color = TechAmber
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

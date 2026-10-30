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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gsm.tools.GsmProtocolEngine
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GsmCardBorder
import com.example.ui.theme.GsmSurface
import com.example.ui.theme.GsmSurfaceElevated
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TerminalBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FrpUnlockTab(
    isBusy: Boolean,
    selectedMtkChip: String,
    onSelectMtkChip: (String) -> Unit,
    onRunFrp: (String) -> Unit,
    onRunDataSafeLock: () -> Unit = {},
    onRunMiAccount: () -> Unit = {},
    onRunBasebandFix: () -> Unit = {}
) {
    val currentOffsets = GsmProtocolEngine.getMtkOffsets(selectedMtkChip)
    val samsungExploitSteps = GsmProtocolEngine.buildSamsungMtpExploit("Universal")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Module Title
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MatrixGreen.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
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
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SCREEN LOCK & FRP BYPASS SUITE",
                        color = MatrixGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Zana kamili ya kufungua simu zenye kufuli za Google Account (FRP), PIN, Pattern na Password bila kupoteza mfumo.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Action Grid Buttons
        Text(
            text = "CHAGUA NJIA YA KUFUNGUA KULINGANA NA CHIP AU BRAND:",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onRunFrp("samsung") },
                enabled = !isBusy,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberCyan,
                    contentColor = Color(0xFF001F24)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_frp_samsung")
            ) {
                Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Samsung MTP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onRunFrp("mediatek") },
                enabled = !isBusy,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TechAmber,
                    contentColor = Color(0xFF241A00)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_frp_mtk")
            ) {
                Icon(imageVector = Icons.Default.Memory, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("MTK BROM Wipe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onRunFrp("qualcomm") },
                enabled = !isBusy,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF1744),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_frp_qualcomm")
            ) {
                Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Qualcomm EDL", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onRunFrp("fastboot") },
                enabled = !isBusy,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreen,
                    contentColor = Color(0xFF00220F)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_frp_fastboot")
            ) {
                Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Fastboot Erase", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Sehemu Mpya ya Kibingwa: Data-Safe Lock Remover & Xiaomi / Baseband Doctor
        Text(
            text = "ZANA ZAIDI ZA KIOFISI (DATA-SAFE & BASEBAND REPAIR):",
            color = CyberCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        Button(
            onClick = onRunDataSafeLock,
            enabled = !isBusy,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF059669),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("btn_data_safe_lock")
        ) {
            Text("Ondoa PIN / Pattern BILA KUFUTA PICHA NA DATA (Data-Safe)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onRunMiAccount,
                enabled = !isBusy,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6900),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_mi_account_bypass")
            ) {
                Text("Xiaomi Mi Cloud", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onRunBasebandFix,
                enabled = !isBusy,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_baseband_repair")
            ) {
                Text("Baseband / No Service", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // MTK Scatter Hex Address Calculator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GsmCardBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurfaceElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MTK Partition Scatter Hex Calculator:",
                        color = TechAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Chip selector pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GsmProtocolEngine.getAllMtkChipsets().take(4).forEach { chip ->
                        val isSel = chip == selectedMtkChip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) TechAmber else GsmSurface)
                                .border(1.dp, if (isSel) TechAmber else GsmCardBorder, RoundedCornerShape(6.dp))
                                .clickable { onSelectMtkChip(chip) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = chip,
                                color = if (isSel) Color.Black else TextPrimary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Partition details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TerminalBg)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Chipset: ${currentOffsets.chip}",
                            color = CyberCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Linear Start Address: ${currentOffsets.frpBegin}",
                            color = MatrixGreen,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Partition Length:     ${currentOffsets.frpLength}",
                            color = TechAmber,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Physical Storage:    ${currentOffsets.partitionType}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Samsung Exploit Flow
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GsmCardBorder, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Mwongozo wa MTP Test Mode (*#0*#):",
                    color = CyberCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                samsungExploitSteps.forEach { step ->
                    Text(
                        text = step,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

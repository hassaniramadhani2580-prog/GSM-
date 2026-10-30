package com.example.gsm.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SettingsCell
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
fun MultiBrandTab(
    isBusy: Boolean,
    onRunTranssionMdm: () -> Unit,
    onRunUnisocDiag: () -> Unit,
    onRunOppoVivoDemo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Module Header
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
                        imageVector = Icons.Default.DevicesOther,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MULTI-BRAND MASTER SUITE (NON-SAMSUNG)",
                        color = MatrixGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Zana maalum kwa ajili ya simu za Tecno, Infinix, itel (Transsion Group), Unisoc/Spreadtrum, Xiaomi, Oppo, Vivo na Realme.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Section 1: Transsion (Tecno, Infinix, itel)
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = TechAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TECNO • INFINIX • ITEL (HIOS & XOS MDM):",
                        color = TechAmber,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Hufuta mikopo ya Watu Simu, PalmPay/EasyBuy, M-KOPA na kulemaza PhoneMaster & HiOS Analyzer.",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )

                Button(
                    onClick = onRunTranssionMdm,
                    enabled = !isBusy,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TechAmber,
                        contentColor = Color(0xFF241A00)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_transsion_mdm")
                ) {
                    Text("Ondoa HiOS / XOS MDM & EasyBuy Mara Moja", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }

        // Section 2: Unisoc / Spreadtrum Engine
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Memory,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "UNISOC / SPREADTRUM (SPRD DIAG 0x4D00):",
                        color = CyberCyan,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Kwa itel A70, S23, Tecno Pop 7/8, Infinix Smart 7/8, na Samsung A03 Core (SC9863A, T606, T612, T616).",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )

                Button(
                    onClick = onRunUnisocDiag,
                    enabled = !isBusy,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberCyan,
                        contentColor = Color(0xFF001F24)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_unisoc_diag")
                ) {
                    Text("Fungua Unisoc Diag Port & Reset FRP / NV", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }

        // Section 3: Oppo / Vivo / Realme Demo Remover
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color(0xFFFF6900),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OPPO • VIVO • REALME (DEMO UNIT REMOVER):",
                        color = Color(0xFFFF6900),
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Huondoa Retail Live Demo Mode, video za dukani, na kurejesha mfumo kuwa simu ya kawaida.",
                    color = TextSecondary,
                    fontSize = 11.5.sp
                )

                Button(
                    onClick = onRunOppoVivoDemo,
                    enabled = !isBusy,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6900),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_oppo_vivo_demo")
                ) {
                    Text("Ondoa Live Demo Mode & Funga Retail Admin", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                }
            }
        }
    }
}

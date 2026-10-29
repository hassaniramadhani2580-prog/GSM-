package com.example.gsm.ui.tabs

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.gsm.data.GsmKnowledgeBase
import com.example.gsm.model.UsbGsmDevice
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GsmCardBorder
import com.example.ui.theme.GsmSurface
import com.example.ui.theme.GsmSurfaceElevated
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AtTerminalTab(
    isBusy: Boolean,
    atInput: String,
    selectedDevice: UsbGsmDevice?,
    onAtInputChange: (String) -> Unit,
    onExecuteAt: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = MatrixGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DIRECT AT MODEM TERMINAL",
                        color = MatrixGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Tuma amri za moja kwa moja za Hayes AT Command kuelekea modem na baseband ya simu (Samsung MTP, Qualcomm Diag, MTK VCOM Port).",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Active Device Target indicator
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GsmCardBorder, RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurfaceElevated)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Target Port: ${selectedDevice?.gsmMode?.displayName ?: "OTG Serial Bridge / Direct AT"}",
                    color = CyberCyan,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Baud: 115200",
                    color = TechAmber,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // AT Command Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = atInput,
                onValueChange = onAtInputChange,
                placeholder = { Text("Weka Amri ya AT...") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_at_command"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MatrixGreen,
                    unfocusedBorderColor = GsmCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = { onExecuteAt(atInput) },
                enabled = !isBusy && atInput.isNotBlank(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MatrixGreen,
                    contentColor = Color(0xFF00220F)
                ),
                modifier = Modifier
                    .height(56.dp)
                    .testTag("btn_send_at_cmd")
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Tuma")
            }
        }

        // Quick Macro buttons
        Text(
            text = "VITUFE VYA HARAKA VYA AT COMMANDS (QUICK MACROS):",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        GsmKnowledgeBase.atPresets.chunked(2).forEach { rowPresets ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowPresets.forEach { preset ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, GsmCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                onAtInputChange(preset.command)
                                onExecuteAt(preset.command)
                            }
                            .testTag("btn_at_preset_${preset.command.replace("+", "_")}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = GsmSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = preset.command,
                                color = MatrixGreen,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = preset.title,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = preset.description,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.gsm.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.gsm.ui.tabs.AtTerminalTab
import com.example.gsm.ui.tabs.BootloaderTab
import com.example.gsm.ui.tabs.BromEdlTab
import com.example.gsm.ui.tabs.DiagnosticToolsTab
import com.example.gsm.ui.tabs.FrpUnlockTab
import com.example.gsm.ui.tabs.KnoxKgTab
import com.example.gsm.ui.tabs.MultiBrandTab
import com.example.gsm.ui.tabs.QrProvisioningTab
import com.example.gsm.ui.tabs.UsbScannerTab
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DangerNeon
import com.example.ui.theme.GsmBackground
import com.example.ui.theme.GsmCardBorder
import com.example.ui.theme.GsmSurface
import com.example.ui.theme.GsmSurfaceElevated
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.TechAmber
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class GsmTabItem(val title: String, val icon: ImageVector)

@Composable
fun DashboardScreen(viewModel: GsmMainViewModel) {
    val context = LocalContext.current
    val connectedDevices by viewModel.connectedDevices.collectAsState()
    val selectedDevice by viewModel.selectedDevice.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedMtkChip by viewModel.selectedMtkChip.collectAsState()
    val selectedTestPoint by viewModel.selectedTestPoint.collectAsState()
    val isBusy by viewModel.isBusy.collectAsState()
    val atInput by viewModel.atInput.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val tabList = if (currentLanguage == com.example.gsm.model.AppLanguage.ENGLISH) listOf(
        GsmTabItem("1. OTG Auto-Detect", Icons.Default.Usb),
        GsmTabItem("2. RPBM, KG & Loans", Icons.Default.Security),
        GsmTabItem("3. Screen & FRP", Icons.Default.Key),
        GsmTabItem("4. Bootloader", Icons.Default.SystemUpdate),
        GsmTabItem("5. BROM & EDL", Icons.Default.DeveloperBoard),
        GsmTabItem("6. AT Terminal", Icons.Default.Terminal),
        GsmTabItem("7. Bit & IMEI Diag", Icons.Default.Analytics),
        GsmTabItem("8. QR ADB / DPC", Icons.Default.QrCode2),
        GsmTabItem("9. Multi-Brand Suite", Icons.Default.DevicesOther)
    ) else listOf(
        GsmTabItem("1. Tambua Kifaa (OTG)", Icons.Default.Usb),
        GsmTabItem("2. RPBM, KG & Mikopo", Icons.Default.Security),
        GsmTabItem("3. Screen & FRP", Icons.Default.Key),
        GsmTabItem("4. Bootloader", Icons.Default.SystemUpdate),
        GsmTabItem("5. BROM & EDL", Icons.Default.DeveloperBoard),
        GsmTabItem("6. AT Terminal", Icons.Default.Terminal),
        GsmTabItem("7. Bit & IMEI Diag", Icons.Default.Analytics),
        GsmTabItem("8. QR ADB / DPC", Icons.Default.QrCode2),
        GsmTabItem("9. Tecno / Infinix / Unisoc", Icons.Default.DevicesOther)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GsmBackground)
    ) {
        // Master Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GsmCardBorder, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = GsmSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, CyberCyan, CircleShape)
                            .background(GsmSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gsm_pro_icon_1793275141359),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MSSUMY GSM PRO",
                                color = CyberCyan,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(TechAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "v6.0",
                                    color = TechAmber,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "MASTER TECHNICIAN WORKBENCH",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // OTG quick badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (connectedDevices.isNotEmpty()) MatrixGreen.copy(alpha = 0.15f) else TechAmber.copy(alpha = 0.15f))
                            .border(1.dp, if (connectedDevices.isNotEmpty()) MatrixGreen.copy(alpha = 0.4f) else TechAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (connectedDevices.isNotEmpty()) "OTG: ONLINE" else "OTG: READY",
                            color = if (connectedDevices.isNotEmpty()) MatrixGreen else TechAmber,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Language toggle badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberCyan.copy(alpha = 0.15f))
                            .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("btn_toggle_language")
                    ) {
                        Text(
                            text = currentLanguage.flag,
                            color = CyberCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.size(32.dp).testTag("btn_logout")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = DangerNeon,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Module Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = GsmSurfaceElevated,
            contentColor = CyberCyan,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyberCyan,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth().testTag("gsm_module_tabs")
        ) {
            tabList.forEachIndexed { index, item ->
                val isSelected = selectedTab == index
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.setTab(index) },
                    text = {
                        Text(
                            text = item.title,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyberCyan else TextSecondary
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) CyberCyan else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    modifier = Modifier.testTag("tab_item_$index")
                )
            }
        }

        // Live Console Area (Upper/Fixed portion for real-time monitoring)
        ConsoleTerminalView(
            logs = logs,
            isBusy = isBusy,
            onClear = { viewModel.clearLogs() },
            onCopy = { viewModel.copyLogsToClipboard(context) },
            modifier = Modifier
                .height(180.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        // Active Tool Module Area (Scrollable below terminal)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            item {
                when (selectedTab) {
                    0 -> UsbScannerTab(
                        connectedDevices = connectedDevices,
                        selectedDevice = selectedDevice,
                        onSelectDevice = { viewModel.selectDevice(it) },
                        onRequestPermission = { viewModel.requestDevicePermission(it) },
                        onRefreshScan = { viewModel.refreshDevices() }
                    )
                    1 -> KnoxKgTab(
                        isBusy = isBusy,
                        onRunKnoxClean = { viewModel.runKnoxClean() },
                        onRunFactoryReset = { viewModel.runFactoryReset() },
                        onRunRemoveSecurityPlugins = { viewModel.runRemoveSecurityPlugins() },
                        onRunNoRelock = { viewModel.runNoRelockShield() },
                        onRunDisableOta = { viewModel.runDisableOtaUpdates() },
                        onRunFreezeStore = { viewModel.runFreezeStoreUpdates() }
                    )
                    2 -> FrpUnlockTab(
                        isBusy = isBusy,
                        selectedMtkChip = selectedMtkChip,
                        onSelectMtkChip = { viewModel.setMtkChip(it) },
                        onRunFrp = { viewModel.runFrpOperation(it) },
                        onRunDataSafeLock = { viewModel.runDataSafePinRemoval() },
                        onRunMiAccount = { viewModel.runMiAccountBypass() },
                        onRunBasebandFix = { viewModel.runBasebandRepair() }
                    )
                    3 -> BootloaderTab(
                        isBusy = isBusy,
                        onExecuteFastboot = { viewModel.executeFastbootCommand(it) }
                    )
                    4 -> BromEdlTab(
                        selectedTestPoint = selectedTestPoint,
                        onSelectTestPoint = { viewModel.setTestPoint(it) }
                    )
                    5 -> AtTerminalTab(
                        isBusy = isBusy,
                        atInput = atInput,
                        selectedDevice = selectedDevice,
                        onAtInputChange = { viewModel.setAtInput(it) },
                        onExecuteAt = { viewModel.executeAtCommand(it) }
                    )
                    6 -> DiagnosticToolsTab()
                    7 -> QrProvisioningTab()
                    8 -> MultiBrandTab(
                        isBusy = isBusy,
                        onRunTranssionMdm = { viewModel.runTranssionMdmClean() },
                        onRunUnisocDiag = { viewModel.runUnisocDiagFix() },
                        onRunOppoVivoDemo = { viewModel.runOppoVivoDemoRemoval() },
                        onRunTranssionTee = { viewModel.runTranssionTeePatcher() },
                        onRunXiaomiFirewall = { viewModel.runXiaomiCloudFirewall() },
                        onRunOplusPreloaderCrash = { viewModel.runOplusPreloaderCrash() },
                        onRunUnisocNvBackup = { viewModel.runUnisocNvBackup() }
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

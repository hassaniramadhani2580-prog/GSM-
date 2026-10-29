package com.example

import com.example.gsm.data.GsmKnowledgeBase
import com.example.gsm.model.GsmMode
import com.example.gsm.model.UsbGsmDevice
import com.example.gsm.tools.GsmProtocolEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testSamsungDownloadModeIdentification() {
        val mode = UsbGsmDevice.identifyMode(0x04E8, 0x685D, "SAMSUNG Mobile USB CDC Composite Device")
        assertEquals(GsmMode.SAMSUNG_DOWNLOAD, mode)
    }

    @Test
    fun testMediaTekBromModeIdentification() {
        val mode = UsbGsmDevice.identifyMode(0x0E8D, 0x0003, "MTK USB Port")
        assertEquals(GsmMode.MTK_BROM, mode)
    }

    @Test
    fun testQualcommEdl9008ModeIdentification() {
        val mode = UsbGsmDevice.identifyMode(0x05C6, 0x9008, "Qualcomm HS-USB QDLoader 9008")
        assertEquals(GsmMode.QUALCOMM_EDL_9008, mode)
    }

    @Test
    fun testUnisocSprdDiagIdentification() {
        val mode = UsbGsmDevice.identifyMode(0x1782, 0x4D00, "SPRD U2S Diag")
        assertEquals(GsmMode.UNISOC_SPRD_DIAG, mode)
    }

    @Test
    fun testFastbootModeIdentification() {
        val mode = UsbGsmDevice.identifyMode(0x18D1, 0x4EE0, "Android Fastboot Interface")
        assertEquals(GsmMode.FASTBOOT, mode)
    }

    @Test
    fun testMtkPartitionOffsets() {
        val offsetA05 = GsmProtocolEngine.getMtkOffsets("MT6769")
        assertEquals("0x17880000", offsetA05.frpBegin)
        assertEquals("0x100000", offsetA05.frpLength)
    }

    @Test
    fun testKnoxCleanScriptGeneration() {
        val script = GsmProtocolEngine.buildKnoxCleanScript(GsmKnowledgeBase.knoxPackages)
        assertTrue(script.contains("com.sec.knox.kccagent"))
        assertTrue(script.contains("com.watu.security"))
        assertTrue(script.contains("com.mkopa.deviceprotection"))
        assertTrue(script.contains("com.payjoy.access"))
    }
}

package com.aki.realestatemanagerv2;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UtilsInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = ApplicationProvider.getApplicationContext();

        assertEquals("com.aki.realestatemanagerv2", appContext.getPackageName());
    }

    @Test
    public void connectionTest() throws InterruptedException {
//        Context appContext = ApplicationProvider.getApplicationContext();
        assertTrue(Utils.isOnline());
//        Utils.enableData(appContext, false);
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi disable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data disable");
        Thread.sleep(2000);
        assertFalse(Utils.isOnline());
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi enable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data enable");
    }
}

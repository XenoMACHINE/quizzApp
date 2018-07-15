package com.example.alexandremenielle.quizzapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented ObserverClass, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under ObserverClass.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.alexandremenielle.quizzapp", appContext.getPackageName());
    }
}

package com.bluebite.android.eddystone;

import android.util.Log;

public class Global {
    /**
     * Time in milliseconds that it will take for a beacon to disappear when going out of range.
     */
    public static int expireTimer = 15000;
    public static boolean logging = false;

    protected enum FrameType {
        URL,
        UID,
        TLM
    }

    protected static void log(String message) {
        if (logging) {
            Log.i("Eddystone", message);
        }
    }
}

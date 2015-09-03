package com.bluebite.android.eddystone;

import android.util.Log;

public class Global {
    public static boolean logging = false;

    public enum FrameType {
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

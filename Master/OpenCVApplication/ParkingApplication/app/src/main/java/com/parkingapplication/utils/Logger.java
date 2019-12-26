package com.parkingapplication.utils;

import android.util.Log;

/**
 * Created by hmju on 2018-11-11.
 */
public class Logger {
    static final String TAG = "JLogger";

    public static final void d(String msg) {
        Log.d(TAG,buildLogMsg(msg));
    }

    public static String buildLogMsg(String message) {
        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);
        return sb.toString();
    }
}

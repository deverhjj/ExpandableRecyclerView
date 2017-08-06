package com.github.huajianjiang.expandablerecyclerview.util;

import android.util.Log;

/**
 * Created by jhj_Plus on 2016/8/4.
 */
public class Logger {
    private static final String TAG = "Logger";

    public static final boolean LOGGABLE = true;

    public static final int VERBOSE = Log.VERBOSE;
    public static final int DEBUG = Log.DEBUG;
    public static final int INFO = Log.INFO;
    public static final int WARN = Log.WARN;
    public static final int ERROR = Log.ERROR;
    public static final int ASSERT = Log.ASSERT;

    public static void println(int priority, String tag, String msg) {
        println(priority, tag, msg, null);
    }

    public static void println(int priority, String tag, String msg, Throwable tr) {
        if (!LOGGABLE) return;
        Log.println(priority, tag,
                msg != null ? msg : "" + "\n" + (tr != null ? Log.getStackTraceString(tr) : ""));
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable tr) {
        println(VERBOSE, tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable tr) {
        println(DEBUG,tag,msg,tr);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        println(INFO,tag,msg,tr);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        println(WARN,tag,msg,tr);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        println(ERROR,tag,msg,tr);
    }

    public static void wtf(String tag, String msg) {
        wtf(tag, msg, null);
    }

    public static void wtf(String tag, Throwable tr) {
        wtf(tag, null, tr);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        println(ASSERT,tag,msg,tr);
    }
}

package dev.isnow.fox.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static long CurrentMS() {
        return System.currentTimeMillis();
    }

    public static boolean Passed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static boolean elapsed(long time, long needed) {
        return Math.abs(System.currentTimeMillis() - time) >= needed;
    }

    public static String GetDate() {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return format.format(now);
    }

    public static long Remainder(long start, long required) {
        return required + start - System.currentTimeMillis();
    }

    public static long elapsed(final long time) {
        return Math.abs(System.currentTimeMillis() - time);
    }
}
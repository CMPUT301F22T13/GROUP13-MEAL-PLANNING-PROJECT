package com.example.cmput301f22t13.domainlayer.utils;

import java.util.concurrent.atomic.AtomicLong;

public class Utils {
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Stole from github https://stackoverflow.com/questions/17918553/is-it-good-practice-to-put-date-in-hashcode
    private static final AtomicLong TIME_STAMP = new AtomicLong();
    // can have up to 1000 ids per second.
    public static String getUniqueHash() {
        long now = System.currentTimeMillis();
        while (true) {
            long last = TIME_STAMP.get();
            if (now <= last)
                now = last + 1;
            if (TIME_STAMP.compareAndSet(last, now))
                return Long.toString(now);
        }
    }
}

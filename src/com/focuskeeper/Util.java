package com.focuskeeper;

public class Util {
    private Util() {
        
    }
    
    public static OS getPlatform() {
        String name = System.getProperty("os.name").toLowerCase();
        if (name.startsWith("windows")) {
            return OS.WINDOWS;
        } else if (name.indexOf("mac") >= 0) {
            return OS.OSX;
        } else {
            System.exit(1);
        }

        return OS.UNKNOWN;
    }
}

enum OS {
    WINDOWS, OSX, UNKNOWN
}
package com.focuskeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class DataCollector {
    String daemonPath;
    static final long ACTIVE_TIMEOUT = 5;
    long windowTimer = DataCollector.getTime();
    String windowName = null;
    
    public DataCollector() {
        daemonPath = "daemons/";
        if(FocusKeeper.os == OS.WINDOWS) {
            daemonPath += "WindowsDaemon.exe";
        }
    }
    
    public static long getTime() {
        return System.currentTimeMillis() / 1000;
    }
    
    public void startAsync() {
        Thread thread = new Thread(this::start);
        thread.setDaemon(true);
        thread.start();
    }
    
    public void start() {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec(daemonPath);
        } catch (IOException e) {
            FocusKeeper.logger.error("Unable to start data daemon", e);
            return;
        }

        try(BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
            String line;

            while ((line = input.readLine()) != null) {
                processDaemonLine(line);
            }
        } catch (IOException e) {
            FocusKeeper.logger.error("Error communicating with data daemon", e);
        }
    }
    
    public void processDaemonLine(String line) {
        line = line.trim();
        
        if(Pattern.matches("state:.+", line)) {
            processStateChange(line);
        }
        else if(Pattern.matches("window:.+", line)) {
            processWindowChange(line);
        }
        else {
            FocusKeeper.logger.error("Unknown daemon message type in line {}", line);
        }
    }
    
    public void processStateChange(String line) {
        if(line.endsWith(" active")) {
            this.windowTimer = getTime();
        }
        else {
            outputChangeEvent(windowName, getTime() - this.windowTimer - ACTIVE_TIMEOUT);
        }
    }
    
    public void processWindowChange(String line) {
        String[] splitLine = line.split("\\\\");
        
        String executable = splitLine[splitLine.length - 1];
        String newWindowName = executable.split("\\.")[0];
        
        outputChangeEvent(windowName, getTime() - this.windowTimer);
        
        windowName = newWindowName;
        windowTimer = getTime();
    }

    public void outputChangeEvent(String executable, long seconds) {
        FocusKeeper.logger.info("{}: {}", executable, seconds);
    }
}

package com.focuskeeper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataCollector {
    String daemonPath;
    
    public DataCollector() {
        daemonPath = "daemons/";
        if(FocusKeeper.os == OS.WINDOWS) {
            daemonPath += "WindowsDaemon.exe";
        }
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
        String[] split_line = line.split(": ");
        int seconds = Integer.parseInt(split_line[0]);
        String app_name = split_line[1];

        DatabaseController.addURLUsage(seconds, app_name);
    }
}

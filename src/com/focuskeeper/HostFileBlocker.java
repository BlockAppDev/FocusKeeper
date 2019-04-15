package com.focuskeeper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HostFileBlocker implements BlockController<String> {
    private static String lf = FocusKeeper.os == OS.WINDOWS ? "\r\n" : "\n";
    private final String ERR_MSG = "Couldn't read/write to hosts file";
    private HashSet<String> blockedHosts = new HashSet<>();

    public HostFileBlocker() {
        restoreHostsFile();
    }

    private void restoreHostsFile() {
        File backup = new File(getHostsFileBackupLocation());
        File hosts = new File(getHostsFileLocation());

        // Restore hosts file from backup if it exists
        if (backup.isFile()) {
            try {
                Files.copy(backup.toPath(), hosts.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                FocusKeeper.logger.error(ERR_MSG, e);
                return;
            }
        }

        // Create backup of hosts file
        try {
            Files.copy(hosts.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            FocusKeeper.logger.error(ERR_MSG, e);
        }
    }

    private static String formatBlockEntry(String hostname) {
        return "127.0.0.1\t" + hostname + lf;
    }

    public static String getHostsFileLocation() {
        switch (FocusKeeper.os) {
        case WINDOWS:
            return "C:\\Windows\\System32\\drivers\\etc\\hosts";
        case OSX:
            return "/etc/hosts";
        default:
            return "";
        }
    }

    public static String getHostsFileBackupLocation() {
        return getHostsFileLocation() + ".bak";
    }

    @Override
    public void addBlockItem(String hostname) {
        blockedHosts.add(hostname);

        try (FileWriter writer = new FileWriter(getHostsFileLocation(), true)) {
            writer.write(lf + formatBlockEntry(hostname));
        } catch (IOException e) {
            FocusKeeper.logger.error(ERR_MSG, e);
        }
    }

    @Override
    public void addBlockItems(List<String> hostnames) {
        blockedHosts.addAll(hostnames);

        try (FileWriter writer = new FileWriter(getHostsFileLocation(), true)) {
            writer.write(lf);
            for (String hostname : hostnames) {
                writer.write(formatBlockEntry(hostname));
            }
        } catch (IOException e) {
            FocusKeeper.logger.error(ERR_MSG, e);
        }
    }

    @Override
    public List<String> getBlockItems() {
        ArrayList<String> items = new ArrayList<>();
        items.addAll(blockedHosts);
        return items;
    }

    @Override
    public void enable() {
        addBlockItems(getBlockItems());
    }

    @Override
    public void disable() {
        restoreHostsFile();
    }
}
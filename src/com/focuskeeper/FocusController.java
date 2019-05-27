package com.focuskeeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jdk.nashorn.internal.ir.Block;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FocusController {
    FocusSettings settings = FocusSettings.load();

    public boolean shouldBlock(String url) {
        HashSet<BlockList> activeLists = checkListsToBlock();
        HashSet<String> urls = new HashSet<>();

        for(BlockList list: activeLists) {
            urls.addAll(list.items);
        }

        return urls.contains(url);
    }

    public HashSet<BlockList> checkListsToBlock() {
        HashSet<BlockList> listsToBlock = new HashSet<>();

        if(settings.manualFocus) {
            for(BlockList list: settings.blockLists.values()) {
                if(list.active) {
                    listsToBlock.add(list);
                }
            }

            return listsToBlock;
        }

        int day = getWeekday();
        long currMinute = minutesSinceDayStart();
        ArrayList<ScheduledBlock> scheduledBlocks = settings.schedule.days.get(Weekday.values()[day]);

        HashSet<String> listNamesToBlock = new HashSet<>();
        for(ScheduledBlock block: scheduledBlocks) {
            if(block.start < currMinute && block.end > currMinute) {
                listNamesToBlock.addAll(block.lists);
            }
        }

        for(String listName: listNamesToBlock) {
            BlockList considerList = settings.blockLists.get(listName);
            listsToBlock.add(considerList);
        }

        return listsToBlock;
    }

    public static int getWeekday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        day -= 2;
        if(day == -1) {
            day = 6;
        }

        return day;
    }

    public static long minutesSinceDayStart() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long passed = now - c.getTimeInMillis();
        long secondsPassed = passed / 1000;
        long minutesPassed = secondsPassed / 60;

        return minutesPassed;
    }
}

class FocusSettings {
    public static final String fileName = "settings.json";
    String version = "1.0.0";
    boolean manualFocus;
    HashMap<String, BlockList> blockLists;
    BlockSchedule schedule;

    public static FocusSettings getDefault() {
        FocusSettings settings = new FocusSettings();
        settings.blockLists = new HashMap<>();
        settings.schedule = new BlockSchedule();
        settings.schedule.days = new HashMap<>();

        BlockList distracting = new BlockList("Distracting", true, new HashSet<>());
        List<String> distractingSites = null;
        try {
            distractingSites = Files.readAllLines(Paths.get("lib/distracting.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        distracting.items.addAll(distractingSites);
        settings.blockLists.put("Distracting", distracting);

        for(int weekday = 0; weekday < 7; weekday++) {
            settings.schedule.days.put(Weekday.values()[weekday], new ArrayList<>());
        }

        HashSet<String> dayLists = new HashSet<>();
        dayLists.add("Distracting");
        settings.schedule.days.get(Weekday.MONDAY).add(new ScheduledBlock(0, 1440, dayLists));

        return settings;
    }

    public static FocusSettings load() {
        Path path = Paths.get(fileName);
        boolean exists = Files.exists(path);
        if(!exists) {
            FocusSettings newSettings = getDefault();
            save(newSettings);
            return newSettings;
        }

        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson settings_json = new Gson();
        FocusSettings loaded_settings = settings_json.fromJson(new String(fileBytes, StandardCharsets.UTF_8), FocusSettings.class);
        return loaded_settings;
    }

    public static void save(FocusSettings settings) {
        Gson settings_json = new GsonBuilder().setPrettyPrinting().create();
        String json_to_save = settings_json.toJson(settings);

        try (PrintWriter outFile = new PrintWriter(fileName)) {
            outFile.println(json_to_save);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

class BlockList {
    String name;
    boolean active;
    HashSet<String> items;

    public BlockList(String name, boolean active, HashSet<String> items) {
        this.name = name;
        this.active = active;
        this.items = items;
    }
}

class BlockSchedule {
    HashMap<Weekday, ArrayList<ScheduledBlock>> days;
}

class ScheduledBlock {
    // Start and end are both in minutes since the start of the day
    int start;
    int end;
    HashSet<String> lists;

    public ScheduledBlock(int start, int end, HashSet<String> lists) {
        this.start = start;
        this.end = end;
        this.lists = lists;
    }
}

enum Weekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
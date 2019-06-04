package com.focuskeeper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public Set<String> getAllBlockItems() {
        Set<BlockList> activeLists = checkListsToBlock();
        HashSet<String> urls = new HashSet<>();

        for(BlockList list: activeLists) {
            urls.addAll(list.items);
        }

        return urls;
    }

    public boolean shouldBlock(String url) {
        Set<String> allUrls = getAllBlockItems();
        return allUrls.contains(url);
    }

    public Map<String, Boolean> checkDistracting(List<String> items) {
        HashMap<String, Boolean> retItems = new HashMap<>();
        HashSet<String> allDistractingItems = new HashSet<>();
        for(BlockList list: settings.blockLists.values()) {
            allDistractingItems.addAll(list.items);
        }

        for(String item: items) {
            retItems.put(item, !allDistractingItems.contains(item));
        }

        return retItems;
    }

    public boolean checkDistracting(String item) {
        ArrayList<String> items = new ArrayList<>();
        items.add(item);
        Map<String, Boolean> result = checkDistracting(items);
        return result.get(item);
    }

    public Set<BlockList> checkListsToBlock() {
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
        return secondsPassed / 60;
    }
}

class FocusSettings {
    public static final String FILENAME = "settings.json";
    String version = "1.0.0";
    boolean manualFocus;
    HashMap<String, BlockList> blockLists;
    BlockSchedule schedule;
    public static final String DISTRACTING = "Distracting";

    public static FocusSettings getDefault() {
        FocusSettings settings = new FocusSettings();
        settings.blockLists = new HashMap<>();
        settings.schedule = new BlockSchedule();
        settings.schedule.days = new HashMap<>();

        BlockList distracting = loadList(DISTRACTING, true,"lib/distracting_sites.txt");
        settings.blockLists.put(DISTRACTING, distracting);

        BlockList apps = loadList("Apps", false,"lib/distracting_apps.txt");
        settings.blockLists.put("Apps", apps);

        settings.blockLists.put("Work", new BlockList("Work", false, new HashSet<>()));

        for(int weekday = 0; weekday < 7; weekday++) {
            settings.schedule.days.put(Weekday.values()[weekday], new ArrayList<>());
        }

        HashSet<String> dayLists = new HashSet<>();
        dayLists.add(DISTRACTING);
        settings.schedule.days.get(Weekday.MONDAY).add(new ScheduledBlock(0, 1440, dayLists));
        settings.schedule.days.get(Weekday.MONDAY).add(new ScheduledBlock(45, 900, dayLists));
        settings.schedule.days.get(Weekday.MONDAY).add(new ScheduledBlock(1000, 1100, dayLists));

        return settings;
    }

    public static BlockList loadList(String listName, boolean active, String listFileName) {
        BlockList distracting = new BlockList(listName, active, new HashSet<>());
        List<String> distractingSites = null;
        try {
            distractingSites = Files.readAllLines(Paths.get(listFileName));
        } catch (IOException e) {
            FocusKeeper.logger.error(e.getMessage());
        }
        distracting.items.addAll(distractingSites);

        return distracting;
    }

    public static FocusSettings load() {
        Path path = Paths.get(FILENAME);
        boolean exists = path.toFile().exists();
        if(!exists) {
            FocusSettings newSettings = getDefault();
            save(newSettings);
            return newSettings;
        }

        byte[] fileBytes = null;
        try {
            fileBytes = Files.readAllBytes(path);
        } catch (IOException e) {
            FocusKeeper.logger.error(e.getMessage());
        }

        Gson settingsJson = new Gson();
        return settingsJson.fromJson(new String(fileBytes, StandardCharsets.UTF_8), FocusSettings.class);
    }

    public static void save(FocusSettings settings) {
        Gson settingsJson = new GsonBuilder().setPrettyPrinting().create();
        String jsonToSave = settingsJson.toJson(settings);

        try (PrintWriter outFile = new PrintWriter(FILENAME)) {
            outFile.println(jsonToSave);
        } catch (FileNotFoundException e) {
            FocusKeeper.logger.error(e.getMessage());
        }
    }
}

class BlockList {
    String name;
    boolean active;
    Set<String> items;

    public BlockList(String name, boolean active, Set<String> items) {
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
    Set<String> lists;

    public ScheduledBlock(int start, int end, Set<String> lists) {
        this.start = start;
        this.end = end;
        this.lists = lists;
    }
}

enum Weekday {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}
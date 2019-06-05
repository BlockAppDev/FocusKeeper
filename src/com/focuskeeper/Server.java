package com.focuskeeper;

import static spark.Spark.*;

import com.google.gson.Gson;
import spark.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Server {
    static final int PORT = 8000;
    static final int MAX_THREADS = 3;
    static final int TIMEOUT = 3000;
    static final String SUCCESS_RESPONSE = "{\"message\": \"success\"}";
    static final String SETTINGS = "/settings";
    static final String CONTENT_TYPE = "Content-Type";
    static final String APPLICATION_JSON = "application/json";
    
    public static String getAddr() {
        return "http://localhost:" + PORT;
    }

    public void run() {
        threadPool(MAX_THREADS + 1, MAX_THREADS, TIMEOUT);
        port(PORT);
        staticFiles.externalLocation("static/build");

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "*");
        });

        options("/*", (request, response) -> "ok");

        path("/data", () -> put("", this::data));

        path("/focused", () -> get("", (request, response) -> {
            FocusKeeper.focusController.settings.manualFocus = !FocusKeeper.focusController.settings.manualFocus;
            return "";
        }));

        path("/stats", () -> get("", this::stats));

        path("/blocked", () -> get("", this::blocked));

        path(SETTINGS, () -> get("", (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            return new Gson().toJson(FocusKeeper.focusController.settings);
        }));

        get(SETTINGS, (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            return new Gson().toJson(FocusKeeper.focusController.settings);
        });

        post(SETTINGS, (request, response) -> {
            FocusSettings newSettings = new Gson().fromJson(request.body(), FocusSettings.class);
            FocusKeeper.focusController.settings = newSettings;
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            return SUCCESS_RESPONSE;
        });
    }

    public String data(spark.Request request, spark.Response response) {
        String host = request.queryParams("url");
        String seconds = request.queryParams("seconds");
        int intSeconds = Integer.parseInt(seconds);
        DatabaseController.addURLUsage(intSeconds, host);
        DatabaseController.addURLUsage(-intSeconds, "chrome");
        response.header(CONTENT_TYPE, APPLICATION_JSON);
        return SUCCESS_RESPONSE;
    }

    public String stats(spark.Request request, spark.Response response) {
        // Return usage statistics
        String start = request.queryMap().get("start").value();
        String end = request.queryMap().get("end").value();

        response.header(CONTENT_TYPE, APPLICATION_JSON);
        Map<String, Integer> mostUsed = DatabaseController.getMostUsed(start, end);

        List<StatsItem> mostUsedList = new ArrayList<>();
        
        for(Entry<String, Integer> entry : mostUsed.entrySet()){
            String name = entry.getKey();
            int value = entry.getValue();
            boolean focused = FocusKeeper.focusController.checkDistracting(name);
            mostUsedList.add(new StatsItem(name, value, focused));
        }

        return new Gson().toJson(mostUsedList);
    }

    public String blocked(spark.Request request, spark.Response response) {
        String submittedUrl = request.queryMap().get("url").value();
        boolean shouldBlock = FocusKeeper.focusController.shouldBlock(submittedUrl);
        response.header(CONTENT_TYPE, APPLICATION_JSON);

        HashMap<String, Boolean> responseJson = new HashMap<>();
        responseJson.put("blocked", shouldBlock);

        return new Gson().toJson(responseJson);
    }

    public void stopServer() {
        stop();
    }
}

class StatsItem {
    String name;
    int seconds;
    boolean focused;

    public StatsItem(String name, int seconds, boolean focused) {
        this.name = name;
        this.seconds = seconds;
        this.focused = focused;
    }
}
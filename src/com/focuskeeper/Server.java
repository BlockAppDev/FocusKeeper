package com.focuskeeper;

import static spark.Spark.*;

import com.google.gson.Gson;
import spark.Filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    static final int PORT = 8000;
    static final int MAX_THREADS = 3;
    static final int TIMEOUT = 3000;

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

        path("/data", () ->
            put("", (request, response) -> {
                System.out.println(request.params());
                response.header("Content-Type", "application/json");
                return "{'message': 'success'}";
            })
        );

        path("/stats", () -> get("", this::stats));

        path("/blocked", () -> get("", this::blocked));
    }

    public String stats(spark.Request request, spark.Response response) {
        response.header("Content-Type", "application/json");
        Map<String, Integer> mostUsed = DatabaseController.getMostUsed("2019/05/27", "2019/05/27");
        List<StatsItem> mostUsedList = new ArrayList<>();
        for(String name: mostUsed.keySet()) {
            mostUsedList.add(new StatsItem(name, mostUsed.get(name), true));
        }

        Gson gson = new Gson();
        return gson.toJson(mostUsedList);
    }

    public String blocked(spark.Request request, spark.Response response) {
        String submittedUrl = request.queryMap().get("url").value();
        boolean should_block = false;
        if(submittedUrl.length() % 2 == 0) {
            should_block = true;
        }
        response.header("Content-Type", "application/json");
        return "{'blocked': " + should_block + "}";
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
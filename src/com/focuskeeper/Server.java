package com.focuskeeper;

import static spark.Spark.*;

import spark.Filter;

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
                response.header("Content-Type", "application/json");
                return "{'message': 'success'}";
            })
        );

        path("/stats", () ->
            get("", (request, response) -> {
                response.header("Content-Type", "application/json");
                return "{'focus_time': 4566, 'distracted_time': 6345}";
            })
        );
        
        path("/blocked", () ->
            get("", (request, response) -> {
                String submittedUrl = request.queryMap().get("url").value();
                boolean should_block = false;
                if(submittedUrl.length() % 2 == 0) {
                    should_block = true;
                }
                response.header("Content-Type", "application/json");
                return "{'blocked': " + should_block + "}";
            })
        );
    }

    public void stopServer() {
        stop();
    }
}
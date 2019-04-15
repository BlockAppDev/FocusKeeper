package com.focuskeeper;

import static spark.Spark.*;

import spark.Filter;

public class Server {
    static final int PORT = 8000;
    static final int maxThreads = 3;
    static final int timeoutMS = 3000;

    public static String getAddr() {
        return "http://localhost:" + PORT;
    }

    public void run() {
        threadPool(maxThreads + 1, maxThreads, timeoutMS);
        port(PORT);
        staticFiles.externalLocation("static");

        after((Filter) (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "*");
        });

        options("/*", (request, response) -> {
            return "ok";
        });

        path("/data", () -> {
            put("", (request, response) -> {
                response.header("Content-Type", "application/json");
                return "{'message': 'success'}";
            });
        });

        path("/stats", () -> {
            get("", (request, response) -> {
                response.header("Content-Type", "application/json");
                return "{'focus_time': 4566, 'distracted_time': 6345}";
            });
        });
    }

    public void stopServer() {
        stop();
    }
}
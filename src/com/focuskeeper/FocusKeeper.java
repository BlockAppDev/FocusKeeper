package com.focuskeeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class FocusKeeper extends Application {
    Server server;
    static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    BlockController<String> blockController;
    static OS os = Util.getPlatform();

    @Override
    public void init() throws Exception {
        if (Environment.isMac()) {
            BrowserCore.initialize();
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("FocusKeeper");
        
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);
        
        primaryStage.setScene(new Scene(view, 350, 450));
        primaryStage.show();

        browser.loadURL(Server.getAddr());
    }

    public static void main(String[] args) {
        FocusKeeper focuskeeper = new FocusKeeper();
        focuskeeper.blockController = new HostFileBlocker();
        focuskeeper.server = new Server();
        try {
            focuskeeper.server.run();
        } catch (Exception e) {
            FocusKeeper.logger.error("Server start error", e);
            return;
        }

        // Launch GUI
        launch(args);

        focuskeeper.server.stopServer();
        
        System.exit(0); // Make sure all threads terminate
    }
}
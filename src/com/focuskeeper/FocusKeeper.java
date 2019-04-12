package com.focuskeeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class FocusKeeper extends Application {
	Server server;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("FocusKeeper");
        primaryStage.setScene(new Scene(root, 350, 450));
        primaryStage.show();

        WebView view = (WebView)root.lookup("#main_view");

        final WebEngine webEngine = view.getEngine();
        webEngine.load(Server.getAddr() + "/static/index.html");
    }

    public static void main(String[] args) {
    	FocusKeeper focuskeeper = new FocusKeeper();
    	focuskeeper.server = new Server();
    	try {
    		focuskeeper.server.run();
    	} catch(Exception e) {
    		System.out.println(e);
    		return;
    	}
    	
    	// Launch GUI
        launch(args);
        
        focuskeeper.server.stop();
    }
}
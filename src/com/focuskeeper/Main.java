package com.focuskeeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("FocusKeeper");
        primaryStage.setScene(new Scene(root, 350, 450));
        primaryStage.show();

        WebView view = (WebView)root.lookup("#main_view");

        final WebEngine webEngine = view.getEngine();
        webEngine.load("http://localhost:8000/index.html");
    }


    public static void main(String[] args) {
        launch(args);
    }
}
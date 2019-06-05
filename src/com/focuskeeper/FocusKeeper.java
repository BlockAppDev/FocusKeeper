package com.focuskeeper;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FocusKeeper extends Application {
    private static FocusKeeper instance = null;
    Server server;
    Stage stage;
    static final Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    BlockController<String> blockController;
    static FocusController focusController;
    long lastFocusChange = 0;
    static OS os = Util.getPlatform();
    
    public static FocusKeeper getInstance()
    {
        if (instance == null) {
            instance = new FocusKeeper();
        }

        return instance;
    }
    
    @Override
    public void init() throws Exception {
        if (Environment.isMac()) {
            BrowserCore.initialize();
        }
    }
    
    public void initTray() {
        Toolkit.getDefaultToolkit();
        
        if(!java.awt.SystemTray.isSupported()) {
            FocusKeeper.logger.error("Platform not supported");
            Platform.exit();
        }
        
        SystemTray tray = SystemTray.getSystemTray();
        
        Image icon = null;
        try {
            icon = ImageIO.read(new File("lib/icon.png"));
        } catch (IOException e) {
            FocusKeeper.logger.error("Problem loading tray icon {}", e);
        }
        
        TrayIcon trayIcon = new TrayIcon(icon);
        
        FocusKeeper fk = this;
        trayIcon.addMouseListener(new MouseAdapter() {
      
        	@Override
            public void mouseClicked(MouseEvent event) {
                if(event.getButton() != 1) {
                    // Ignore anything other than a left click
                    return;
                }
                Platform.runLater(fk::toggleShowStage);
            }
        });
        
        PopupMenu menu = new PopupMenu();
        
        MenuItem exitItem = new MenuItem("Exit FocusKeeper");
        exitItem.addActionListener(event -> Platform.exit());
        menu.add(exitItem);
        
        trayIcon.setPopupMenu(menu);
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            FocusKeeper.logger.error(e.getMessage());
        }
    }
    
    public void toggleShowStage() {
        if(System.currentTimeMillis() - lastFocusChange < 100) {
            return;
        }
        
        lastFocusChange = System.currentTimeMillis();
        
        if(stage.isShowing()) {
            stage.hide();
        }
        else {
            stage.show();
        }
    }
    
    public void positionWindow() {
        int x = 0;
        int y = 0;

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        
        // pop up location for Windows
        if(FocusKeeper.os == OS.WINDOWS) {
            x = (int) (bounds.getMaxX() - 430);
            y = (int) (bounds.getMaxY() - 475);
        }
        // pop up location for Mac
        else {
        	x = (int) (bounds.getMaxX() - 420);
        	y = (int) (bounds.getMaxY() - 715);
       }
        
        this.stage.setX(x);
        this.stage.setY(y);
        
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        Platform.setImplicitExit(false);
        
        FocusKeeper fk = this;
        primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean hidden, Boolean focused) {
                if(!focused && System.currentTimeMillis() - fk.lastFocusChange > 100) {
                    fk.lastFocusChange = System.currentTimeMillis();
                    stage.hide();
                }
            }
        });
        
        javax.swing.SwingUtilities.invokeLater(this::initTray);
        
        primaryStage.initStyle(StageStyle.UNDECORATED);
        this.positionWindow();
        
        Browser browser = new Browser();
        BrowserView view = new BrowserView(browser);
        
        primaryStage.setScene(new Scene(view, 350, 450));
        primaryStage.show();

        browser.loadURL(Server.getAddr());
    }

    public static void main(String[] args) {
        FocusKeeper focuskeeper = new FocusKeeper();

        DatabaseController.connect();

        FocusKeeper.focusController = new FocusController();

        focuskeeper.server = new Server();
        try {
            focuskeeper.server.run();
        } catch (Exception e) {
            FocusKeeper.logger.error("Server start error", e);
            return;
        }

        DataCollector dataCollector = new DataCollector();
        dataCollector.startAsync();

        // Launch GUI
        // FocusKeeper.launch(args); // Launch

        // focuskeeper.server.stopServer();  // Stop
        
        // System.exit(0); // Make sure all threads terminate
    }
}

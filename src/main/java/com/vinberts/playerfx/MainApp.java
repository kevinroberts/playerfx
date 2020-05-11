package com.vinberts.playerfx;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import javafx.application.Application;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

/**
 *
 */
public class MainApp extends Application {
    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);
    Player player;
    FileChooser fileChooser;
    BorderlessScene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {

        MenuItem open = new MenuItem("Open");
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        MenuItem exit = new MenuItem("Exit");
        exit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));

        Menu file = new Menu("File");
        MenuBar menu = new MenuBar();

        // Connecting the above menu
        file.getItems().add(open);
        file.getItems().add(exit);
        menu.getMenus().add(file);

        fileChooser = new FileChooser();
        open.setOnAction(event -> {
            // Pausing the video while switching
            player.player.pause();
            File videoFile = fileChooser.showOpenDialog(primaryStage);

            // Choosing the file to play
            if (videoFile != null) {
                try {
                    player = new Player(videoFile.toURI().toURL().toExternalForm(), menu);
                    scene.setContent(player);
                } catch (MalformedURLException e1) {
                    LOG.error("Malformed url exception", e1);
                }
            }
        });

        exit.setOnAction(event -> {
            primaryStage.close();
        });

        // here you can choose any video
        player = new Player(getClass().getResource("video.mp4").toURI().toString(), menu);

        // Constructor using your primary stage and the root Parent of your content.
        scene = new BorderlessScene(primaryStage, StageStyle.UNDECORATED, player, 800, 545);
        primaryStage.setScene(scene); // Set the scene to your stage and you're done!
        scene.removeDefaultCSS();
        // To move the window around by pressing a node:
        scene.setMoveControl(menu);

        //Show
        primaryStage.setTitle("JavaFX Media Player");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}

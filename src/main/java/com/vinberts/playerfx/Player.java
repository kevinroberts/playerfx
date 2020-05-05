package com.vinberts.playerfx;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class Player extends BorderPane {
    private static final Logger LOG = LoggerFactory.getLogger(Player.class);
    Media media;
    MediaPlayer player;
    MediaView view;
    Pane pane;
    MediaControls mediaControls;

    public Player(String file, MenuBar menu) {
        try {
            this.setTop(menu);
            media = new Media(file);
            player = new MediaPlayer(media);
            view = new MediaView(player);
            pane = new Pane();
            pane.getChildren().add(view);
            // set background color of video player to black
            pane.setStyle("-fx-background-color: black;");

            setCenter(pane);
            mediaControls = new MediaControls(player);
            setBottom(mediaControls);
            player.play(); // Making the video auto play on open
        } catch (Exception e) {
            LOG.error("Could not load media file", e);
        }
    }

}

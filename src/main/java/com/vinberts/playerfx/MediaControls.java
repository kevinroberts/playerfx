package com.vinberts.playerfx;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class MediaControls extends HBox {
    private static final Logger LOG = LoggerFactory.getLogger(MediaControls.class);

    MediaPlayer player;
    private final boolean repeat = false;
    Button playButton = new Button("||"); // For pausing the player
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;

    public MediaControls(final MediaPlayer player) {
        this.player = player;
        setStyle("-fx-background-color: #bfc2c7;");
        setAlignment(Pos.CENTER);
        setPadding(new Insets(5, 10, 5, 10));

        playButton.setOnAction(e -> {
            MediaPlayer.Status status = player.getStatus(); // To get the status of Player
            if (status == status.PLAYING) {

                // If the status is Video playing
                if (player.getCurrentTime().greaterThanOrEqualTo(player.getTotalDuration())) {

                    // If the player is at the end of video
                    player.seek(player.getStartTime()); // Restart the video
                    player.play();
                }
                else {
                    // Pausing the player
                    player.pause();
                    playButton.setText(">");
                }
            } // If the video is stopped, halted or paused
            if (status == MediaPlayer.Status.HALTED || status == MediaPlayer.Status.STOPPED || status == MediaPlayer.Status.PAUSED) {
                player.play(); // Start the video
                playButton.setText("||");
            }
        });

        player.currentTimeProperty().addListener(ov -> updateValues());

        player.setOnReady(() -> {
            duration = player.getTotalDuration();
            updateValues();
        });

        player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);

        player.setOnEndOfMedia(() -> {
            LOG.info("End of video action");
            if (!repeat) {
                LOG.info("Resetting video");
                playButton.setText(">");
            }
        });

        this.getChildren().add(playButton);
        // Add spacer
        Label spacer = new Label("   ");
        this.getChildren().add(spacer);

        // Add Time label
        Label timeLabel = new Label("Time: ");
        this.getChildren().add(timeLabel);

        // Add time slider
        timeSlider = new Slider();
        HBox.setHgrow(timeSlider, Priority.ALWAYS);
        timeSlider.setMinWidth(50);
        timeSlider.setMaxWidth(Double.MAX_VALUE);

        // In order to jump to the certain part of video
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isPressed()) {
                    player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
                }
            }
        });

        this.getChildren().add(timeSlider);

        // Add Play label
        playTime = new Label();
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        this.getChildren().add(playTime);

        // Add the volume label
        Label volumeLabel = new Label("Vol: ");
        this.getChildren().add(volumeLabel);

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setPrefWidth(70);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    player.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });
        this.getChildren().add(volumeSlider);

    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = player.getCurrentTime();
                playTime.setText(formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled()
                        && duration.greaterThan(Duration.ZERO)
                        && !timeSlider.isValueChanging()) {
                    timeSlider.setValue(currentTime.divide(duration).toMillis()
                            * 100.0);
                }
                if (!volumeSlider.isValueChanging()) {
                    volumeSlider.setValue((int) Math.round(player.getVolume()
                            * 100));
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60
                    - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours,
                        elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes,
                        elapsedSeconds);
            }
        }
    }
}

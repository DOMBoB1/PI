package ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusic {
    private static MediaPlayer mediaPlayer;

    public static void playMusic() {
        try {
          
            String musicPath = BackgroundMusic.class.getClassLoader().getResource("R/scub.wav").toExternalForm();
            System.out.println("Calea către fișier: " + musicPath);

            Media sound = new Media(musicPath);
            mediaPlayer = new MediaPlayer(sound);

            mediaPlayer.setOnReady(() -> System.out.println("MediaPlayer este pregătit."));
            mediaPlayer.setOnPlaying(() -> System.out.println("Muzica este redată."));
            mediaPlayer.setOnError(() -> System.out.println("Eroare la redare: " + mediaPlayer.getError()));

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); 
            mediaPlayer.setVolume(1.0); 
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Eroare la redarea muzicii.");
        }
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            System.out.println("Muzica a fost oprită.");
        }
    }
}

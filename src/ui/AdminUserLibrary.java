package ui;

import bd.GameDBC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Game;
import model.User;

import java.util.List;

public class AdminUserLibrary {

    private final User user; 

    public AdminUserLibrary(User user) {
        this.user = user;
    }

    public void start(Stage primaryStage) {
    	 primaryStage.setTitle("Librăria lui " + user.getUsername());


        
        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER); 
        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);
       
        Text title = new Text("Librăria lui " + user.getUsername());
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        
        FlowPane gamesContainer = new FlowPane(20, 20);
        gamesContainer.setPadding(new Insets(20));
        gamesContainer.setAlignment(Pos.CENTER);

       
        List<Game> games = GameDBC.getUserLibrary(user.getId());
        for (Game game : games) {
            VBox gameCard = createGameCard(game);
            gamesContainer.getChildren().add(gameCard);
        }


        ScrollPane scrollPane = new ScrollPane(gamesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

       
        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> primaryStage.close());

        HBox buttonBox = new HBox(backButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));

        layout.getChildren().addAll(title, scrollPane, buttonBox);

       
        
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
       

    }

    /**
     * Creează un card pentru fiecare joc.
     */
    private VBox createGameCard(Game game) {
        VBox gameCard = new VBox(10);
        gameCard.setAlignment(Pos.CENTER);
        gameCard.setPadding(new Insets(10));
        gameCard.setStyle("-fx-border-color: lightgray; -fx-border-width: 2; -fx-border-radius: 5;");

        ImageView gameIcon = new ImageView();
        try {
            if (game.getIconPath() != null && !game.getIconPath().isEmpty()) {
                gameIcon.setImage(new Image(game.getIconPath(), 100, 100, true, true));
            } else {
                gameIcon.setImage(new Image("https://via.placeholder.com/100"));
            }
        } catch (Exception e) {
            gameIcon.setImage(new Image("https://via.placeholder.com/100"));
        }

        gameIcon.setFitWidth(100);
        gameIcon.setFitHeight(100);

        Text gameName = new Text(game.getName());
        gameName.setStyle("-fx-font-weight: bold;");

        gameCard.getChildren().addAll(gameIcon, gameName);
        return gameCard;
    }

    /**
     * Setează imaginea de fundal pentru layout.
     */
    private void setBackground(VBox layout) {
        try {
            Image backgroundImage = new Image("file:/C:/path_to_background_image/background.jpg");
            layout.setStyle("-fx-background-image: url('" + backgroundImage.getUrl() + "'); " +
                    "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
        } catch (Exception e) {
            System.out.println("Eroare la încărcarea imaginii de fundal.");
        }
    }
}

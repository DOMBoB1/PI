package gameStuff;

import bd.GameDBC;
import bd.RatingDBC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Game;

import java.util.List;

/**
 * @author Dumitras Olga-Maria
 * @date 12/17/2024
 * 
 * Clasa VGD reprezintă interfața grafică pentru afișarea detaliilor unui joc.
 * Utilizatorul poate vizualiza informații despre joc, imagini suplimentare și
 * poate acorda un rating jocului.
 */
public class VGD {

    private final Game game; // Jocul selectat pentru afișarea detaliilor
    private final int userId; // ID-ul utilizatorului curent

    /**
     * Constructorul clasei VGD.
     * 
     * @param game   Obiectul Game care conține informațiile despre jocul selectat.
     * @param userId ID-ul utilizatorului care vizualizează detaliile.
     */
    public VGD(Game game, int userId) {
        this.game = game;
        this.userId = userId;
    }

    /**
     * Inițializează interfața grafică pentru afișarea detaliilor jocului.
     * 
     * @param primaryStage Fereastra principală în care sunt afișate detaliile.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Detalii Joc - " + game.getName());

 
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

    
        ImageView gameIcon = new ImageView(new Image(game.getIconPath()));
        gameIcon.setFitWidth(150);
        gameIcon.setPreserveRatio(true);


        Label nameLabel = new Label("Nume: " + game.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label genreLabel = new Label("Gen: " + game.getGenre());
        Label yearLabel = new Label("Anul lansării: " + game.getReleaseYear());
        Label developerLabel = new Label("Dezvoltator: " + game.getDeveloper());
        Label descriptionLabel = new Label("Descriere: " + game.getDescription());
        descriptionLabel.setWrapText(true);

     
        double averageRating = RatingDBC.getAverageRating(game.getId());
        Label ratingLabel = new Label("Rating Mediu: " + String.format("%.1f / 5", averageRating));

  
        FlowPane imageContainer = new FlowPane(10, 10);
        imageContainer.setAlignment(Pos.CENTER);
        List<String> imagePaths = GameDBC.getGameImages(game.getId());
        if (imagePaths.isEmpty()) {
            imageContainer.getChildren().add(new Label("Nu există imagini adiționale."));
        } else {
            for (String path : imagePaths) {
                ImageView imageView = new ImageView(new Image(path));
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
                imageContainer.getChildren().add(imageView);
            }
        }


        int userRating = RatingDBC.getUserRating(game.getId(), userId);
        FlowPane starsContainer = createStarsContainer(primaryStage, userRating);

     
        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> primaryStage.close());

    
        layout.getChildren().addAll(
                gameIcon, nameLabel, genreLabel, yearLabel, developerLabel,
                descriptionLabel, ratingLabel, imageContainer,
                new Label("Acordă Rating:"), starsContainer, backButton
        );

       
        Scene scene = new Scene(layout, 500, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creează un sistem de rating cu stele pentru utilizator.
     * 
     * @param primaryStage  Fereastra principală pentru reîncărcare.
     * @param currentRating Rating-ul actual al utilizatorului pentru acest joc.
     * @return FlowPane care conține stelele pentru rating.
     */
    private FlowPane createStarsContainer(Stage primaryStage, int currentRating) {
        FlowPane starsContainer = new FlowPane(10, 10);
        starsContainer.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 5; i++) {
            Button starButton = new Button("★");
            starButton.setStyle("-fx-font-size: 24px; -fx-text-fill: " + (i <= currentRating ? "gold" : "gray"));
            final int rating = i;
            starButton.setOnAction(e -> {
                RatingDBC.addRating(game.getId(), userId, rating);
                start(primaryStage);
            });
            starsContainer.getChildren().add(starButton);
        }

        return starsContainer;
    }
}

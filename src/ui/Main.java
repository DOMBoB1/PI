package ui;

import bd.DBC;

import bd.GameDBC;
import bd.GameSDBC;
import bd.RatingDBC;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import ui.BackgroundMusic;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Meniu Principal");
        BackgroundMusic.playMusic();
        DBC.initializeDatabase(); 
        GameDBC.initializeGameTables();
        RatingDBC.createRatingsTable();
        GameSDBC.initializeSuggestionTable();
        GameSDBC.initializeSuggestionVotesTable();
        DBC.verifyTables(); 
      
        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

    
        Image scoobyImage = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        ImageView scoobyImageView = new ImageView(scoobyImage);
        scoobyImageView.setFitWidth(100);
        scoobyImageView.setPreserveRatio(true);

        
        Rectangle textBackground = new Rectangle(300, 50, Color.BEIGE);
        textBackground.setArcWidth(20);
        textBackground.setArcHeight(20);

       
        Text welcomeText = new Text("Bine ai venit în Game Library!\nScooby-Dooby-Doo!");
        welcomeText.setTextAlignment(TextAlignment.CENTER);
        welcomeText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        BorderPane textBubble = new BorderPane();
        textBubble.setCenter(welcomeText);
        textBubble.setMinSize(300, 50);
        textBubble.setMaxSize(300, 50);
        textBubble.setStyle("-fx-background-color: beige; -fx-background-radius: 20px; -fx-border-radius: 20px; -fx-border-color: black; -fx-border-width: 2px;");

     
        Button loginButton = new Button("Logare");
        loginButton.setPrefWidth(150);
        loginButton.setOnAction(e -> new Login().start(primaryStage));

        Button createAccountButton = new Button("Creare Cont");
        createAccountButton.setPrefWidth(150);
        createAccountButton.setOnAction(e -> new NewAcc().start(primaryStage));


        VBox layout = new VBox(20, textBubble, scoobyImageView, loginButton, createAccountButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));


        Back.setBackground(layout);


        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

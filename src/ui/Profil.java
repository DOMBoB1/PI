package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.User;
import bd.DBC;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Profil {

    private User user;

    public Profil(User user) {
        this.user = user;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Profilul Meu");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Back.setBackground(layout);

      
        ImageView profilePicture = new ImageView();
        try {
            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                profilePicture.setImage(new Image(user.getProfilePicture(), true));
            } else {
                throw new IllegalArgumentException("Invalid image path");
            }
        } catch (Exception e) {
            profilePicture.setImage(new Image("https://via.placeholder.com/100"));
        }
        profilePicture.setFitHeight(100);
        profilePicture.setFitWidth(100);
        profilePicture.setPreserveRatio(true);

        Label profilePictureLabel = new Label("Imagine de profil:");
        Button changePictureButton = new Button("Schimbă Poza");
        changePictureButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Alege Poza de Profil");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                user.setProfilePicture(selectedFile.toURI().toString());
                profilePicture.setImage(new Image(user.getProfilePicture()));
            }
        });

   
        Label usernameLabel = new Label("Nume:");
        TextField usernameInput = new TextField(user.getUsername());
        usernameInput.setEditable(false);

        Label emailLabel = new Label("Email:");
        TextField emailInput = new TextField(user.getEmail());
        emailInput.setEditable(false);

        Label bioLabel = new Label("Bio:");
        TextArea bioInput = new TextArea(user.getBio());
        bioInput.setPrefRowCount(3);

        Button saveButton = new Button("Salvează Modificările");
        saveButton.setOnAction(e -> {
            String newBio = bioInput.getText().trim();
            user.setBio(newBio);
            saveProfileChangesInDatabase(newBio, user.getProfilePicture());
        });

        Button settingsButton = new Button("Setări");
        settingsButton.setOnAction(e -> new UserSet(user).start(primaryStage));

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> new UserM(user).start(primaryStage));

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(10);
        infoGrid.setVgap(10);
        infoGrid.setAlignment(Pos.CENTER);
        infoGrid.add(profilePictureLabel, 0, 0);
        infoGrid.add(profilePicture, 1, 0);
        infoGrid.add(changePictureButton, 1, 1);
        infoGrid.add(usernameLabel, 0, 2);
        infoGrid.add(usernameInput, 1, 2);
        infoGrid.add(emailLabel, 0, 3);
        infoGrid.add(emailInput, 1, 3);
        infoGrid.add(bioLabel, 0, 4);
        infoGrid.add(bioInput, 1, 4);

        VBox buttons = new VBox(10, saveButton, settingsButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(infoGrid, buttons);

        Scene scene = new Scene(layout, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveProfileChangesInDatabase(String bio, String profilePicture) {
        try (Connection connection = DBC.getConnection()) {
            String updateQuery = "UPDATE users SET bio = ?, profile_picture = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, bio);
            preparedStatement.setString(2, profilePicture);
            preparedStatement.setInt(3, user.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Modificările au fost salvate în baza de date.");
            } else {
                System.out.println("Eroare la salvarea modificărilor.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

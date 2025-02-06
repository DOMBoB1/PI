package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Admin;
import bd.DBC;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ProfilA {

    private Admin admin;

    public ProfilA(Admin admin) {
        this.admin = admin;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Profilul Meu");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

      
        Back.setBackground(layout);


        ImageView profilePicture = new ImageView();
        if (admin.getProfilePicture() != null && !admin.getProfilePicture().isEmpty()) {
            profilePicture.setImage(new Image(admin.getProfilePicture()));
        } else {
            profilePicture.setImage(new Image("https://via.placeholder.com/100")); 
        }
        profilePicture.setFitHeight(100);
        profilePicture.setFitWidth(100);
        profilePicture.setPreserveRatio(true);

        Label profilePictureLabel = new Label("Imagine de profil:");
        profilePictureLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button changePictureButton = new Button("Schimbă Poza");
        changePictureButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Alege Poza de Profil");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                admin.setProfilePicture(selectedFile.toURI().toString());
                profilePicture.setImage(new Image(admin.getProfilePicture()));
            }
        });

        Label usernameLabel = new Label("Nume:");
        usernameLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        TextField usernameInput = new TextField(admin.getUsername());

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        TextField emailInput = new TextField(admin.getEmail());
        emailInput.setEditable(false);

        Label bioLabel = new Label("Bio:");
        bioLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        TextArea bioInput = new TextArea(admin.getBio());
        bioInput.setPrefRowCount(3);

       
        Button saveButton = new Button("Salvează Modificările");
        saveButton.setOnAction(e -> {
            String newUsername = usernameInput.getText().trim();
            String newBio = bioInput.getText().trim();
            admin.setUsername(newUsername);
            admin.setBio(newBio);

            
            saveProfileChangesInDatabase(newUsername, newBio, admin.getProfilePicture());
        });

  
        Button settingsButton = new Button("Setări");
        settingsButton.setOnAction(e -> new AdminSet(admin).start(primaryStage));

 
        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> new AdminM(admin).start(primaryStage));

     
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

    private void saveProfileChangesInDatabase(String username, String bio, String profilePicture) {
        try (Connection connection = DBC.getConnection()) {
            String updateQuery = "UPDATE admins SET username = ?, bio = ?, profile_picture = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, bio);
            preparedStatement.setString(3, profilePicture);
            preparedStatement.setInt(4, admin.getId());

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

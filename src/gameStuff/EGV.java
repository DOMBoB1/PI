package gameStuff;

import bd.GameDBC;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Game;
import ui.Back;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 * 
 * Clasa EGV oferă o interfață grafică pentru adăugarea sau editarea unui joc existent.
 * Permite modificarea detaliilor jocului precum numele, genul, anul lansării, descrierea și imaginile asociate.
 */
public class EGV {

    private Game game;
    private List<File> selectedImages = new ArrayList<>(); 

    /**
     * Constructorul clasei EGV.
     * 
     * @param game Jocul care urmează să fie editat. Dacă jocul este null, se va inițializa o sesiune pentru adăugarea unui joc nou.
     */
    public EGV(Game game) {
        this.game = game;
    }

    /**
     * Inițializează și afișează interfața grafică pentru adăugarea sau modificarea jocului.
     * 
     * @param primaryStage Fereastra principală în care se va afișa interfața.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle(game == null ? "Adaugă Joc" : "Modifică Joc");

        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextField nameInput = new TextField(game == null ? "" : game.getName());
        nameInput.setPromptText("Nume joc");

        TextField genreInput = new TextField(game == null ? "" : game.getGenre());
        genreInput.setPromptText("Gen joc");

        TextField releaseYearInput = new TextField(game == null ? "" : String.valueOf(game.getReleaseYear()));
        releaseYearInput.setPromptText("Anul apariției");

        TextArea descriptionInput = new TextArea(game == null ? "" : game.getDescription());
        descriptionInput.setPromptText("Descriere joc");

        Label imageLabel = new Label("Iconiță existentă: " + (game != null ? game.getIconPath() : "Nicio iconiță"));
        Button changeIconButton = new Button("Schimbă Iconița");

        File[] selectedIcon = {null}; 

        changeIconButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedIcon[0] = file;
                imageLabel.setText("Iconiță nouă: " + file.getName());
            }
        });

        Label additionalImagesLabel = new Label("Imagini suplimentare (opțional):");
        FlowPane imageContainer = new FlowPane(10, 10);
        imageContainer.setAlignment(Pos.CENTER);

        Button uploadImagesButton = new Button("Adaugă Imagini");
        uploadImagesButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg"));
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                selectedImages.addAll(files);
                imageContainer.getChildren().clear();
                for (File file : files) {
                    ImageView imageView = new ImageView(new Image(file.toURI().toString()));
                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);
                    imageContainer.getChildren().add(imageView);
                }
            }
        });

        Button saveButton = new Button("Salvează Modificări");
        saveButton.setOnAction(e -> {
            String name = nameInput.getText().trim();
            String genre = genreInput.getText().trim();
            String description = descriptionInput.getText().trim();

            if (name.isEmpty() || genre.isEmpty() || description.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Câmpuri incomplete", "Toate câmpurile sunt obligatorii.");
                return;
            }

            int releaseYear = Integer.parseInt(releaseYearInput.getText().trim());

            if (game == null) {
                
                Game newGame = new Game(0, name, description, 0, genre, releaseYear, "", selectedIcon[0].toURI().toString());
                int gameId = GameDBC.addGame(newGame);
                if (gameId > 0 && !selectedImages.isEmpty()) {
                    GameDBC.addGameImages(gameId, selectedImages);
                }
            } else {
               
                game.setName(name);
                game.setGenre(genre);
                game.setDescription(description);
                game.setReleaseYear(releaseYear);
                if (selectedIcon[0] != null) {
                    game.setIconPath(selectedIcon[0].toURI().toString());
                }
                GameDBC.updateGame(game);
                if (!selectedImages.isEmpty()) {
                    GameDBC.addGameImages(game.getId(), selectedImages);
                }
            }

            showAlert(Alert.AlertType.INFORMATION, "Succes", "Jocul a fost salvat cu succes!");
            primaryStage.close();
        });

        Button cancelButton = new Button("Anulează");
        cancelButton.setOnAction(e -> primaryStage.close());

        layout.getChildren().addAll(
                nameInput, genreInput, releaseYearInput, descriptionInput,
                imageLabel, changeIconButton,
                additionalImagesLabel, imageContainer, uploadImagesButton,
                saveButton, cancelButton
        );

        Scene scene = new Scene(layout, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Afișează o alertă utilizatorului cu un mesaj specificat.
     * 
     * @param alertType Tipul alertei (INFORMATION, WARNING, ERROR).
     * @param title Titlul alertei.
     * @param message Mesajul afișat în alertă.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

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
import model.Admin;
import model.Game;
import ui.Back;

import java.io.File;
import java.util.List;

/**
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 * 
 * Clasa EditGF oferă o interfață grafică pentru modificarea detaliilor unui joc existent în baza de date.
 * Permite actualizarea numelui jocului, genului, anului apariției, descrierii, iconiței și gestionarea imaginilor suplimentare.
 */
public class EditGF {

   
    private String selectedImageForDeletion = null; 
    private ImageView currentlySelectedImageView = null; 
    private Label selectedImageLabel = new Label("Nicio imagine selectată"); 

    /**
     * Constructorul clasei EditGF.
     *
     * @param gameToEdit Jocul care urmează să fie modificat.
     */
    private final Game gameToEdit;
    private final Admin currentAdmin;

    public EditGF(Game gameToEdit, Admin admin) {
        this.gameToEdit = gameToEdit;
        this.currentAdmin = admin;
    }


    /**
     * Inițializează și afișează interfața grafică pentru editarea jocului selectat.
     * 
     * @param primaryStage Fereastra principală în care se va afișa interfața grafică.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Modifică Joc");

        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        TextField nameInput = new TextField(gameToEdit.getName());
        TextField genreInput = new TextField(gameToEdit.getGenre());
        TextField releaseYearInput = new TextField(String.valueOf(gameToEdit.getReleaseYear()));
        TextArea descriptionInput = new TextArea(gameToEdit.getDescription());

        Label iconLabel = new Label("Iconiță existentă: " + gameToEdit.getIconPath());
        Button iconButton = new Button("Schimbă Iconița");

        File[] selectedIcon = {null};
        iconButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedIcon[0] = file;
                iconLabel.setText("Iconiță selectată: " + file.getName());
            }
        });

        FlowPane imageContainer = new FlowPane(10, 10);
        imageContainer.setAlignment(Pos.CENTER);

        currentlySelectedImageView = new ImageView();
        currentlySelectedImageView.setFitWidth(150);
        currentlySelectedImageView.setFitHeight(150);
        currentlySelectedImageView.setPreserveRatio(true);

        List<String> existingImages = GameDBC.getGameImages(gameToEdit.getId());
        for (String imagePath : existingImages) {
            ImageView imageView = new ImageView(new Image(imagePath));
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);

            imageView.setOnMouseClicked(e -> {
                selectedImageForDeletion = imagePath;
                currentlySelectedImageView.setImage(new Image(imagePath));
                selectedImageLabel.setText("Imagine selectată: " + imagePath);
            });

            imageContainer.getChildren().add(imageView);
        }

        Button deleteImageButton = new Button("Șterge Imaginea Selectată");
        deleteImageButton.setOnAction(e -> {
            if (selectedImageForDeletion != null) {
                GameDBC.deleteImage(gameToEdit.getId(), selectedImageForDeletion);
                start(primaryStage);
            }
        });
        Button addImagesButton = new Button("Adaugă Imagini");
        addImagesButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selectează Imagini");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg"));
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);

            if (files != null) {
                for (File file : files) {
                    String filePath = file.toURI().toString();

                  
                    boolean alreadyExists = GameDBC.getGameImages(gameToEdit.getId()).contains(filePath);

                    if (!alreadyExists) {
                        
                        GameDBC.addGameImage(gameToEdit.getId(), file);

                        
                        ImageView imageView = new ImageView(new Image(filePath));
                        imageView.setFitWidth(80);
                        imageView.setFitHeight(80);
                        imageView.setPreserveRatio(true);

                        
                        imageView.setOnMouseClicked(event -> {
                            selectedImageForDeletion = filePath;
                            currentlySelectedImageView.setImage(new Image(filePath));
                            selectedImageLabel.setText("Imagine selectată: " + file.getName());
                        });

                       
                        imageContainer.getChildren().add(imageView);
                    } else {
                       
                        showAlert(Alert.AlertType.WARNING, "Imagine duplicată", "Imaginea " + file.getName() + " este deja adăugată.");
                    }
                }
            }
        });


        Button saveButton = new Button("Salvează Modificări");
        saveButton.setOnAction(e -> {
            if (nameInput.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Eroare", "Toate câmpurile sunt obligatorii.");
                return;
            }

            if (!GameDBC.isGameNameUnique(nameInput.getText().trim(), gameToEdit.getId())) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Numele jocului există deja.");
                return;
            }

            gameToEdit.setName(nameInput.getText().trim());
            gameToEdit.setGenre(genreInput.getText().trim());
            gameToEdit.setReleaseYear(Integer.parseInt(releaseYearInput.getText().trim()));
            gameToEdit.setDescription(descriptionInput.getText().trim());

            if (selectedIcon[0] != null) {
                gameToEdit.setIconPath(selectedIcon[0].toURI().toString());
            }

            if (GameDBC.updateGame(gameToEdit)) {
                primaryStage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Modificările nu au putut fi salvate.");
            }
        });

        layout.getChildren().addAll(
                nameInput, genreInput, releaseYearInput, descriptionInput,
                iconLabel, iconButton,
                new Label("Imagini existente:"), imageContainer,
                selectedImageLabel, currentlySelectedImageView,
                deleteImageButton, addImagesButton,saveButton
        );

        Scene scene = new Scene(layout, 700, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Afișează o alertă utilizatorului cu mesajul specificat.
     * 
     * @param type Tipul alertei (INFORMATION, WARNING, ERROR).
     * @param title Titlul alertei.
     * @param message Mesajul afișat în alertă.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
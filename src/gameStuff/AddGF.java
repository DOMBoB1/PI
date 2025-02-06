
package gameStuff;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Admin;
import model.Game;
import ui.Back;
import bd.GameDBC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Clasa `AddGF` reprezintă interfața grafică pentru adăugarea de jocuri în aplicație.
 * Aceasta permite administratorilor să completeze informații despre jocuri, precum numele, genul,
 * anul apariției, dezvoltatorul și descrierea. De asemenea, suportă încărcarea unei iconițe 
 * și a imaginilor suplimentare.
 * 
 * Această clasă interacționează cu baza de date prin clasa {@link GameDBC} pentru a salva 
 * informațiile despre jocuri.
 * 
 * <p>Clasa este destinată exclusiv utilizării de către administratorii aplicației.</p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class AddGF {

    private final Admin currentAdmin;

    /**
     * Constructorul clasei `AddGF`.
     * 
     * @param admin Administratorul curent care inițiază interfața de adăugare a jocurilor.
     */
    public AddGF(Admin admin) {
        this.currentAdmin = admin;
    }

    /**
     * Creează interfața grafică pentru adăugarea unui joc nou.
     * 
     * @param primaryStage Fereastra principală a aplicației.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Adaugă Joc");

        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        TextField nameInput = new TextField();
        nameInput.setPromptText("Nume joc");

        TextField genreInput = new TextField();
        genreInput.setPromptText("Gen joc");

        TextField releaseYearInput = new TextField();
        releaseYearInput.setPromptText("Anul apariției (yyyy)");

        TextField developerInput = new TextField();
        developerInput.setPromptText("Dezvoltator joc");

        TextArea descriptionInput = new TextArea();
        descriptionInput.setPromptText("Descriere joc");

        Label iconLabel = new Label("Iconiță joc");
        Button iconButton = new Button("Selectează Iconiță");
        final File[] selectedIcon = {null};

        iconButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selectează Iconiță");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                selectedIcon[0] = file;
                iconLabel.setText("Iconiță selectată: " + file.getName());
            }
        });

        Label imageSectionLabel = new Label("Imagini suplimentare (opțional)");
        FlowPane imageContainer = new FlowPane();
        imageContainer.setHgap(10);
        imageContainer.setVgap(10);
        List<File> selectedImages = new ArrayList<>();

        Button uploadImagesButton = new Button("Adaugă Imagini");
        uploadImagesButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selectează Imagini");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagini", "*.png", "*.jpg", "*.jpeg"));
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                for (File file : files) {
                    if (!selectedImages.contains(file)) {
                        selectedImages.add(file);

                        ImageView imageView = new ImageView(new Image(file.toURI().toString()));
                        imageView.setFitWidth(100);
                        imageView.setFitHeight(100);
                        imageView.setPreserveRatio(true);
                        imageContainer.getChildren().add(imageView);
                    }
                }
            }
        });

        Button saveButton = new Button("Adaugă Joc");
        saveButton.setOnAction(e -> {
            String name = nameInput.getText().trim();
            String genre = genreInput.getText().trim();
            String releaseYearText = releaseYearInput.getText().trim();
            String developer = developerInput.getText().trim();
            String description = descriptionInput.getText().trim();

            if (name.isEmpty() || genre.isEmpty() || releaseYearText.isEmpty() || developer.isEmpty() || description.isEmpty() || selectedIcon[0] == null) {
                showAlert(Alert.AlertType.WARNING, "Câmpuri incomplete", "Completează toate câmpurile și alege o iconiță.");
                return;
            }

            try {
                int releaseYear = Integer.parseInt(releaseYearText);
                Game newGame = new Game(0, name, description, 0, genre, releaseYear, developer, selectedIcon[0].toURI().toString());
                int gameId = GameDBC.addGame(newGame);

                if (gameId != -1) {
                    if (!selectedImages.isEmpty()) {
                        GameDBC.addGameImages(gameId, selectedImages);
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Jocul a fost adăugat cu succes!");
                    primaryStage.close();
                    new GLB(currentAdmin).start(new Stage());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Nu s-a putut adăuga jocul.");
                }
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Anul apariției trebuie să fie un număr valid.");
            }
        });

        Button backButton = new Button("Anulează");
        backButton.setOnAction(e -> {
            primaryStage.close();
            new GLB(currentAdmin).start(new Stage());
        });

        layout.getChildren().addAll(nameInput, genreInput, releaseYearInput, developerInput, descriptionInput,
                iconLabel, iconButton, imageSectionLabel, imageContainer, uploadImagesButton, saveButton, backButton);

        Scene scene = new Scene(layout, 600, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Afișează un dialog de alertă utilizatorului.
     * 
     * @param alertType Tipul alertei (WARNING, INFORMATION, ERROR etc.).
     * @param title     Titlul alertei.
     * @param message   Mesajul afișat în alertă.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

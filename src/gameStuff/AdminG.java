package gameStuff;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Game;
import ui.AdminM;
import ui.Back;
import bd.GameDBC;

/**
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 * 
 * Clasa AdminG oferă o interfață grafică pentru administrarea librăriei de jocuri. 
 * Permite adăugarea, modificarea, ștergerea și vizualizarea detaliilor unui joc existent în baza de date.
 */
public class AdminG {

	private final model.Admin admin;

	public AdminG(model.Admin admin) {
	    this.admin = admin;
	}

    /**
     * Inițializează și afișează interfața grafică pentru gestionarea librăriei de jocuri.
     * 
     * @param primaryStage Fereastra principală în care se va afișa interfața.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestionare Librărie Jocuri");

        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        ObservableList<Game> games = FXCollections.observableArrayList(GameDBC.getAllGames());
        ListView<Game> gameList = new ListView<>(games);

        Button addGameButton = new Button("Adaugă Joc");
        addGameButton.setOnAction(e -> {
            Stage currentStage = (Stage) addGameButton.getScene().getWindow();
            currentStage.close();
            new AddGF(admin).start(new Stage());
        });

        Button editGameButton = new Button("Modifică Joc");
        editGameButton.setOnAction(e -> {
            Game selectedGame = gameList.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                new EditGF(selectedGame, admin).start(new Stage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a modifica.");
            }
        });

        Button deleteGameButton = new Button("Șterge Joc");
        deleteGameButton.setOnAction(e -> {
            Game selectedGame = gameList.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                if (GameDBC.deleteGame(selectedGame.getId())) {
                    games.remove(selectedGame);
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Jocul a fost șters.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Jocul nu a putut fi șters.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a șterge.");
            }
        });

        Button viewGameButton = new Button("Vizualizează Joc");
        viewGameButton.setOnAction(e -> {
            Game selectedGame = gameList.getSelectionModel().getSelectedItem();
            if (selectedGame != null) {
                new VGD(selectedGame, 1).start(new Stage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a vizualiza.");
            }
        });

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> {
            primaryStage.close();
            new AdminM(admin).start(new Stage());
        });


        HBox buttons = new HBox(10, addGameButton, editGameButton, deleteGameButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(new Label("Librăria Jocurilor"), gameList, buttons);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    /**
     * Afișează o alertă cu un mesaj specificat utilizatorului.
     * 
     * @param alertType Tipul alertei (INFORMATION, WARNING, ERROR).
     * @param title Titlul alertei.
     * @param message Mesajul de afișat.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


package gameStuff;

import bd.GameDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Admin;
import model.Game;
import ui.AdminM;
import ui.Back;
/**
 * Clasa `GLB` gestionează interfața grafică pentru afișarea și gestionarea bibliotecii de jocuri.
 * Aceasta permite utilizatorilor să vizualizeze, adauge, editeze, și șteargă jocuri din biblioteca lor.
 * <p>
 * Clasa interacționează cu baza de date prin clasa {@link GameDBC} și utilizează mai multe componente
 * din pachetul `ui` pentru navigare și gestionare.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class GLB {

    private Game selectedGame;
    private Admin currentAdmin;

    /**
     * Constructorul clasei `GLB`.
     * 
     * @param admin Administratorul curent care gestionează biblioteca de jocuri.
     */
    public GLB(Admin admin) {
        this.currentAdmin = admin;
    }

    /**
     * Începe interfața grafică pentru afișarea bibliotecii de jocuri.
     * 
     * @param primaryStage Fereastra principală a aplicației.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Librăria Jocurilor");

        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        FlowPane gamesContainer = new FlowPane();
        gamesContainer.setPadding(new Insets(20));
        gamesContainer.setHgap(20);
        gamesContainer.setVgap(20);

        ObservableList<Game> games = FXCollections.observableArrayList(GameDBC.getAllGames());
        updateGameCards(games, gamesContainer);

        ScrollPane scrollPane = new ScrollPane(gamesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        Button addGameButton = new Button("Adaugă Joc");
        addGameButton.setOnAction(e -> {
            primaryStage.close();
            new AddGF(currentAdmin).start(new Stage());
        });

        Button viewGameButton = new Button("Vizualizează Joc");
        viewGameButton.setOnAction(e -> {
            if (selectedGame != null) {
                new VGD(selectedGame, 1).start(new Stage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a vizualiza.");
            }
        });

        Button editGameButton = new Button("Modifică Joc");
        editGameButton.setOnAction(e -> {
            if (selectedGame != null) {
                new EditGF(selectedGame, currentAdmin).start(new Stage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a modifica.");
            }
        });

        Button deleteGameButton = new Button("Șterge Joc");
        deleteGameButton.setOnAction(e -> {
            if (selectedGame != null) {
                if (GameDBC.deleteGame(selectedGame.getId())) {
                    games.remove(selectedGame);
                    updateGameCards(games, gamesContainer);
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Jocul a fost șters.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Jocul nu a putut fi șters.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a șterge.");
            }
        });

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> {
            primaryStage.close();
            new AdminM(currentAdmin).start(new Stage());
        });

        HBox buttons = new HBox(10, addGameButton, viewGameButton, editGameButton, deleteGameButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(scrollPane, buttons);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Resetează selecțiile din containerul de jocuri.
     * 
     * @param gamesContainer Containerul care afișează cardurile jocurilor.
     */
    private void clearSelections(FlowPane gamesContainer) {
        for (var node : gamesContainer.getChildren()) {
            if (node instanceof VBox) {
                ((VBox) node).setStyle("-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 2; -fx-border-radius: 5;");
            }
        }
    }

    /**
     * Actualizează afișarea cardurilor jocurilor din container.
     * 
     * @param games          Lista de jocuri care trebuie afișate.
     * @param gamesContainer Containerul pentru cardurile jocurilor.
     */
    private void updateGameCards(ObservableList<Game> games, FlowPane gamesContainer) {
        gamesContainer.getChildren().clear();
        for (Game game : games) {
            VBox gameCard = createGameCard(game);
            gameCard.setOnMouseClicked(event -> {
                selectedGame = game;
                clearSelections(gamesContainer);
                gameCard.setStyle("-fx-border-color: blue; -fx-border-width: 3; -fx-border-radius: 5;");
            });
            gamesContainer.getChildren().add(gameCard);
        }
    }

    /**
     * Creează un card grafic pentru un joc.
     * 
     * @param game Obiectul {@link Game} pentru care se creează cardul.
     * @return Obiectul {@link VBox} reprezentând cardul grafic al jocului.
     */
    private VBox createGameCard(Game game) {
        VBox gameCard = new VBox(10);
        gameCard.setAlignment(Pos.CENTER);
        gameCard.setPadding(new Insets(10));
        gameCard.setStyle("-fx-border-color: rgba(255, 255, 255, 0.5); -fx-border-width: 2; -fx-border-radius: 5;");

        ImageView gameIcon = new ImageView();
        if (game.getIconPath() != null && !game.getIconPath().isEmpty()) {
            gameIcon.setImage(new Image(game.getIconPath()));
        } else {
            gameIcon.setImage(new Image("https://via.placeholder.com/100"));
        }
        gameIcon.setFitHeight(100);
        gameIcon.setFitWidth(100);
        gameIcon.setPreserveRatio(true);

        Label gameName = new Label(game.getName());
        Label gameGenre = new Label("Gen: " + game.getGenre());
        Label gameReleaseYear = new Label("Lansare: " + game.getReleaseYear());

        gameCard.getChildren().addAll(gameIcon, gameName, gameGenre, gameReleaseYear);
        return gameCard;
    }

    /**
     * Afișează un mesaj de alertă utilizatorului.
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

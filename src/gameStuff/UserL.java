package gameStuff;

import bd.GameDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Game;
import model.User;
import ui.Back;
import ui.UserM;

/**
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 * 
 * Clasa UserL reprezintă interfața grafică pentru vizualizarea librăriei personale a utilizatorului.
 * Utilizatorul poate vizualiza, adăuga jocuri și accesa detalii despre jocurile din librăria personală.
 */
public class UserL {

    private final User user; // Utilizatorul conectat
    private Game selectedGame = null; // Referință către jocul selectat

    /**
     * Constructorul clasei UserL.
     * 
     * @param user Obiectul User care reprezintă utilizatorul curent.
     */
    public UserL(User user) {
        this.user = user;
    }

    /**
     * Inițializează interfața grafică pentru librăria personală a utilizatorului.
     * 
     * @param primaryStage Fereastra principală în care este afișată interfața.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Librăria Personală");

        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        FlowPane gamesContainer = new FlowPane(20, 20);
        gamesContainer.setPadding(new Insets(20));
        gamesContainer.setAlignment(Pos.CENTER);

     
        ObservableList<Game> personalLibrary = FXCollections.observableArrayList(GameDBC.getUserLibrary(user.getId()));
        updateGameCards(personalLibrary, gamesContainer);

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(gamesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

  
        Button viewGameButton = new Button("Detalii Joc");
        viewGameButton.setOnAction(e -> {
            if (selectedGame != null) {
                new VGD(selectedGame, user.getId()).start(new Stage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a vedea detaliile.");
            }
        });

        Button deleteGameButton = new Button("Șterge Joc");
        deleteGameButton.setPrefWidth(200);
        deleteGameButton.setOnAction(e -> {
            if (selectedGame != null) {
                boolean success = GameDBC.removeGameFromUserLibrary(user.getId(), selectedGame.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Jocul a fost șters din librăria personală!");
                    
                    ObservableList<Game> updatedLibrary = FXCollections.observableArrayList(GameDBC.getUserLibrary(user.getId()));
                    updateGameCards(updatedLibrary, gamesContainer);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Jocul nu a putut fi șters.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a-l șterge.");
            }
        });


        Button backButton = new Button("Înapoi");
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> new UserM(user).start(primaryStage));

        HBox buttons = new HBox(10, viewGameButton, deleteGameButton, backButton);
        buttons.setAlignment(Pos.CENTER);


        layout.getChildren().addAll(new Label("Librăria Personală"), scrollPane, buttons);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Resetează selecția jocurilor din container.
     * 
     * @param gamesContainer Containerul jocurilor.
     */
    private void clearSelections(FlowPane gamesContainer) {
        for (var node : gamesContainer.getChildren()) {
            if (node instanceof VBox) {
                ((VBox) node).setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
            }
        }
    }

    /**
     * Actualizează cardurile jocurilor afișate în interfață.
     * 
     * @param games          Lista jocurilor afișate.
     * @param gamesContainer Containerul în care sunt afișate jocurile.
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
     * Creează carduri pentru jocurile afișate.
     * 
     * @param game Obiectul Game pentru care se creează cardul.
     * @return VBox reprezentând cardul jocului.
     */
    private VBox createGameCard(Game game) {
        VBox gameCard = new VBox(10);
        gameCard.setAlignment(Pos.CENTER);
        gameCard.setPadding(new Insets(10));
        gameCard.setStyle("-fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 5;");

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
     * Afișează o alertă pentru utilizator.
     * 
     * @param alertType Tipul alertei.
     * @param title     Titlul alertei.
     * @param message   Mesajul afișat.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

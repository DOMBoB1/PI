
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Game;
import model.User;
import ui.Back;
import ui.UserM;

import java.util.Comparator;
import java.util.List;
/**
 * Clasa `UserG` reprezintă interfața grafică pentru utilizatori, unde aceștia pot vizualiza și interacționa cu lista de jocuri disponibile.
 * Aceasta permite utilizatorilor să vizualizeze detalii despre jocuri, să adauge jocuri în biblioteca personală
 * și să sorteze jocurile în funcție de diverse criterii.
 * <p>
 * Clasa utilizează componenta `GameDBC` pentru a obține și manipula datele din baza de date.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class UserG {

    private final User user;
    private Game selectedGame = null; 
    private ObservableList<Game> gamesList; 

    /**
     * Constructorul clasei `UserG`.
     * 
     * @param user Obiectul {@link User} reprezentând utilizatorul curent.
     */
    public UserG(User user) {
        this.user = user;
    }

    /**
     * Începe interfața grafică pentru vizualizarea jocurilor disponibile.
     * 
     * @param primaryStage Fereastra principală a aplicației.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Vizualizare Jocuri");

        VBox layout = new VBox(15);
        Back.setBackground(layout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        Back.setBackground(layout);

        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        FlowPane gamesContainer = new FlowPane(20, 20);
        gamesContainer.setPadding(new Insets(20));
        gamesContainer.setAlignment(Pos.CENTER);

        gamesList = FXCollections.observableArrayList(GameDBC.getAllGames());
        updateGameCards(gamesList, gamesContainer);

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(gamesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        ComboBox<String> sortByComboBox = new ComboBox<>();
        sortByComboBox.getItems().addAll("Anul Lansării", "Nume", "Gen", "Rating");
        sortByComboBox.setValue("Anul Lansării"); 
        sortByComboBox.setOnAction(e -> {
            String selectedSort = sortByComboBox.getValue();
            sortGames(selectedSort);
            updateGameCards(gamesList, gamesContainer);
        });

        Button viewGameButton = new Button("Detalii Joc");
        viewGameButton.setOnAction(e -> {
            if (selectedGame != null) {
                new VGD(selectedGame, user.getId()).start(new Stage());
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc pentru a vedea detaliile.");
            }
        });
        
        Button addGameButton = new Button("Adaugă Joc");
        addGameButton.setPrefWidth(200);
        addGameButton.setOnAction(e -> {
            if (selectedGame != null) {
                boolean success = GameDBC.addGameToUserLibrary(user.getId(), selectedGame.getId());
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succes", "Jocul a fost adăugat în librăria personală!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Eroare", "Jocul nu a putut fi adăugat.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Selecție Incompletă", "Selectați un joc înainte de a-l adăuga.");
            }
        });

        Button backButton = new Button("Înapoi");
        backButton.setPrefWidth(200);
        backButton.setOnAction(e -> new UserM(user).start(primaryStage));

        HBox sortBox = new HBox(10, new Label("Sortează după:"), sortByComboBox);
        sortBox.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(10, viewGameButton, addGameButton, backButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(new Label("Jocuri Disponibile"), sortBox, scrollPane, buttons);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sortează lista de jocuri în funcție de criteriul selectat.
     * 
     * @param criterion Criteriul de sortare.
     */
    private void sortGames(String criterion) {
        switch (criterion) {
            case "Anul Lansării":
                gamesList.sort(Comparator.comparingInt(Game::getReleaseYear));
                break;
            case "Nume":
                gamesList.sort(Comparator.comparing(Game::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Gen":
                gamesList.sort(Comparator.comparing(Game::getGenre, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Rating":
                gamesList.sort(Comparator.comparingDouble(Game::getRating));
                break;
        }
    }

    /**
     * Actualizează lista cardurilor jocurilor afișate.
     * 
     * @param games          Lista jocurilor din baza de date.
     * @param gamesContainer Containerul în care sunt afișate jocurile.
     */
    private void updateGameCards(List<Game> games, FlowPane gamesContainer) {
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
     * Creează un card pentru afișarea informațiilor despre joc.
     * 
     * @param game Jocul pentru care se creează cardul.
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
        Label gameRating = new Label("Rating: " + game.getRating());

        gameCard.getChildren().addAll(gameIcon, gameName, gameGenre, gameReleaseYear, gameRating);
        return gameCard;
    }

    /**
     * Resetează selecția cardurilor.
     * 
     * @param gamesContainer Containerul în care sunt afișate cardurile.
     */
    private void clearSelections(FlowPane gamesContainer) {
        for (var node : gamesContainer.getChildren()) {
            if (node instanceof VBox) {
                ((VBox) node).setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
            }
        }
    }

    /**
     * Afișează o alertă pentru utilizator.
     * 
     * @param alertType Tipul alertei.
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

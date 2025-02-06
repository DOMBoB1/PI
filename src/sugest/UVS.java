package sugest;

import bd.GameSDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Sugest;
import model.User;
import ui.Back;
import ui.UserM;

import java.util.List;

public class UVS {

    private final User user;

    public UVS(User user) {
        this.user = user;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Votează Sugestii");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        Back.setBackground(layout);

        Label title = new Label("Sugestii Disponibile pentru Vot");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<Sugest> suggestions = GameSDBC.getAllSuggestionsExceptUser(user.getId());
        ObservableList<Sugest> suggestionList = FXCollections.observableArrayList(suggestions);

        ListView<Sugest> suggestionListView = new ListView<>(suggestionList);
        suggestionListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Sugest suggestion, boolean empty) {
                super.updateItem(suggestion, empty);
                if (empty || suggestion == null || suggestion.getGameName() == null) {
                    setText(null);
                } else {
                    setText(suggestion.getGameName() + " - Likes: " + suggestion.getLikes() + " | Dislikes: " + suggestion.getDislikes());
                }
            }
        });

        Button likeButton = new Button("Like");
        likeButton.setDisable(true);

        Button dislikeButton = new Button("Dislike");
        dislikeButton.setDisable(true);

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> new UserM(user).start(primaryStage));

        suggestionListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            likeButton.setDisable(newSelection == null);
            dislikeButton.setDisable(newSelection == null);
        });

        likeButton.setOnAction(e -> {
            Sugest selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = GameSDBC.voteSuggestion(user.getId(), selected.getId(), true);
                if (success) {
                    selected.setLikes(selected.getLikes() + 1);
                    suggestionListView.refresh();
                } else {
                    showErrorMessage("Eroare", "Ați votat deja această sugestie.");
                }
            }
        });

        dislikeButton.setOnAction(e -> {
            Sugest selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                boolean success = GameSDBC.voteSuggestion(user.getId(), selected.getId(), false);
                if (success) {
                    selected.setDislikes(selected.getDislikes() + 1);
                    suggestionListView.refresh();
                } else {
                    showErrorMessage("Eroare", "Ați votat deja această sugestie.");
                }
            }
        });

        HBox buttonBox = new HBox(10, likeButton, dislikeButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(title, suggestionListView, buttonBox);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

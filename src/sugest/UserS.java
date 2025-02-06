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

public class UserS {

    private final User user;

    public UserS(User user) {
        this.user = user;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sugestiile mele");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        Back.setBackground(layout);


        Label title = new Label("Sugestiile mele");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        List<Sugest> suggestions = GameSDBC.getSuggestionsByUser(user.getId());
        ObservableList<Sugest> suggestionList = FXCollections.observableArrayList(suggestions);

        ListView<Sugest> suggestionListView = new ListView<>(suggestionList);
        suggestionListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Sugest suggestion, boolean empty) {
                super.updateItem(suggestion, empty);
                if (empty || suggestion == null || suggestion.getGameName() == null) {
                    setText(null);
                } else if (suggestion.getStatus().equals("rejected")) {
                    setText(suggestion.getGameName() + " - rejected (Motiv: " + suggestion.getRejectionReason() + ")");
                } else {
                    setText(suggestion.getGameName() + " - " + suggestion.getStatus());
                }
            }
        });

        Button addSuggestionButton = new Button("Adaugă Sugestie");
        addSuggestionButton.setOnAction(e -> showAddSuggestionDialog(suggestionList));

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> new UserM(user).start(primaryStage));

        layout.getChildren().addAll(title, suggestionListView, addSuggestionButton, backButton);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAddSuggestionDialog(ObservableList<Sugest> suggestionList) {
        Stage dialog = new Stage();
        dialog.setTitle("Adaugă Sugestie");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        TextField gameNameField = new TextField();
        gameNameField.setPromptText("Nume Joc");

        TextField officialDescriptionField = new TextField();
        officialDescriptionField.setPromptText("Descriere Oficială");

        TextField developerField = new TextField();
        developerField.setPromptText("Dezvoltator");

        TextField releaseYearField = new TextField();
        releaseYearField.setPromptText("Anul Lansării");

        TextField userReasonField = new TextField();
        userReasonField.setPromptText("Motivul Tău");

        Button submitButton = new Button("Trimite Sugestia");
        submitButton.setOnAction(e -> {
            if (GameSDBC.hasPendingSuggestion(user.getId())) {
                showErrorMessage("Eroare", "Deja aveți o sugestie în așteptare. Așteptați procesarea acesteia înainte de a adăuga alta.");
                return;
            }

            String gameName = gameNameField.getText().trim();
            String officialDescription = officialDescriptionField.getText().trim();
            String developer = developerField.getText().trim();
            String releaseYearText = releaseYearField.getText().trim();
            String userReason = userReasonField.getText().trim();

            
            if (gameName.isEmpty() || officialDescription.isEmpty() || developer.isEmpty() || releaseYearText.isEmpty() || userReason.isEmpty()) {
                showErrorMessage("Eroare", "Completați toate câmpurile.");
                return;
            }

           
            int currentYear = java.time.Year.now().getValue();
            int releaseYear;
            try {
                releaseYear = Integer.parseInt(releaseYearText);
                if (releaseYear < 1962 || releaseYear > currentYear) {
                    showErrorMessage("Eroare", "Introduceți un an valid între 1962 și " + currentYear + ".");
                    return;
                }
            } catch (NumberFormatException ex) {
                showErrorMessage("Eroare", "Anul lansării trebuie să fie un număr.");
                return;
            }

            
            boolean success = GameSDBC.addSuggestion(user.getId(), gameName, officialDescription, developer, releaseYear, userReason);
            if (success) {
                suggestionList.add(new Sugest(0, user.getId(), gameName, officialDescription, developer, releaseYear, userReason, "pending", null, 0, 0));
                dialog.close();
            } else {
                showErrorMessage("Eroare", "Sugestia nu a putut fi adăugată.");
            }
        });

        layout.getChildren().addAll(
                new Label("Completează detaliile sugestiei:"),
                gameNameField, officialDescriptionField, developerField, releaseYearField, userReasonField, submitButton
        );

        Scene scene = new Scene(layout, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }


    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

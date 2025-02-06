
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
import model.Admin;
import model.Sugest;
import ui.AdminM;
import ui.Back;

import java.util.List;
/**
 * Clasa `AdiminS` gestionează interfața grafică pentru administrarea sugestiilor de jocuri oferite de utilizatori.
 * Aceasta permite filtrarea sugestiilor în funcție de status, vizualizarea detaliilor, aprobarea și respingerea sugestiilor.
 * <p>
 * Interacționează cu baza de date prin clasa {@link GameSDBC} pentru manipularea datelor sugestiilor.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class AdiminS {

    private final Admin admin;

    /**
     * Constructorul clasei `AdiminS`.
     * 
     * @param admin Administratorul curent care gestionează sugestiile.
     */
    public AdiminS(Admin admin) {
        this.admin = admin;
    }

    /**
     * Începe interfața grafică pentru gestionarea sugestiilor de jocuri.
     * 
     * @param primaryStage Fereastra principală a aplicației.
     */
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestionare Sugestii");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        Back.setBackground(layout);
        
        Label title = new Label("Gestionare Sugestii");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Pending", "Approved", "Rejected");
        statusFilter.setValue("Pending");

        ObservableList<Sugest> suggestionList = FXCollections.observableArrayList();
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

        Button viewDetailsButton = new Button("Vezi Detalii");
        Button approveButton = new Button("Aprobă Sugestia");
        Button rejectButton = new Button("Respinge Sugestia");
        Button backButton = new Button("Înapoi");

        viewDetailsButton.setDisable(true);
        approveButton.setDisable(true);
        rejectButton.setDisable(true);

        statusFilter.setOnAction(e -> {
            String selectedStatus = statusFilter.getValue().toLowerCase();
            List<Sugest> filteredSuggestions = GameSDBC.getSuggestionsByStatus(selectedStatus);
            suggestionList.setAll(filteredSuggestions);

            boolean isPending = selectedStatus.equals("pending");
            approveButton.setDisable(!isPending);
            rejectButton.setDisable(!isPending);
        });

        suggestionListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewDetailsButton.setDisable(newSelection == null);
        });

        approveButton.setOnAction(e -> {
            Sugest selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                GameSDBC.updateSuggestionStatus(selected.getId(), "approved", null);
                suggestionList.remove(selected);
            }
        });

        rejectButton.setOnAction(e -> {
            Sugest selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Respinge Sugestia");
                dialog.setHeaderText("Introduceți motivul respingerii:");
                dialog.setContentText("Motiv:");
                dialog.showAndWait().ifPresent(reason -> {
                    GameSDBC.updateSuggestionStatus(selected.getId(), "rejected", reason);
                    suggestionList.remove(selected);
                });
            }
        });

        viewDetailsButton.setOnAction(e -> {
            Sugest selected = suggestionListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showSuggestionDetails(selected);
            }
        });

        backButton.setOnAction(e -> new AdminM(admin).start(primaryStage));

        HBox buttonBox = new HBox(10, viewDetailsButton, approveButton, rejectButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(title, statusFilter, suggestionListView, buttonBox);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        statusFilter.getOnAction().handle(null);
    }

    /**
     * Afișează o fereastră cu detaliile unei sugestii selectate.
     * 
     * @param suggestion Sugestia pentru care se afișează detaliile.
     */
    private void showSuggestionDetails(Sugest suggestion) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalii Sugestie");
        alert.setHeaderText("Detalii pentru: " + suggestion.getGameName());
        alert.setContentText(
                "Descriere: " + suggestion.getOfficialDescription() + "\n" +
                        "Dezvoltator: " + suggestion.getDeveloper() + "\n" +
                        "An Lansare: " + suggestion.getReleaseYear() + "\n" +
                        "Motiv Utilizator: " + suggestion.getUserReason() + "\n" +
                        "Likes: " + suggestion.getLikes() + ", Dislikes: " + suggestion.getDislikes() + "\n" +
                        "Status: " + suggestion.getStatus() + "\n" +
                        (suggestion.getRejectionReason() != null ? "Motiv Respinger: " + suggestion.getRejectionReason() : "")
        );
        alert.showAndWait();
    }
}

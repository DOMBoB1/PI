package ui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Admin;
import sugest.AdiminS;
import bd.DBC;
import gameStuff.GLB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class AdminM {
    private Admin admin;

    public AdminM(Admin admin) {
        if (admin != null && admin.getUsername() != null && !admin.getUsername().isEmpty()) {
            this.admin = admin;
        } else {
            this.admin = new Admin(0, "Fără Nume", null, null, null, null, null); 
        }
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Meniul Administratorului");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Back.setBackground(layout);
        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        Text welcomeText = new Text("Bine ai venit, Administrator " + (admin.getUsername() != null ? admin.getUsername() : "Fără Nume") + "!");
        welcomeText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button profileButton = new Button("Profilul Meu");
        profileButton.setPrefWidth(200);
        profileButton.setOnAction(e -> new ProfilA(admin).start(primaryStage));

        Button manageUsersButton = new Button("Gestionare Utilizatori");
        manageUsersButton.setPrefWidth(200);
        manageUsersButton.setOnAction(e -> new AdMU(admin).start(primaryStage));

        Button manageGamesButton = new Button("Gestionare Librărie Jocuri");
        manageGamesButton.setPrefWidth(200);
        manageGamesButton.setOnAction(e -> new GLB(admin).start(primaryStage));

        
        Button suggestionsButton = new Button("Gestionare Sugestii");
        suggestionsButton.setPrefWidth(200);
        suggestionsButton.setOnAction(e -> new AdiminS(admin).start(primaryStage));


        Button logoutButton = new Button("Deconectare");
        logoutButton.setPrefWidth(200);
        logoutButton.setOnAction(e -> new Login().start(primaryStage));

        Button deleteAccountButton = new Button("Șterge Contul");
        deleteAccountButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteAccountButton.setPrefWidth(200);
        deleteAccountButton.setOnAction(e -> showConfirmationDialog(primaryStage));

        layout.getChildren().addAll(welcomeText, profileButton, manageUsersButton, manageGamesButton, suggestionsButton, logoutButton, deleteAccountButton);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void showConfirmationDialog(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmare Ștergere Cont");
        alert.setHeaderText("Sigur doriți să vă ștergeți contul?");
        alert.setContentText("Această acțiune nu poate fi anulată!");

        ButtonType deleteButton = new ButtonType("Șterge Contul");
        ButtonType cancelButton = new ButtonType("Anulează", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(deleteButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == deleteButton) {
            deleteAccountFromDatabase();
            primaryStage.close();
        }
    }

    private void deleteAccountFromDatabase() {
       
        Task<Void> deleteTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try (Connection connection = DBC.getConnection()) {
                    String deleteQuery = "DELETE FROM admins WHERE id = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setInt(1, admin.getId());

                    int rowsAffected = deleteStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        // Afișare mesaj pe UI
                        javafx.application.Platform.runLater(() -> showSuccessMessage("Contul a fost șters cu succes!"));
                        System.exit(0);
                    } else {
                        javafx.application.Platform.runLater(() -> showErrorMessage("Eroare la ștergerea contului."));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> showErrorMessage("Eroare la conectarea la baza de date."));
                }
                return null;
            }
        };

       
        Thread thread = new Thread(deleteTask);
        thread.setDaemon(true); 
        thread.start();
    }


    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succes");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}

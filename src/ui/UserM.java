package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import sugest.SV;
import sugest.UserS;
import gameStuff.UserG;
import gameStuff.UserL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import bd.DBC;

public class UserM {

    private User user;

    public UserM(User user) {
        this.user = user;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Meniul Utilizator");
        

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Back.setBackground(layout);

        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        Text welcomeText = new Text("Bine ai venit, " + user.getUsername() + "!");
        welcomeText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

     
        Button profileButton = new Button("Profilul Meu");
        profileButton.setPrefWidth(200);
        profileButton.setOnAction(e -> {
            if (user != null) {
                new Profil(user).start(primaryStage);
            } else {
                showErrorMessage("Eroare", "Datele utilizatorului nu sunt disponibile.");
            }
        });

     
        Button manageLibraryButton = new Button("Librăria Generală");
        manageLibraryButton.setPrefWidth(200);
        manageLibraryButton.setOnAction(e -> new UserG(user).start(primaryStage));


        Button personalLibraryButton = new Button("Librăria Personală");
        personalLibraryButton.setPrefWidth(200);
        personalLibraryButton.setOnAction(e -> new UserL(user).start(primaryStage));

        Button suggestionsButton = new Button("Sugestii");
        suggestionsButton.setPrefWidth(200);
        suggestionsButton.setOnAction(e -> new SV(user).start(primaryStage));

        Button mySuggestionsButton = new Button("Sugestiile Mele");
        mySuggestionsButton.setPrefWidth(200);
        mySuggestionsButton.setOnAction(e -> new UserS(user).start(primaryStage));

       

        Button logoutButton = new Button("Deconectare");
        logoutButton.setPrefWidth(200);
        logoutButton.setOnAction(e -> new Login().start(primaryStage));

      
        Button deleteAccountButton = new Button("Șterge Contul");
        deleteAccountButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteAccountButton.setPrefWidth(200);
        deleteAccountButton.setOnAction(e -> {
            showConfirmationDialog(primaryStage);
        });

     
        VBox layoutContent = new VBox(15, welcomeText, profileButton, manageLibraryButton, personalLibraryButton, suggestionsButton,mySuggestionsButton, logoutButton, deleteAccountButton);

        layoutContent.setAlignment(Pos.CENTER);
        layout.getChildren().add(layoutContent);

        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showConfirmationDialog(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmare Ștergere Cont");
        alert.setHeaderText("Sigur doriți să vă ștergeți contul?");
        alert.setContentText("Această acțiune este permanentă!");

        ButtonType buttonTypeYes = new ButtonType("Șterge Contul", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Anulează", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeCancel);

        alert.showAndWait().ifPresent(result -> {
            if (result == buttonTypeYes) {
                deleteAccountFromDatabase();
                primaryStage.close();
                new Login().start(new Stage()); 
            }
        });
    }

    private void deleteAccountFromDatabase() {
        try (Connection connection = DBC.getConnection()) {
            String deleteQuery = "DELETE FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, user.getId());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                showSuccessMessage("Contul tău a fost șters cu succes!");
            } else {
                showErrorMessage("Eroare", "Nu am putut șterge contul. Încercați din nou.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Eroare", "A apărut o problemă la conectarea la baza de date.");
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succes");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

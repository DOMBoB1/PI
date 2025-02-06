package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Admin;
import model.User;
import bd.DBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MU {

    private Admin admin;  
    public MU(Admin admin) { 
        this.admin = admin;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestionare Utilizatori");

  
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);


        ListView<User> userListView = new ListView<>();
        populateUserList(userListView);


        Button viewUserButton = new Button("Vezi Detalii Utilizator");
        viewUserButton.setOnAction(e -> viewUserDetails(userListView.getSelectionModel().getSelectedItem(), primaryStage));

      
        Button deleteUserButton = new Button("Șterge Utilizator");
        deleteUserButton.setOnAction(e -> deleteUser(userListView.getSelectionModel().getSelectedItem(), userListView, primaryStage));

      
        grid.add(new Label("Lista Utilizatori:"), 0, 0);
        grid.add(userListView, 1, 0);
        grid.add(viewUserButton, 1, 1);
        grid.add(deleteUserButton, 1, 2);

    
        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> {
          
            new AdminM(admin).start(primaryStage);
        });
        grid.add(backButton, 1, 3);

   
        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void populateUserList(ListView<User> userListView) {
        try (Connection connection = DBC.getConnection()) {
            String query = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String bio = resultSet.getString("bio");

            
                User user = new User(id, username, email, bio, null, bio);  

              
                userListView.getItems().add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Eroare la conectarea la baza de date!");
        }
    }

    private void viewUserDetails(User user, Stage primaryStage) {
        if (user == null) {
            showErrorMessage("Te rog selectează un utilizator.");
            return;
        }

  
        Stage detailsStage = new Stage();
        detailsStage.setTitle("Detalii Utilizator");

       
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

    
        Text usernameText = new Text("Username: " + user.getUsername());
        usernameText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

  
        Text emailText = new Text("Email: " + user.getEmail());

    
        Text bioText = new Text("Bio: " + user.getBio());

       
        VBox detailsBox = new VBox(10, usernameText, emailText, bioText);
        detailsBox.setAlignment(Pos.CENTER);
        layout.getChildren().add(detailsBox);

       
        Button closeButton = new Button("Închide");
        closeButton.setOnAction(e -> detailsStage.close());
        layout.getChildren().add(closeButton);

      
        Scene scene = new Scene(layout, 300, 300);
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private void deleteUser(User user, ListView<User> userListView, Stage primaryStage) {
        if (user == null) {
            showErrorMessage("Te rog selectează un utilizator.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmare Ștergere");
        confirmationAlert.setHeaderText("Sigur doriți să ștergeți utilizatorul " + user.getUsername() + "?");
        confirmationAlert.setContentText("Această acțiune nu poate fi anulată!");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection connection = DBC.getConnection()) {
                    String deleteQuery = "DELETE FROM users WHERE id = ?";
                    PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                    deleteStatement.setInt(1, user.getId());
                    int rowsAffected = deleteStatement.executeUpdate();

                    if (rowsAffected > 0) {
                 
                        userListView.getItems().remove(user);
                        showSuccessMessage("Utilizatorul a fost șters cu succes!");
                    } else {
                        showErrorMessage("Eroare la ștergerea utilizatorului!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showErrorMessage("Eroare la conectarea la baza de date!");
                }
            }
        });
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Eroare");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succes");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}

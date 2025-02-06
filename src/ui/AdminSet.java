package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Admin;
import bd.DBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AdminSet {

    private Admin user;

    public AdminSet(Admin admin) {
        this.user = admin;
    }

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Setări Administrator");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Back.setBackground(grid);
        
        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

      
        Label usernameLabel = new Label("Nume Administrator:");
        usernameLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(usernameLabel, 0, 0);

        TextField usernameInput = new TextField(user.getUsername());
        GridPane.setConstraints(usernameInput, 1, 0);

        Label usernameErrorLabel = new Label();
        usernameErrorLabel.setTextFill(Color.RED);
        GridPane.setConstraints(usernameErrorLabel, 1, 1);

        Button saveUsernameButton = new Button("Schimbă Numele");
        GridPane.setConstraints(saveUsernameButton, 2, 0);
        saveUsernameButton.setOnAction(e -> {
            String newUsername = usernameInput.getText().trim();
            if (newUsername.isEmpty()) {
                usernameErrorLabel.setText("Numele nu poate fi gol!");
            } else {
                try (Connection connection = DBC.getConnection()) {
                    String updateQuery = "UPDATE admins SET username = ? WHERE id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setString(1, newUsername);
                    preparedStatement.setInt(2, user.getId());

                    int rowsUpdated = preparedStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        usernameErrorLabel.setTextFill(Color.GREEN);
                        usernameErrorLabel.setText("Numele a fost schimbat cu succes!");
                        user.setUsername(newUsername);
                    } else {
                        usernameErrorLabel.setTextFill(Color.RED);
                        usernameErrorLabel.setText("Eroare la actualizarea numelui.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    usernameErrorLabel.setTextFill(Color.RED);
                    usernameErrorLabel.setText("Eroare la conectarea la baza de date.");
                }
            }
        });

     
        Label passwordMethodLabel = new Label("Metodă de verificare:");
        passwordMethodLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(passwordMethodLabel, 0, 2);

        ChoiceBox<String> methodChoice = new ChoiceBox<>();
        methodChoice.getItems().addAll("Parola Veche", "Verificare prin Email");
        methodChoice.setValue("Parola Veche");
        GridPane.setConstraints(methodChoice, 1, 2);

        Label currentPasswordLabel = new Label("Parola Curentă:");
        currentPasswordLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(currentPasswordLabel, 0, 3);

        PasswordField currentPasswordInput = new PasswordField();
        GridPane.setConstraints(currentPasswordInput, 1, 3);

        TextField currentPasswordVisibleInput = new TextField();
        currentPasswordVisibleInput.setManaged(false);
        currentPasswordVisibleInput.setVisible(false);
        GridPane.setConstraints(currentPasswordVisibleInput, 1, 3);

        Button toggleCurrentPasswordButton = createToggleButton();
        GridPane.setConstraints(toggleCurrentPasswordButton, 2, 3);
        toggleCurrentPasswordButton.setOnAction(e -> togglePasswordVisibility(currentPasswordInput, currentPasswordVisibleInput, toggleCurrentPasswordButton));

        Label newPasswordLabel = new Label("Noua Parolă:");
        newPasswordLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(newPasswordLabel, 0, 5);

        PasswordField newPasswordInput = new PasswordField();
        GridPane.setConstraints(newPasswordInput, 1, 5);

        TextField newPasswordVisibleInput = new TextField();
        newPasswordVisibleInput.setManaged(false);
        newPasswordVisibleInput.setVisible(false);
        GridPane.setConstraints(newPasswordVisibleInput, 1, 5);

        Button toggleNewPasswordButton = createToggleButton();
        GridPane.setConstraints(toggleNewPasswordButton, 2, 5);
        toggleNewPasswordButton.setOnAction(e -> togglePasswordVisibility(newPasswordInput, newPasswordVisibleInput, toggleNewPasswordButton));

        Label confirmPasswordLabel = new Label("Confirmare Parolă:");
        confirmPasswordLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(confirmPasswordLabel, 0, 7);

        PasswordField confirmPasswordInput = new PasswordField();
        GridPane.setConstraints(confirmPasswordInput, 1, 7);

        TextField confirmPasswordVisibleInput = new TextField();
        confirmPasswordVisibleInput.setManaged(false);
        confirmPasswordVisibleInput.setVisible(false);
        GridPane.setConstraints(confirmPasswordVisibleInput, 1, 7);

        Button toggleConfirmPasswordButton = createToggleButton();
        GridPane.setConstraints(toggleConfirmPasswordButton, 2, 7);
        toggleConfirmPasswordButton.setOnAction(e -> togglePasswordVisibility(confirmPasswordInput, confirmPasswordVisibleInput, toggleConfirmPasswordButton));

        Button backButton = new Button("Înapoi");
        GridPane.setConstraints(backButton, 1, 9);
        backButton.setOnAction(e -> new ProfilA(user).start(primaryStage));

        grid.getChildren().addAll(
                usernameLabel, usernameInput, usernameErrorLabel, saveUsernameButton,
                passwordMethodLabel, methodChoice,
                currentPasswordLabel, currentPasswordInput, currentPasswordVisibleInput, toggleCurrentPasswordButton,
                newPasswordLabel, newPasswordInput, newPasswordVisibleInput, toggleNewPasswordButton,
                confirmPasswordLabel, confirmPasswordInput, confirmPasswordVisibleInput, toggleConfirmPasswordButton,
                backButton
        );

        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createToggleButton() {
        Button button = new Button();
        Circle circle = new Circle(10);
        circle.setFill(Color.RED);
        button.setGraphic(circle);
        button.setStyle("-fx-background-color: transparent;");

        return button;
    }

    private void togglePasswordVisibility(PasswordField passwordField, TextField visibleField, Button toggleButton) {
        Circle circle = (Circle) toggleButton.getGraphic();
        if (passwordField.isVisible()) {
            visibleField.setText(passwordField.getText());
            visibleField.setVisible(true);
            visibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            circle.setFill(Color.GREEN);
        } else {
            passwordField.setText(visibleField.getText());
            visibleField.setVisible(false);
            visibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            circle.setFill(Color.RED);
        }
    }
}

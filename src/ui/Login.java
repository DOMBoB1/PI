package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Admin;
import model.User;
import bd.DBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Logare");

       
        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        Back.setBackground(mainLayout);

       
        ImageView scoobyImageView = new ImageView(new Image(getClass().getResourceAsStream("/r/log2.png")));
        scoobyImageView.setFitWidth(100);
        scoobyImageView.setPreserveRatio(true);

        Label specialMessage = new Label("Conectează-te singur! Eu nu pot vedea!!");
        specialMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: black; -fx-font-weight: bold;");

        VBox header = new VBox(10, scoobyImageView, specialMessage);
        header.setAlignment(Pos.CENTER);

       
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email sau Nume:");
        emailLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(emailLabel, 0, 0);

        TextField emailInput = new TextField();
        emailInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(emailInput, 1, 0);

        Label passwordLabel = new Label("Parolă:");
        passwordLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(passwordLabel, 0, 1);

        PasswordField passwordInput = new PasswordField();
        passwordInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(passwordInput, 1, 1);

        TextField passwordVisibleInput = new TextField();
        passwordVisibleInput.setManaged(false);
        passwordVisibleInput.setVisible(false);
        passwordVisibleInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(passwordVisibleInput, 1, 1);

        Button togglePasswordButton = createToggleButton(passwordInput, passwordVisibleInput, scoobyImageView, specialMessage);
        GridPane.setConstraints(togglePasswordButton, 2, 1);

        Label roleLabel = new Label("Rol:");
        roleLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(roleLabel, 0, 2);

        ChoiceBox<String> roleInput = new ChoiceBox<>();
        roleInput.getItems().addAll("user", "admin");
        roleInput.setValue("user");
        roleInput.setStyle("-fx-font-size: 14px;");
        GridPane.setConstraints(roleInput, 1, 2);

        Label adminIdLabel = new Label("ID Admin:");
        adminIdLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        TextField adminIdInput = new TextField();
        adminIdInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(adminIdLabel, 0, 3);
        GridPane.setConstraints(adminIdInput, 1, 3);

        adminIdLabel.setManaged(false);
        adminIdLabel.setVisible(false);
        adminIdInput.setManaged(false);
        adminIdInput.setVisible(false);

        roleInput.setOnAction(e -> {
            boolean isAdmin = roleInput.getValue().equals("admin");
            adminIdLabel.setManaged(isAdmin);
            adminIdLabel.setVisible(isAdmin);
            adminIdInput.setManaged(isAdmin);
            adminIdInput.setVisible(isAdmin);
        });

        Button loginButton = new Button("Logare");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        GridPane.setConstraints(loginButton, 1, 4);

        Button backButton = new Button("Înapoi");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        GridPane.setConstraints(backButton, 0, 4);
        backButton.setOnAction(e -> new Main().start(primaryStage));

        Label messageLabel = new Label();
        GridPane.setConstraints(messageLabel, 1, 5);

        grid.getChildren().addAll(emailLabel, emailInput, passwordLabel, passwordInput, passwordVisibleInput,
                togglePasswordButton, roleLabel, roleInput, adminIdLabel, adminIdInput, loginButton, backButton, messageLabel);

        loginButton.setOnAction(e -> {
            String emailOrName = emailInput.getText().trim();
            String password = passwordInput.isVisible() ? passwordInput.getText().trim() : passwordVisibleInput.getText().trim();
            String adminId = adminIdInput.getText().trim();

            if (emailOrName.isEmpty() || password.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Email/nume și parola sunt obligatorii!");
                return;
            }

            if ("admin".equals(roleInput.getValue()) && adminId.isEmpty()) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("ID Admin este obligatoriu pentru admin!");
                return;
            }

            try (Connection connection = DBC.getConnection()) {
                String query;
                PreparedStatement statement;
                if ("admin".equals(roleInput.getValue())) {
                    query = "SELECT * FROM admins WHERE admin_id = ? AND password = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, adminId);
                    statement.setString(2, password);
                } else {
                    query = "SELECT * FROM users WHERE (email = ? OR username = ?) AND password = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, emailOrName);
                    statement.setString(2, emailOrName);
                    statement.setString(3, password);
                }

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    if ("admin".equals(roleInput.getValue())) {
                        Admin admin = new Admin(
                                resultSet.getInt("id"),
                                resultSet.getString("username"),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                resultSet.getString("bio"),
                                resultSet.getString("profile_picture"),
                                resultSet.getString("admin_id")
                        );
                        new AdminM(admin).start(primaryStage);
                    } else {
                        User user = new User(
                                resultSet.getInt("id"),
                                resultSet.getString("username"),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                resultSet.getString("bio"),
                                resultSet.getString("profile_picture")
                        );
                        new UserM(user).start(primaryStage);
                    }
                } else {
                    messageLabel.setTextFill(Color.RED);
                    messageLabel.setText("Eroare la autentificare!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Eroare la conectarea bazei de date!");
            }
        });

        mainLayout.getChildren().addAll(header, grid);

        Scene scene = new Scene(mainLayout, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createToggleButton(PasswordField passwordField, TextField visibleField, ImageView scoobyImageView, Label specialMessage) {
        Button button = new Button();
        Circle circle = new Circle(10);
        circle.setFill(Color.RED);
        button.setGraphic(circle);
        button.setStyle("-fx-background-color: transparent;");

        button.setOnAction(e -> togglePasswordVisibility(passwordField, visibleField, button, scoobyImageView, specialMessage));

        return button;
    }

    private void togglePasswordVisibility(PasswordField passwordField, TextField visibleField, Button toggleButton, ImageView scoobyImageView, Label specialMessage) {
        Circle circle = (Circle) toggleButton.getGraphic();
        if (passwordField.isVisible()) {
            visibleField.setText(passwordField.getText());
            visibleField.setVisible(true);
            visibleField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            circle.setFill(Color.GREEN);
            scoobyImageView.setImage(new Image(getClass().getResourceAsStream("/r/log1.png")));
            specialMessage.setText("Hai să ne conectăm!");
        } else {
            passwordField.setText(visibleField.getText());
            visibleField.setVisible(false);
            visibleField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            circle.setFill(Color.RED);
            scoobyImageView.setImage(new Image(getClass().getResourceAsStream("/r/log2.png")));
            specialMessage.setText("Conectează-te singur! Eu nu pot vedea!!");
        }
    }
}

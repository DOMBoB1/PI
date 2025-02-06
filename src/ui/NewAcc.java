package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import bd.DBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class NewAcc {
private PasswordField confirmPasswordInput;
    public void start(Stage primaryStage) {
    	

            confirmPasswordInput = new PasswordField();
            confirmPasswordInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
            GridPane.setConstraints(confirmPasswordInput, 1, 3);

        primaryStage.setTitle("Creare Cont");

         GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

     
        Back.setBackground(grid); 

        Label nameLabel = new Label("Nume:");
        nameLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(nameLabel, 0, 0);
        TextField nameInput = new TextField();
        nameInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(nameInput, 1, 0);

        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(emailLabel, 0, 1);
        TextField emailInput = new TextField();
        emailInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(emailInput, 1, 1);

        Label passwordLabel = new Label("Parolă:");
        passwordLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(passwordLabel, 0, 2);
        PasswordField passwordInput = new PasswordField();
        passwordInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(passwordInput, 1, 2);

        // Add visible field for password toggle functionality
        TextField passwordVisibleInput = new TextField();
        passwordVisibleInput.setManaged(false);
        passwordVisibleInput.setVisible(false);
        passwordVisibleInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(passwordVisibleInput, 1, 2);

        Button togglePasswordButton = createToggleButton(passwordInput, passwordVisibleInput);
        GridPane.setConstraints(togglePasswordButton, 2, 2);

        Label confirmPasswordLabel = new Label("Confirmare Parolă:");
        confirmPasswordLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(confirmPasswordLabel, 0, 3);
        PasswordField confirmPasswordInput = new PasswordField();
        confirmPasswordInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(confirmPasswordInput, 1, 3);

        TextField confirmPasswordVisibleInput = new TextField();
        confirmPasswordVisibleInput.setManaged(false);
        confirmPasswordVisibleInput.setVisible(false);
        confirmPasswordVisibleInput.setStyle("-fx-font-size: 14px; -fx-background-color: rgba(255, 255, 255, 0.7);");
        GridPane.setConstraints(confirmPasswordVisibleInput, 1, 3);

        Button toggleConfirmPasswordButton = createToggleButton(confirmPasswordInput, confirmPasswordVisibleInput);
        GridPane.setConstraints(toggleConfirmPasswordButton, 2, 3);

        Label roleLabel = new Label("Rol:");
        roleLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(roleLabel, 0, 4);
        ChoiceBox<String> roleInput = new ChoiceBox<>();
        roleInput.getItems().addAll("user", "admin");
        roleInput.setValue("user");
        roleInput.setStyle("-fx-font-size: 14px;");
        GridPane.setConstraints(roleInput, 1, 4);

        Label adminIdLabel = new Label("ID Admin:");
        adminIdLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
        GridPane.setConstraints(adminIdLabel, 0, 5);
        TextField adminIdInput = new TextField();
        adminIdInput.setStyle("-fx-font-size: 14px;");
        GridPane.setConstraints(adminIdInput, 1, 5);

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

        Button createButton = new Button("Creează");
        createButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        GridPane.setConstraints(createButton, 1, 6);

        Button backButton = new Button("Înapoi");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        GridPane.setConstraints(backButton, 1, 7);
        backButton.setOnAction(e -> new Main().start(primaryStage));

        Label messageLabel = new Label();
        GridPane.setConstraints(messageLabel, 1, 8);
        grid.getChildren().addAll(nameLabel, nameInput, emailLabel, emailInput, passwordLabel, passwordInput, passwordVisibleInput,
                togglePasswordButton, confirmPasswordLabel, confirmPasswordInput, confirmPasswordVisibleInput,
                toggleConfirmPasswordButton, roleLabel, roleInput, adminIdLabel, adminIdInput, createButton, backButton, messageLabel);
      
        createButton.setOnAction(e -> {
            String username = nameInput.getText().trim();
            String email = emailInput.getText().trim();
            String password = passwordInput.getText().trim();
            String confirmPassword = confirmPasswordInput.getText().trim();
            String adminId = adminIdInput.getText().trim();

            StringBuilder errorMessages = new StringBuilder();

            if (username.isEmpty()) {
                errorMessages.append("Numele nu poate fi gol!\n");
            }
            if (email.isEmpty() || !validateEmail(email)) {
                errorMessages.append("Email-ul nu este valid!\n");
            }
            if (password.isEmpty() || !validatePassword(password)) {
                errorMessages.append("Parola trebuie să conțină minim 6 caractere, 2 cifre și 1 simbol special!\n");
            }
            if (!password.equals(confirmPassword)) {
                errorMessages.append("Parolele nu se potrivesc!\n");
            }

          
            if (roleInput.getValue().equals("admin")) {
                if (adminId.isEmpty()) {
                    errorMessages.append("ID-ul Admin este necesar!\n");
                } else if (!validateAdminId(adminId)) {
                    errorMessages.append("ID-ul Admin nu respectă formatul standard!\n");
                }
            }

            if (errorMessages.length() > 0) {
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText(errorMessages.toString());
                return;
            }

            try (Connection connection = DBC.getConnection()) {
                String query;

                if (roleInput.getValue().equals("admin")) {
              
                    query = "INSERT INTO admins (username, email, password, bio, profile_picture, permissions, admin_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                    
                        statement.setString(1, username);
                        statement.setString(2, email);
                        statement.setString(3, password);
                        statement.setString(4, "");
                        statement.setString(5, "");  
                        statement.setString(6, ""); 
                        statement.setString(7, adminId);
                        statement.executeUpdate();
                    }
                } else {
                 
                    query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.setString(1, username);
                        statement.setString(2, email);
                        statement.setString(3, password);
                        statement.executeUpdate();
                    }
                }

       
                new Login().start(primaryStage);
            } catch (SQLException ex) {
                ex.printStackTrace();
                messageLabel.setTextFill(Color.RED);
                messageLabel.setText("Eroare la crearea contului!");
            }
        });



        grid.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                createButton.fire();
            }
        });

        Scene scene = new Scene(grid, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createToggleButton(PasswordField passwordField, TextField visibleField) {
        Button button = new Button();
        Circle circle = new Circle(10);
        circle.setFill(Color.RED);
        button.setGraphic(circle);
        button.setStyle("-fx-background-color: transparent;");

        button.setOnAction(e -> togglePasswordVisibility(passwordField, visibleField, button));

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

    private boolean validateEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([a-zA-Z0-9-]+\\.)+(com|org|net|edu|ro|gov|info|io|biz)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        if (password.length() < 6) return false;
        int digitCount = 0;
        int specialCharCount = 0;
        String specialCharacters = "!@#$%^&*()_+=|<>?{}[]~-,.;:";

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) digitCount++;
            if (specialCharacters.indexOf(c) != -1) specialCharCount++;
        }

      
        String confirmPassword = confirmPasswordInput.getText(); 
        System.out.println("Confirm Password: " + confirmPassword);

        return digitCount >= 2 && specialCharCount >= 1;
    }
   

    
    private boolean validateAdminId(String adminId) {
    	String adminIdRegex = "^I[A-Za-z]{2,4}\\d{2,4}!$";

        return Pattern.compile(adminIdRegex).matcher(adminId).matches();
    }
}

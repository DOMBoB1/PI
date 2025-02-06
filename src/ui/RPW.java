package ui;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.User;
import javafx.geometry.Insets;

import java.util.regex.Pattern;

public class RPW {

    private User utilizatorCurent;

    public RPW(User utilizatorCurent) {
        this.utilizatorCurent = utilizatorCurent;
    }

    public void display(Stage primaryStage) {
        Stage resetStage = new Stage();
        resetStage.setTitle("Resetare Parolă");

        Image icon = new Image("file:///D:/proiecte%20java/Proiect_Pi_P3/src/ecrane/Iconita_magazin.JPG");  
        resetStage.getIcons().add(icon);

        Label lblParolaVeche = new Label("Parola veche:");
        PasswordField txtParolaVeche = new PasswordField();
        TextField txtParolaVecheVisible = createVisibleField(txtParolaVeche);
        Button btnToggleOldPassword = createToggleButton(txtParolaVeche, txtParolaVecheVisible);

        Label lblParolaNoua = new Label("Parola nouă:");
        PasswordField txtParolaNoua = new PasswordField();
        TextField txtParolaNouaVisible = createVisibleField(txtParolaNoua);
        Button btnToggleNewPassword = createToggleButton(txtParolaNoua, txtParolaNouaVisible);

        Label lblConfirmareParola = new Label("Confirmare parolă nouă:");
        PasswordField txtConfirmareParola = new PasswordField();
        TextField txtConfirmareParolaVisible = createVisibleField(txtConfirmareParola);
        Button btnToggleConfirmPassword = createToggleButton(txtConfirmareParola, txtConfirmareParolaVisible);

        Button btnResetare = new Button("Resetare parolă");
        btnResetare.setOnAction(e -> {
            String parolaVeche = txtParolaVeche.getText().trim();
            String parolaNoua = txtParolaNoua.getText().trim();
            String confirmareParola = txtConfirmareParola.getText().trim();

            if (parolaVeche.isEmpty() || parolaNoua.isEmpty() || confirmareParola.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Toate câmpurile sunt obligatorii.");
                return;
            }

            if (!utilizatorCurent.getPassword().equals(parolaVeche)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Parola veche este incorectă.");
                return;
            }

            if (!validatePassword(parolaNoua)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Parola nouă trebuie să conțină minim 6 litere, 2 cifre și 1 caracter special.");
                return;
            }

            if (!parolaNoua.equals(confirmareParola)) {
                showAlert(Alert.AlertType.ERROR, "Eroare", "Parola nouă și confirmarea nu se potrivesc.");
                return;
            }

            utilizatorCurent.setPassword(parolaNoua);
            showAlert(Alert.AlertType.INFORMATION, "Succes", "Parola a fost resetată cu succes!");
            resetStage.close();
        });

        Button btnBack = new Button("Înapoi");
        btnBack.setOnAction(e -> resetStage.close());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(
                lblParolaVeche, createPasswordBox(txtParolaVeche, txtParolaVecheVisible, btnToggleOldPassword),
                lblParolaNoua, createPasswordBox(txtParolaNoua, txtParolaNouaVisible, btnToggleNewPassword),
                lblConfirmareParola, createPasswordBox(txtConfirmareParola, txtConfirmareParolaVisible, btnToggleConfirmPassword),
                btnResetare, btnBack
        );

        Scene scene = new Scene(layout, 400, 500);
        resetStage.setScene(scene);
        resetStage.show();
    }

    private TextField createVisibleField(PasswordField passwordField) {
        TextField visibleField = new TextField();
        visibleField.managedProperty().bind(passwordField.visibleProperty().not());
        visibleField.visibleProperty().bind(passwordField.visibleProperty().not());
        visibleField.textProperty().bindBidirectional(passwordField.textProperty());
        return visibleField;
    }

    private Button createToggleButton(PasswordField passwordField, TextField visibleField) {
        Button btnTogglePassword = new Button("👁");
        btnTogglePassword.setOnAction(e -> {
            passwordField.setVisible(!passwordField.isVisible());
            visibleField.setVisible(!visibleField.isVisible());
        });
        return btnTogglePassword;
    }

    private HBox createPasswordBox(PasswordField passwordField, TextField visibleField, Button btnTogglePassword) {
        HBox passwordBox = new HBox(5);
        passwordBox.getChildren().addAll(passwordField, visibleField, btnTogglePassword);
        return passwordBox;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validatePassword(String password) {
        String passwordRegex = "^(?=.*[a-zA-Z]{6,})(?=.*\\d{2,})(?=.*[@#$%^&+=]).{9,}$";
        return Pattern.compile(passwordRegex).matcher(password).matches();
    }
}

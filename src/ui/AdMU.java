package ui;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.User;
import bd.DBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdMU {
	
	private final model.Admin admin;

	public AdMU(model.Admin admin) {
	    this.admin = admin;
	}


    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestionare Utilizatori");

     
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Back.setBackground(layout);
        Image appIcon = new Image(getClass().getResourceAsStream("/r/scooby.jpg"));
        primaryStage.getIcons().add(appIcon);

        VBox loadingBox = new VBox(15);
        loadingBox.setAlignment(Pos.CENTER);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        Text loadingText = new Text("Se încarcă utilizatorii...");
        loadingText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        loadingBox.getChildren().addAll(progressIndicator, loadingText);

       
        FlowPane usersContainer = new FlowPane();
        usersContainer.setPadding(new Insets(20));
        usersContainer.setHgap(20);
        usersContainer.setVgap(20);
        usersContainer.setVisible(false);

        ScrollPane scrollPane = new ScrollPane(usersContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent;");

        Button backButton = new Button("Înapoi");
        backButton.setOnAction(e -> new AdminM(admin).start(primaryStage));

        layout.getChildren().addAll(loadingBox, scrollPane, backButton);

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

     
        loadUsersInBackground(usersContainer, loadingBox);
    }

    private void loadUsersInBackground(FlowPane usersContainer, VBox loadingBox) {
        Task<List<User>> loadUsersTask = new Task<>() {
            @Override
            protected List<User> call() throws Exception {
                return getUsersFromDatabase();
            }
        };

        loadUsersTask.setOnSucceeded(e -> {
            List<User> users = loadUsersTask.getValue();
            for (User user : users) {
                VBox userCard = createUserCard(user, usersContainer);
                usersContainer.getChildren().add(userCard);
            }
            loadingBox.setVisible(false);
            usersContainer.setVisible(true);
        });

        loadUsersTask.setOnFailed(e -> loadingBox.getChildren().add(new Text("Eroare la încărcarea utilizatorilor.")));

        Thread thread = new Thread(loadUsersTask);
        thread.setDaemon(true);
        thread.start();
    }

    private List<User> getUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBC.getConnection(); Statement statement = connection.createStatement()) {
            String query = "SELECT id, username, email, profile_picture FROM users";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                users.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        "",
                        "",
                        resultSet.getString("profile_picture")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    private VBox createUserCard(User user, FlowPane usersContainer) {
        VBox userCard = new VBox(10);
        userCard.setAlignment(Pos.CENTER);
        userCard.setPadding(new Insets(10));
        userCard.setStyle("-fx-border-color: #ccc; -fx-border-width: 2; -fx-border-radius: 5;");

        ImageView profilePicture = new ImageView();
        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            profilePicture.setImage(new Image(user.getProfilePicture()));
        } else {
            profilePicture.setImage(new Image("https://via.placeholder.com/100"));
        }
        profilePicture.setFitHeight(100);
        profilePicture.setFitWidth(100);
        profilePicture.setPreserveRatio(true);

        Text usernameText = new Text(user.getUsername());
        Text emailText = new Text(user.getEmail());

        Button viewLibraryButton = new Button("Vezi Librăria");
        viewLibraryButton.setOnAction(e -> new AdminUserLibrary(user).start(new Stage()));

        Button deleteUserButton = new Button("Șterge Contul");
        deleteUserButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        deleteUserButton.setOnAction(e -> deleteUser(user, userCard, usersContainer));

        userCard.getChildren().addAll(profilePicture, usernameText, emailText, viewLibraryButton, deleteUserButton);
        return userCard;
    }

    private void deleteUser(User user, VBox userCard, FlowPane usersContainer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmare Ștergere");
        alert.setHeaderText("Sigur doriți să ștergeți contul lui " + user.getUsername() + "?");
        alert.setContentText("Această acțiune este ireversibilă!");

        alert.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                try (Connection connection = DBC.getConnection()) {
                    String query = "DELETE FROM users WHERE id = ?";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setInt(1, user.getId());
                    statement.executeUpdate();

                    usersContainer.getChildren().remove(userCard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

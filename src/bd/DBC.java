package bd;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dumitras Olga-Maria
 * @date 04/06/2024
 *
 * Clasa DBC gestionează conexiunea la baza de date SQLite și oferă metode pentru inițializarea,
 * verificarea și manipularea tabelelor bazei de date utilizate în aplicație.
 */
public class DBC {

    private static final String URL = "jdbc:sqlite:game_library.db";

    /**
     * Oferă o conexiune activă la baza de date.
     * 
     * @throws SQLException În caz de eroare la conectare.
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        if (connection != null) {
            System.out.println("Database connected successfully.");
        }
        return connection;
    }

    /**
     * Inițializează toate tabelele necesare pentru funcționarea aplicației.
     * Dacă tabelele nu există deja, acestea sunt create.
     */
    public static void initializeDatabase() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {

       
            String createUsersTableQuery = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    bio TEXT DEFAULT '',
                    profile_picture TEXT DEFAULT ''
                );
            """;

       
            String createAdminsTableQuery = """
                CREATE TABLE IF NOT EXISTS admins (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    bio TEXT DEFAULT '',
                    profile_picture TEXT DEFAULT '',
                    permissions TEXT DEFAULT '',
                    admin_id TEXT NOT NULL
                );
            """;

       
            String createGamesTableQuery = """
                CREATE TABLE IF NOT EXISTS games (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    genre TEXT NOT NULL,
                    release_year INTEGER NOT NULL,
                    developer TEXT NOT NULL,
                    icon_path TEXT NOT NULL
                );
            """;

            String createRatingsTableQuery = """
                CREATE TABLE IF NOT EXISTS ratings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5),
                    UNIQUE(game_id, user_id),
                    FOREIGN KEY (game_id) REFERENCES games(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                );
            """;

            
            String createGameImagesTableQuery = """
                CREATE TABLE IF NOT EXISTS game_images (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER NOT NULL,
                    image_path TEXT NOT NULL,
                    FOREIGN KEY (game_id) REFERENCES games(id)
                );
            """;

       
            statement.execute(createUsersTableQuery);
            statement.execute(createAdminsTableQuery);
            statement.execute(createGamesTableQuery);
            statement.execute(createRatingsTableQuery);
            statement.execute(createGameImagesTableQuery);

            System.out.println("Toate tabelele au fost inițializate sau verificate cu succes.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Eroare la inițializarea tabelelor bazei de date.");
        }
    }

    /**
     * Afișează toate tabelele existente din baza de date.
     */
    public static void verifyTables() {
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table';");

            System.out.println("Tabelele existente în baza de date:");
            while (resultSet.next()) {
                System.out.println(resultSet.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Eroare la verificarea tabelelor bazei de date.");
        }
    }

    /**
     * Șterge toate tabelele din baza de date.
     * Util pentru teste și depanare.
     */
    public static void dropAllTables() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS users;");
            statement.execute("DROP TABLE IF EXISTS admins;");
            statement.execute("DROP TABLE IF EXISTS games;");
            statement.execute("DROP TABLE IF EXISTS ratings;");
            statement.execute("DROP TABLE IF EXISTS game_images;");
            System.out.println("Toate tabelele au fost șterse cu succes.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Eroare la ștergerea tabelelor.");
        }
    }

    /**
     * Returnează lista căilor imaginilor asociate unui joc.
     * 
     * @param gameId ID-ul jocului pentru care sunt obținute imaginile.
     * @return O listă de {@link String} reprezentând căile imaginilor.
     */
    public static List<String> getGameImages(int gameId) {
        List<String> imagePaths = new ArrayList<>();
        String query = "SELECT image_path FROM game_images WHERE game_id = ?";
        try (Connection conn = DBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                imagePaths.add(rs.getString("image_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return imagePaths;
    }
}


package bd;

import model.Game;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Clasa `GameDBC` gestionează funcționalitățile legate de jocuri în baza de date.
 * Aceasta include crearea tabelelor necesare, adăugarea, actualizarea, ștergerea jocurilor, precum și gestionarea 
 * imaginilor asociate jocurilor și bibliotecii de jocuri a utilizatorilor.
 * <p>
 * Toate interacțiunile cu baza de date sunt realizate prin conexiunea oferită de clasa {@link DBC}.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class GameDBC {

    /**
     * Obține conexiunea la baza de date.
     * 
     * @return Obiectul {@link Connection} pentru conexiunea la baza de date.
     * @throws SQLException Dacă apare o problemă la conectare.
     */
    public static Connection getConnection() throws SQLException {
        return DBC.getConnection();
    }

    /**
     * Creează tabelele necesare pentru gestionarea jocurilor și imaginilor asociate.
     */
    public static void initializeGameTables() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            
            String createGamesTableQuery = """
                CREATE TABLE IF NOT EXISTS games (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    description TEXT NOT NULL,
                    rating INTEGER DEFAULT 0,
                    genre TEXT NOT NULL,
                    release_year INTEGER NOT NULL,
                    developer TEXT NOT NULL,
                    icon_path TEXT NOT NULL
                );
            """;

            String createGameImagesTableQuery = """
                CREATE TABLE IF NOT EXISTS game_images (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER NOT NULL,
                    image_path TEXT NOT NULL,
                    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
                );
            """;

            String createUserLibraryTableQuery = """
                CREATE TABLE IF NOT EXISTS user_library (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    game_id INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
                );
            """;

            statement.execute(createGamesTableQuery);
            statement.execute(createGameImagesTableQuery);
            statement.execute(createUserLibraryTableQuery);

            System.out.println("Tabelele 'games', 'game_images' și 'user_library' au fost inițializate.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifică dacă numele unui joc este unic în tabelul `games`.
     * 
     * @param name          Numele jocului de verificat.
     * @param currentGameId ID-ul jocului curent (pentru excluderea lui din verificare).
     * @return {@code true} dacă numele este unic, altfel {@code false}.
     */
    public static boolean isGameNameUnique(String name, int currentGameId) {
        String query = "SELECT COUNT(*) FROM games WHERE name = ? AND id != ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, currentGameId);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adaugă un joc în biblioteca unui utilizator.
     * 
     * @param userId ID-ul utilizatorului.
     * @param gameId ID-ul jocului.
     * @return {@code true} dacă jocul a fost adăugat cu succes, altfel {@code false}.
     */
    public static boolean addGameToUserLibrary(int userId, int gameId) {
        String checkQuery = "SELECT COUNT(*) FROM user_library WHERE user_id = ? AND game_id = ?";
        String insertQuery = "INSERT INTO user_library (user_id, game_id) VALUES (?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            checkStatement.setInt(1, userId);
            checkStatement.setInt(2, gameId);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                return false;
            }

            insertStatement.setInt(1, userId);
            insertStatement.setInt(2, gameId);
            return insertStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adaugă o imagine asociată unui joc în baza de date.
     * 
     * @param gameId ID-ul jocului.
     * @param image  Fișierul imaginii de adăugat.
     * @return {@code true} dacă imaginea a fost adăugată cu succes, altfel {@code false}.
     */
    public static boolean addGameImage(int gameId, File image) {
        String query = "INSERT INTO game_images (game_id, image_path) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameId);
            statement.setString(2, image.toURI().toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimină un joc din biblioteca unui utilizator.
     * 
     * @param userId ID-ul utilizatorului.
     * @param gameId ID-ul jocului.
     * @return {@code true} dacă jocul a fost eliminat cu succes, altfel {@code false}.
     */
    public static boolean removeGameFromUserLibrary(int userId, int gameId) {
        String query = "DELETE FROM user_library WHERE user_id = ? AND game_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, gameId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adaugă un joc nou în baza de date.
     * 
     * @param game Obiectul {@link Game} care reprezintă jocul de adăugat.
     * @return ID-ul jocului adăugat sau {@code -1} în caz de eșec.
     */
    public static int addGame(Game game) {
        String query = """
                INSERT INTO games (name, description, genre, release_year, developer, icon_path)
                VALUES (?, ?, ?, ?, ?, ?);
                """;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, game.getName());
            statement.setString(2, game.getDescription());
            statement.setString(3, game.getGenre());
            statement.setInt(4, game.getReleaseYear());
            statement.setString(5, game.getDeveloper());
            statement.setString(6, game.getIconPath());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Obține lista de jocuri din biblioteca unui utilizator.
     * 
     * @param userId ID-ul utilizatorului.
     * @return Lista de jocuri din biblioteca utilizatorului.
     */
    public static List<Game> getUserLibrary(int userId) {
        List<Game> games = new ArrayList<>();
        String query = """
                SELECT g.id, g.name, g.description, g.rating, g.genre, g.release_year, g.developer, g.icon_path
                FROM games g
                JOIN user_library ul ON g.id = ul.game_id
                WHERE ul.user_id = ?;
                """;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Game game = new Game(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("rating"),
                        resultSet.getString("genre"),
                        resultSet.getInt("release_year"),
                        resultSet.getString("developer"),
                        resultSet.getString("icon_path")
                );
                games.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    /**
     * Actualizează informațiile unui joc în baza de date.
     * 
     * @param game Obiectul {@link Game} cu informațiile actualizate.
     * @return {@code true} dacă actualizarea a avut succes, altfel {@code false}.
     */
    public static boolean updateGame(Game game) {
        String query = """
                UPDATE games SET name = ?, description = ?, genre = ?, 
                release_year = ?, developer = ?, icon_path = ? WHERE id = ?;
                """;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, game.getName());
            statement.setString(2, game.getDescription());
            statement.setString(3, game.getGenre());
            statement.setInt(4, game.getReleaseYear());
            statement.setString(5, game.getDeveloper());
            statement.setString(6, game.getIconPath());
            statement.setInt(7, game.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obține lista de imagini asociate unui joc.
     * 
     * @param gameId ID-ul jocului.
     * @return Lista căilor imaginilor asociate.
     */
    public static List<String> getGameImages(int gameId) {
        List<String> images = new ArrayList<>();
        String query = "SELECT image_path FROM game_images WHERE game_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                images.add(resultSet.getString("image_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }

    /**
     * Obține lista tuturor jocurilor din baza de date.
     * 
     * @return Lista de jocuri existente.
     */
    public static List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        String query = "SELECT * FROM games";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Game game = new Game(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        0, resultSet.getString("genre"),
                        resultSet.getInt("release_year"),
                        resultSet.getString("developer"),
                        resultSet.getString("icon_path")
                );
                games.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return games;
    }

    /**
     * Adaugă imagini asociate unui joc în baza de date.
     * 
     * @param gameId ID-ul jocului.
     * @param images Lista de fișiere imagine de adăugat.
     * @return {@code true} dacă toate imaginile au fost adăugate cu succes, altfel {@code false}.
     */
    public static boolean addGameImages(int gameId, List<File> images) {
        String query = "INSERT INTO game_images (game_id, image_path) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            for (File image : images) {
                statement.setInt(1, gameId);
                statement.setString(2, image.toURI().toString());
                statement.addBatch();
            }
            statement.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Șterge un joc din baza de date.
     * 
     * @param gameId ID-ul jocului de șters.
     * @return {@code true} dacă jocul a fost șters cu succes, altfel {@code false}.
     */
    public static boolean deleteGame(int gameId) {
        String query = "DELETE FROM games WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Șterge o imagine asociată unui joc din baza de date.
     * 
     * @param gameId    ID-ul jocului.
     * @param imagePath Calea imaginii de șters.
     * @return {@code true} dacă imaginea a fost ștersă cu succes, altfel {@code false}.
     */
    public static boolean deleteImage(int gameId, String imagePath) {
        String query = "DELETE FROM game_images WHERE game_id = ? AND image_path = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, gameId);
            statement.setString(2, imagePath);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

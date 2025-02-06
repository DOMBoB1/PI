package bd;

import model.Sugest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameSDBC {

    /**
     * Oferă o conexiune activă la baza de date.
     * 
     * @throws SQLException În caz de eroare la conectare.
     */
    public static Connection getConnection() throws SQLException {
        return DBC.getConnection();
    }
    /**
     * Inițializează tabelul sugestiilor în baza de date.
     */
    public static void initializeSuggestionTable() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS suggestions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    game_name TEXT NOT NULL,
                    official_description TEXT NOT NULL,
                    developer TEXT NOT NULL,
                    release_year INTEGER NOT NULL,
                    user_reason TEXT NOT NULL,
                    status TEXT DEFAULT 'pending', -- 'pending', 'approved', 'rejected'
                    rejection_reason TEXT DEFAULT NULL,
                    likes INTEGER DEFAULT 0,
                    dislikes INTEGER DEFAULT 0,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
            """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
            System.out.println("Tabela 'suggestions' a fost creată sau verificată cu succes.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Eroare la inițializarea tabelului 'suggestions'.");
        }
    }

    /**
     * Inițializează tabelul voturilor pentru sugestii în baza de date.
     */
    public static void initializeSuggestionVotesTable() {
        String createTableQuery = """
                CREATE TABLE IF NOT EXISTS suggestion_votes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    suggestion_id INTEGER NOT NULL,
                    vote_type TEXT NOT NULL, -- 'like' sau 'dislike'
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    FOREIGN KEY (suggestion_id) REFERENCES suggestions(id) ON DELETE CASCADE,
                    UNIQUE (user_id, suggestion_id) -- Un utilizator poate vota o singură dată pe o sugestie
                );
            """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(createTableQuery);
            System.out.println("Tabela 'suggestion_votes' a fost creată sau verificată cu succes.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Eroare la inițializarea tabelului 'suggestion_votes'.");
        }
    }


/**
 * Adaugă o sugestie nouă în tabel.
 *
 * @param userId             ID-ul utilizatorului care face sugestia.
 * @param gameName           Numele jocului sugerat.
 * @param officialDescription Descrierea oficială a jocului.
 * @param developer          Dezvoltatorul jocului.
 * @param releaseYear        Anul lansării jocului.
 * @param userReason         Motivul utilizatorului pentru a adăuga jocul.
 * @return True dacă sugestia a fost adăugată cu succes, altfel False.
 */
public static boolean addSuggestion(int userId, String gameName, String officialDescription, String developer, int releaseYear, String userReason) {
    String query = """
            INSERT INTO suggestions (user_id, game_name, official_description, developer, release_year, user_reason)
            VALUES (?, ?, ?, ?, ?, ?);
        """;
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, userId);
        statement.setString(2, gameName);
        statement.setString(3, officialDescription);
        statement.setString(4, developer);
        statement.setInt(5, releaseYear);
        statement.setString(6, userReason);
        return statement.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

/**
 * Obține toate sugestiile făcute de alți utilizatori decât cel specificat și care sunt în starea 'pending'.
 *
 * @param userId ID-ul utilizatorului pentru care excludem sugestiile.
 * @return O listă de sugestii făcute de alți utilizatori.
 */
public static List<Sugest> getAllSuggestionsExceptUser(int userId) {
    List<Sugest> suggestions = new ArrayList<>();
    String query = "SELECT * FROM suggestions WHERE user_id != ? AND status = 'pending'";
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            suggestions.add(new Sugest(
                    resultSet.getInt("id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("game_name"),
                    resultSet.getString("official_description"),
                    resultSet.getString("developer"),
                    resultSet.getInt("release_year"),
                    resultSet.getString("user_reason"),
                    resultSet.getString("status"),
                    resultSet.getString("rejection_reason"),
                    resultSet.getInt("likes"),
                    resultSet.getInt("dislikes")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return suggestions;
}


/**
 * Verifică dacă utilizatorul are deja o sugestie cu status "pending".
 *
 * @param userId ID-ul utilizatorului.
 * @return True dacă există deja o sugestie în așteptare, altfel False.
 */
public static boolean hasPendingSuggestion(int userId) {
    String query = "SELECT 1 FROM suggestions WHERE user_id = ? AND status = 'pending'";
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next();
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

/**
 * Obține toate sugestiile în funcție de status.
 *
 * @param status Statusul sugestiilor ('pending', 'approved', 'rejected').
 * @return O listă de sugestii.
 */
public static List<Sugest> getSuggestionsByStatus(String status) {
    List<Sugest> suggestions = new ArrayList<>();
    String query = "SELECT * FROM suggestions WHERE status = ?";
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, status);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            suggestions.add(new Sugest(
                    resultSet.getInt("id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("game_name"),
                    resultSet.getString("official_description"),
                    resultSet.getString("developer"),
                    resultSet.getInt("release_year"),
                    resultSet.getString("user_reason"),
                    resultSet.getString("status"),
                    resultSet.getString("rejection_reason"),
                    resultSet.getInt("likes"),
                    resultSet.getInt("dislikes")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return suggestions;
}

/**
 * Obține toate sugestiile făcute de un utilizator specific.
 *
 * @param userId ID-ul utilizatorului.
 * @return O listă de sugestii făcute de utilizator.
 */
public static List<Sugest> getSuggestionsByUser(int userId) {
    List<Sugest> suggestions = new ArrayList<>();
    String query = "SELECT * FROM suggestions WHERE user_id = ?";
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, userId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            suggestions.add(new Sugest(
                    resultSet.getInt("id"),
                    resultSet.getInt("user_id"),
                    resultSet.getString("game_name"),
                    resultSet.getString("official_description"),
                    resultSet.getString("developer"),
                    resultSet.getInt("release_year"),
                    resultSet.getString("user_reason"),
                    resultSet.getString("status"),
                    resultSet.getString("rejection_reason"),
                    resultSet.getInt("likes"),
                    resultSet.getInt("dislikes")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return suggestions;
}

/**
 * Votează o sugestie (like/dislike) pentru un utilizator.
 *
 * @param userId        ID-ul utilizatorului.
 * @param suggestionId  ID-ul sugestiei.
 * @param isLike        True pentru like, False pentru dislike.
 * @return True dacă votul a fost înregistrat, False dacă utilizatorul a votat deja.
 */
public static boolean voteSuggestion(int userId, int suggestionId, boolean isLike) {
    String checkVoteQuery = "SELECT 1 FROM suggestion_votes WHERE user_id = ? AND suggestion_id = ?";
    String insertVoteQuery = "INSERT INTO suggestion_votes (user_id, suggestion_id, vote_type) VALUES (?, ?, ?)";
    String updateSuggestionQuery = isLike
            ? "UPDATE suggestions SET likes = likes + 1 WHERE id = ?"
            : "UPDATE suggestions SET dislikes = dislikes + 1 WHERE id = ?";

    try (Connection connection = getConnection()) {
     
        try (PreparedStatement checkStmt = connection.prepareStatement(checkVoteQuery)) {
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, suggestionId);
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                
                return false;
            }
        }

       
        try (PreparedStatement insertStmt = connection.prepareStatement(insertVoteQuery)) {
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, suggestionId);
            insertStmt.setString(3, isLike ? "like" : "dislike");
            insertStmt.executeUpdate();
        }

       
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSuggestionQuery)) {
            updateStmt.setInt(1, suggestionId);
            updateStmt.executeUpdate();
        }

        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

/**
 * Actualizează statusul unei sugestii.
 *
 * @param suggestionId    ID-ul sugestiei.
 * @param status          Statusul nou ('approved', 'rejected').
 * @param rejectionReason Motivul respingerii (dacă este cazul).
 * @return True dacă actualizarea a avut succes, altfel False.
 */
public static boolean updateSuggestionStatus(int suggestionId, String status, String rejectionReason) {
    String query = "UPDATE suggestions SET status = ?, rejection_reason = ? WHERE id = ?";
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, status);
        statement.setString(2, rejectionReason);
        statement.setInt(3, suggestionId);
        return statement.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

/**
 * Șterge o sugestie din tabel.
 *
 * @param suggestionId ID-ul sugestiei.
 * @return True dacă ștergerea a avut succes, altfel False.
 */
public static boolean deleteSuggestion(int suggestionId) {
    String query = "DELETE FROM suggestions WHERE id = ?";
    try (Connection connection = getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setInt(1, suggestionId);
        return statement.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}


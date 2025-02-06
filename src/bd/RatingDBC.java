package bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clasa `RatingDBC` gestionează funcționalitățile legate de sistemul de rating al jocurilor în baza de date.
 * Aceasta include crearea tabelului, adăugarea rating-urilor, calcularea mediei rating-urilor și obținerea rating-ului unui utilizator.
 * <p>
 * Această clasă interacționează cu baza de date folosind conexiunea oferită de clasa {@link DBC}.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class RatingDBC {

    /**
     * Creează tabelul `ratings` dacă acesta nu există.
     * Tabelul `ratings` stochează rating-urile acordate de utilizatori pentru jocuri.
     * - Fiecare rating are o valoare între 1 și 5.
     * - Relația este unică pentru combinația `game_id` și `user_id`.
     */
    public static void createRatingsTable() {
        String query = "CREATE TABLE IF NOT EXISTS ratings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "game_id INTEGER NOT NULL, " +
                "user_id INTEGER NOT NULL, " +
                "rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5), " +
                "UNIQUE(game_id, user_id), " +
                "FOREIGN KEY (game_id) REFERENCES games(id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id))";
        try (Connection conn = DBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.execute();
            System.out.println("Tabelul 'ratings' a fost creat/verificat.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adaugă sau actualizează un rating pentru un joc specific, acordat de un utilizator.
     *
     * @param gameId  ID-ul jocului pentru care se adaugă rating-ul.
     * @param userId  ID-ul utilizatorului care acordă rating-ul.
     * @param rating  Valoarea rating-ului (între 1 și 5).
     * @return {@code true} dacă operația a fost reușită, {@code false} în caz de eșec.
     */
    public static boolean addRating(int gameId, int userId, int rating) {
        String query = "INSERT INTO ratings (game_id, user_id, rating) VALUES (?, ?, ?) " +
                       "ON CONFLICT(game_id, user_id) DO UPDATE SET rating = excluded.rating";
        try (Connection conn = DBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, rating);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calculează și returnează rating-ul mediu al unui joc.
     *
     * @param gameId ID-ul jocului pentru care se calculează media rating-urilor.
     * @return Media rating-urilor sub formă de {@code double}.
     *         Dacă nu există rating-uri, returnează {@code 0.0}.
     */
    public static double getAverageRating(int gameId) {
        String query = "SELECT AVG(rating) as avg_rating FROM ratings WHERE game_id = ?";
        try (Connection conn = DBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Returnează rating-ul acordat de un utilizator pentru un joc specific.
     *
     * @param gameId ID-ul jocului.
     * @param userId ID-ul utilizatorului.
     * @return Rating-ul acordat (între 1 și 5).
     *         Dacă utilizatorul nu a acordat rating, returnează {@code 0}.
     */
    public static int getUserRating(int gameId, int userId) {
        String query = "SELECT rating FROM ratings WHERE game_id = ? AND user_id = ?";
        try (Connection conn = DBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

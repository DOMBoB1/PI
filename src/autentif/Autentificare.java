package autentif;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bd.DBC;
import model.User;

/**
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 * 
 * Clasa Autentificare oferă funcționalitate pentru verificarea credențialelor
 * unui utilizator pe baza adresei de email sau a numelui de utilizator, împreună
 * cu parola corespunzătoare. Dacă autentificarea reușește, returnează un obiect
 * de tip {@link model.User}.
 */
public class Autentificare {

    /**
     * Verifică dacă există un utilizator în baza de date care corespunde
     * adresei de email sau numelui de utilizator și parolei introduse.
     * 
     * @param emailOrUsername Adresa de email sau numele de utilizator.
     * @param password        Parola asociată contului.
     * @return Obiect {@link User} dacă autentificarea are succes; 
     *         {@code null} dacă nu există niciun utilizator care să corespundă.
     */
    public User authenticate(String emailOrUsername, String password) {
        try (Connection connection = DBC.getConnection()) {
            String query = "SELECT * FROM users WHERE (email = ? OR username = ?) AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, emailOrUsername);
            statement.setString(2, emailOrUsername);
            statement.setString(3, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getString("bio"),
                        resultSet.getString("profile_picture")
                );
            } else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

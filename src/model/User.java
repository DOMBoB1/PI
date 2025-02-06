
package model;
/**
 * Clasa `User` reprezintă un utilizator al aplicației, incluzând informații despre ID, nume de utilizator,
 * email, parolă, biografie și o imagine de profil.
 * <p>
 * Această clasă oferă getterși și setterși pentru gestionarea atributelor utilizatorului, precum și o metodă
 * `toString` pentru afișarea detaliilor utilizatorului într-un format compact.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private String bio;
    private String profilePicture;

    /**
     * Constructorul clasei `User`.
     * 
     * @param id             ID-ul unic al utilizatorului.
     * @param username       Numele de utilizator al acestuia.
     * @param email          Adresa de email a utilizatorului.
     * @param password       Parola utilizatorului.
     * @param bio            Biografia utilizatorului.
     * @param profilePicture URL-ul imaginii de profil a utilizatorului.
     */
    public User(int id, String username, String email, String password, String bio, String profilePicture) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.profilePicture = profilePicture;
    }

    /**
     * Obține ID-ul utilizatorului.
     * 
     * @return ID-ul utilizatorului.
     */
    public int getId() {
        return id;
    }

    /**
     * Obține numele de utilizator.
     * 
     * @return Numele de utilizator.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obține adresa de email a utilizatorului.
     * 
     * @return Adresa de email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Obține parola utilizatorului.
     * 
     * @return Parola utilizatorului.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Obține biografia utilizatorului.
     * 
     * @return Biografia utilizatorului.
     */
    public String getBio() {
        return bio;
    }

    /**
     * Obține calea imaginii de profil a utilizatorului.
     * 
     * @return URL-ul imaginii de profil.
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Setează numele de utilizator.
     * 
     * @param username Noul nume de utilizator.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setează biografia utilizatorului.
     * 
     * @param bio Noua biografie.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Setează imaginea de profil a utilizatorului.
     * 
     * @param profilePicture Noul URL al imaginii de profil.
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Setează parola utilizatorului.
     * 
     * @param password Noua parolă a utilizatorului.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returnează o reprezentare textuală a utilizatorului, incluzând ID-ul și numele acestuia.
     * 
     * @return Reprezentarea textuală a utilizatorului.
     */
    @Override
    public String toString() {
        return "ID: " + id + " | Nume: " + username;
    }
}

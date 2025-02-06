
package model;
/**
 * Clasa `Admin` reprezintă un administrator al aplicației, incluzând informații precum username, email,
 * parolă, biografie, poză de profil și un identificator unic pentru administratori.
 * <p>
 * Această clasă oferă getterși și setterși pentru toate atributele pentru a facilita gestionarea datelor unui administrator.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class Admin {
    private int id;
    private String username;
    private String email;
    private String password;
    private String bio;
    private String profilePicture;
    private String adminId;

    /**
     * Constructorul clasei `Admin`.
     * 
     * @param id             ID-ul unic al administratorului.
     * @param username       Numele de utilizator al administratorului.
     * @param email          Adresa de email a administratorului.
     * @param password       Parola administratorului.
     * @param bio            Biografia administratorului.
     * @param profilePicture URL-ul pozei de profil a administratorului.
     * @param adminId        Identificatorul unic pentru administrator.
     */
    public Admin(int id, String username, String email, String password, String bio, String profilePicture, String adminId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.adminId = adminId;
    }

    /**
     * Obține adresa de email a administratorului.
     * 
     * @return Adresa de email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Setează adresa de email a administratorului.
     * 
     * @param email Noua adresă de email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obține biografia administratorului.
     * 
     * @return Biografia administratorului.
     */
    public String getBio() {
        return bio;
    }

    /**
     * Setează biografia administratorului.
     * 
     * @param bio Noua biografie.
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Obține ID-ul administratorului.
     * 
     * @return ID-ul administratorului.
     */
    public int getId() {
        return id;
    }

    /**
     * Setează ID-ul administratorului.
     * 
     * @param id Noul ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obține numele de utilizator al administratorului.
     * 
     * @return Numele de utilizator.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setează numele de utilizator al administratorului.
     * 
     * @param username Noul nume de utilizator.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obține parola administratorului.
     * 
     * @return Parola administratorului.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setează parola administratorului.
     * 
     * @param password Noua parolă.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obține URL-ul pozei de profil a administratorului.
     * 
     * @return URL-ul pozei de profil.
     */
    public String getProfilePicture() {
        return profilePicture;
    }

    /**
     * Setează URL-ul pozei de profil a administratorului.
     * 
     * @param profilePicture Noul URL al pozei de profil.
     */
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Obține identificatorul unic al administratorului.
     * 
     * @return Identificatorul unic al administratorului.
     */
    public String getAdminId() {
        return adminId;
    }

    /**
     * Setează identificatorul unic al administratorului.
     * 
     * @param adminId Noul identificator unic.
     */
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
}

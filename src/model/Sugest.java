
package model;
/**
 * Clasa `Sugest` reprezintă o sugestie de joc oferită de un utilizator, incluzând informații despre numele jocului,
 * descrierea oficială, dezvoltator, anul lansării, motivele utilizatorului pentru sugestie, precum și starea actuală a sugestiei.
 * <p>
 * Această clasă oferă getterși și setterși pentru toate atributele, permițând gestionarea completă a sugestiilor.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class Sugest {
    private int id;
    private int userId;
    private String gameName;
    private String officialDescription;
    private String developer;
    private int releaseYear;
    private String userReason;
    private String status;
    private String rejectionReason;
    private int likes;
    private int dislikes;

    /**
     * Constructorul clasei `Sugest`.
     * 
     * @param id                  ID-ul unic al sugestiei.
     * @param userId              ID-ul utilizatorului care a oferit sugestia.
     * @param gameName            Numele jocului sugerat.
     * @param officialDescription Descrierea oficială a jocului sugerat.
     * @param developer           Dezvoltatorul jocului sugerat.
     * @param releaseYear         Anul lansării jocului sugerat.
     * @param userReason          Motivul utilizatorului pentru sugestie.
     * @param status              Starea curentă a sugestiei (e.g., "aprobat", "respins").
     * @param rejectionReason     Motivul respingerii (daca există).
     * @param likes               Numărul de aprecieri (like-uri) pentru sugestie.
     * @param dislikes            Numărul de respingeri (dislike-uri) pentru sugestie.
     */
    public Sugest(int id, int userId, String gameName, String officialDescription, String developer, int releaseYear, String userReason, String status, String rejectionReason, int likes, int dislikes) {
        this.id = id;
        this.userId = userId;
        this.gameName = gameName;
        this.officialDescription = officialDescription;
        this.developer = developer;
        this.releaseYear = releaseYear;
        this.userReason = userReason;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    /**
     * Obține ID-ul sugestiei.
     * 
     * @return ID-ul sugestiei.
     */
    public int getId() {
        return id;
    }

    /**
     * Setează ID-ul sugestiei.
     * 
     * @param id Noul ID al sugestiei.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obține ID-ul utilizatorului care a oferit sugestia.
     * 
     * @return ID-ul utilizatorului.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Setează ID-ul utilizatorului care a oferit sugestia.
     * 
     * @param userId Noul ID al utilizatorului.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Obține numele jocului sugerat.
     * 
     * @return Numele jocului.
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * Setează numele jocului sugerat.
     * 
     * @param gameName Noul nume al jocului.
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * Obține descrierea oficială a jocului sugerat.
     * 
     * @return Descrierea oficială.
     */
    public String getOfficialDescription() {
        return officialDescription;
    }

    /**
     * Setează descrierea oficială a jocului sugerat.
     * 
     * @param officialDescription Noua descriere oficială.
     */
    public void setOfficialDescription(String officialDescription) {
        this.officialDescription = officialDescription;
    }

    /**
     * Obține dezvoltatorul jocului sugerat.
     * 
     * @return Dezvoltatorul jocului.
     */
    public String getDeveloper() {
        return developer;
    }

    /**
     * Setează dezvoltatorul jocului sugerat.
     * 
     * @param developer Noul dezvoltator al jocului.
     */
    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    /**
     * Obține anul lansării jocului sugerat.
     * 
     * @return Anul lansării jocului.
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Setează anul lansării jocului sugerat.
     * 
     * @param releaseYear Noul an al lansării.
     */
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Obține motivul utilizatorului pentru sugestie.
     * 
     * @return Motivul utilizatorului.
     */
    public String getUserReason() {
        return userReason;
    }

    /**
     * Setează motivul utilizatorului pentru sugestie.
     * 
     * @param userReason Noul motiv al utilizatorului.
     */
    public void setUserReason(String userReason) {
        this.userReason = userReason;
    }

    /**
     * Obține starea actuală a sugestiei.
     * 
     * @return Starea sugestiei.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setează starea actuală a sugestiei.
     * 
     * @param status Noua stare a sugestiei.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Obține motivul respingerii sugestiei, dacă există.
     * 
     * @return Motivul respingerii.
     */
    public String getRejectionReason() {
        return rejectionReason;
    }

    /**
     * Setează motivul respingerii sugestiei.
     * 
     * @param rejectionReason Noul motiv al respingerii.
     */
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    /**
     * Obține numărul de aprecieri pentru sugestie.
     * 
     * @return Numărul de like-uri.
     */
    public int getLikes() {
        return likes;
    }

    /**
     * Setează numărul de aprecieri pentru sugestie.
     * 
     * @param likes Noul număr de like-uri.
     */
    public void setLikes(int likes) {
        this.likes = likes;
    }

    /**
     * Obține numărul de respingeri pentru sugestie.
     * 
     * @return Numărul de dislike-uri.
     */
    public int getDislikes() {
        return dislikes;
    }

    /**
     * Setează numărul de respingeri pentru sugestie.
     * 
     * @param dislikes Noul număr de dislike-uri.
     */
    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}


package model;
/**
 * Clasa `Game` reprezintă un joc din sistem, incluzând informații precum nume, descriere, rating,
 * gen, anul lansării, dezvoltator și calea către iconița asociată.
 * <p>
 * Această clasă oferă getterși și setterși pentru toate atributele, precum și o metodă
 * `toString` pentru o afișare lizibilă a detaliilor jocului.
 * </p>
 * 
 * @author Dumitras Olga-Maria
 * @date 12/18/2024
 */
public class Game {
    private int id;
    private String name;
    private String description;
    private int rating;
    private String genre;
    private int releaseYear;
    private String developer;
    private String iconPath;

    /**
     * Constructorul clasei `Game`.
     * 
     * @param id          ID-ul unic al jocului.
     * @param name        Numele jocului.
     * @param description Descrierea jocului.
     * @param rating      Rating-ul jocului.
     * @param genre       Genul jocului.
     * @param releaseYear Anul lansării jocului.
     * @param developer   Dezvoltatorul jocului.
     * @param iconPath    Calea către iconița jocului.
     */
    public Game(int id, String name, String description, int rating, String genre, int releaseYear, String developer, String iconPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.developer = developer;
        this.iconPath = iconPath;
    }

    /**
     * Obține ID-ul jocului.
     * 
     * @return ID-ul jocului.
     */
    public int getId() {
        return id;
    }

    /**
     * Setează ID-ul jocului.
     * 
     * @param id Noul ID al jocului.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obține numele jocului.
     * 
     * @return Numele jocului.
     */
    public String getName() {
        return name;
    }

    /**
     * Setează numele jocului.
     * 
     * @param name Noul nume al jocului.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obține descrierea jocului.
     * 
     * @return Descrierea jocului.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setează descrierea jocului.
     * 
     * @param description Noua descriere a jocului.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obține rating-ul jocului.
     * 
     * @return Rating-ul jocului.
     */
    public int getRating() {
        return rating;
    }

    /**
     * Setează rating-ul jocului.
     * 
     * @param rating Noul rating al jocului.
     */
    public void setRating(int rating) {
        this.rating = rating;
    }

    /**
     * Obține genul jocului.
     * 
     * @return Genul jocului.
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Setează genul jocului.
     * 
     * @param genre Noul gen al jocului.
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Obține anul lansării jocului.
     * 
     * @return Anul lansării jocului.
     */
    public int getReleaseYear() {
        return releaseYear;
    }

    /**
     * Setează anul lansării jocului.
     * 
     * @param releaseYear Noul an al lansării jocului.
     */
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    /**
     * Obține dezvoltatorul jocului.
     * 
     * @return Dezvoltatorul jocului.
     */
    public String getDeveloper() {
        return developer;
    }

    /**
     * Setează dezvoltatorul jocului.
     * 
     * @param developer Noul dezvoltator al jocului.
     */
    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    /**
     * Obține calea către iconița jocului.
     * 
     * @return Calea către iconița jocului.
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * Setează calea către iconița jocului.
     * 
     * @param iconPath Noua cale a iconiței jocului.
     */
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    /**
     * Returnează o reprezentare textuală a jocului, incluzând numele, anul lansării, genul și dezvoltatorul.
     * 
     * @return Reprezentarea textuală a jocului.
     */
    @Override
    public String toString() {
        return name + " (" + releaseYear + ") - " + genre + " - " + developer;
    }
}

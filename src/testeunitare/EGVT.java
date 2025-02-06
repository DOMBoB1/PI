package testeunitare;

import model.Game;
import bd.GameDBC;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EGVT {

    @Test
    public void testAddGame_AddsGameSuccessfully() {
        Game newGame = new Game(0, "Test Game", "Test Description", 5, "Adventure", 2024, "Test Dev", "iconPath.png");
        int gameId = GameDBC.addGame(newGame);
        assertTrue(gameId > 0, "Jocul nu a fost adăugat cu succes.");
    }

    @Test
    public void testEditGame_UpdatesGameDetails() {
        Game existingGame = new Game(1, "Old Game", "Old Description", 3, "Action", 2020, "Old Dev", "oldIcon.png");
        existingGame.setName("Updated Game");
        existingGame.setGenre("Adventure");
        existingGame.setReleaseYear(2025);
        existingGame.setDescription("Updated Description");
        existingGame.setIconPath("newIcon.png");

        boolean result = GameDBC.updateGame(existingGame);
        assertTrue(result, "Modificările jocului nu au fost salvate.");
    }

    @Test
    public void testAddAdditionalImages_AddsImagesSuccessfully() {
        int gameId = 1;
        List<File> additionalImages = new ArrayList<>();
        additionalImages.add(new File("image1.png"));
        additionalImages.add(new File("image2.png"));

        boolean result = GameDBC.addGameImages(gameId, additionalImages);
        assertTrue(result, "Imaginile suplimentare nu au fost adăugate.");
    }

    @Test
    public void testMandatoryFieldsValidation_FailsOnEmptyFields() {
        Game invalidGame = new Game(0, "", "", 0, "", 0, "", "");
        int gameId = GameDBC.addGame(invalidGame);
        assertEquals(-1, gameId, "Jocul cu câmpuri goale a fost adăugat incorect.");
    }

    @Test
    public void testDeleteImage_DeletesSpecificImage() {
        int gameId = 1;
        String imagePath = "image1.png";

        boolean result = GameDBC.deleteImage(gameId, imagePath);
        assertTrue(result, "Imaginea specificată nu a fost ștearsă.");
    }
}

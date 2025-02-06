package testeunitare;

import model.Game;
import bd.GameDBC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class EGF {

    @Test
    public void testEditGame_UpdatesGameDetails() {
        Game gameToEdit = new Game(1, "Old Name", "Old Description", 5, "Action", 2020, "Old Dev", "oldIcon.png");
        gameToEdit.setName("Updated Game");
        gameToEdit.setGenre("RPG");
        gameToEdit.setReleaseYear(2025);
        gameToEdit.setDescription("New Description");
        gameToEdit.setIconPath("updatedIcon.png");

        boolean result = GameDBC.updateGame(gameToEdit);
        assertTrue(result, "Jocul nu a fost actualizat cu succes.");
    }

    @Test
    public void testDeleteImage_RemovesSelectedImage() {
        int gameId = 1;
        String imagePath = "oldImage.png";
        boolean result = GameDBC.deleteImage(gameId, imagePath);
        assertTrue(result, "Imaginea nu a fost ștersă cu succes din baza de date.");
    }

    @Test
    public void testSaveGame_ValidatesMandatoryFields() {
        Game incompleteGame = new Game(1, "", "", 0, "", 2020, "", "");
        boolean result = GameDBC.updateGame(incompleteGame);
        assertFalse(result, "Metoda permite salvarea unui joc cu câmpuri incomplete.");
    }

    @Test
    public void testChangeIcon_UpdatesIconPathSuccessfully() {
        Game gameWithNewIcon = new Game(1, "Test Game", "Description", 5, "Action", 2024, "Test Dev", "newIcon.png");
        boolean result = GameDBC.updateGame(gameWithNewIcon);
        assertTrue(result, "Calea iconiței nu a fost actualizată cu succes.");
    }

    @Test
    public void testLoadGameImages_ReturnsImageList() {
        int gameId = 1;
        List<String> images = GameDBC.getGameImages(gameId);
        assertNotNull(images, "Lista de imagini nu a fost încărcată.");
        assertFalse(images.isEmpty(), "Lista de imagini este goală.");
    }
}

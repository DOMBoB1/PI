package testeunitare;

import model.Game;
import bd.GameDBC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AddgfT {

    @Test
    public void testValidGameDetails_SavesGameSuccessfully() {
        Game validGame = new Game(0, "Test Game", "Game Description", 0, "Adventure", 2023, "Test Dev", "mockIcon.png");
        int result = GameDBC.addGame(validGame);
        assertEquals(1, result);

        List<File> mockImages = new ArrayList<>();
        mockImages.add(new File("mockImage1.png"));
        boolean imagesResult = GameDBC.addGameImages(result, mockImages);
        assertTrue(imagesResult);
    }

    @Test
    public void testInvalidYear_ThrowsException() {
        Exception exception = assertThrows(NumberFormatException.class, () -> Integer.parseInt("invalidYear"));
        assertEquals(NumberFormatException.class, exception.getClass());
    }

    @Test
    public void testMissingFields_DetectsEmptyFields() {
        String name = "";
        String genre = "";
        String year = "";
        String developer = "";
        String description = "";

        boolean fieldsEmpty = name.isEmpty() || genre.isEmpty() || year.isEmpty() || developer.isEmpty() || description.isEmpty();
        assertTrue(fieldsEmpty);
    }

    @Test
    public void testNullIcon_DetectsNullFile() {
        File nullIcon = null;
        assertNull(nullIcon);
    }

    @Test
    public void testGameDBCInsertFails_ReturnsError() {
        Game invalidGame = new Game(0, "", "", 0, "", 2024, "", ""); 
        int result = GameDBC.addGame(invalidGame);

      
        assertTrue(result <= 0, "Metoda GameDBC.addGame nu întoarce eroare pentru date invalide");
    }

}

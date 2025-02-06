package testeunitare;

import model.Game;
import bd.GameDBC;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class AG {

    @Test
    public void testAddGame_SavesGameSuccessfully() {
        Game newGame = new Game(0, "New Game", "Description", 5, "Action", 2024, "Test Dev", "iconPath.png");
        int result = GameDBC.addGame(newGame);
        assertTrue(result > 0, "Jocul nu a fost adăugat cu succes.");
    }

    @Test
    public void testDeleteGame_RemovesGameSuccessfully() {
        int gameId = 1; 
        boolean deleteResult = GameDBC.deleteGame(gameId);
        assertTrue(deleteResult, "Jocul nu a fost șters din baza de date.");
    }

    @Test
    public void testDeleteGame_GameNotFound() {
        int invalidGameId = -1; 
        boolean deleteResult = GameDBC.deleteGame(invalidGameId);
        assertFalse(deleteResult, "Metoda returnează succes pentru un joc inexistent.");
    }

    @Test
    public void testEditGame_UpdatesGameDetails() {
        Game updatedGame = new Game(1, "Updated Game", "New Description", 5, "RPG", 2025, "Updated Dev", "updatedIcon.png");
        boolean updateResult = GameDBC.updateGame(updatedGame);
        assertTrue(updateResult, "Jocul nu a fost actualizat cu succes.");
    }

    @Test
    public void testViewGame_LoadsGameDetails() {
        int gameId = 1; 
        Game loadedGame = GameDBC.getAllGames().stream()
                .filter(game -> game.getId() == gameId)
                .findFirst()
                .orElse(null);
        
        assertNotNull(loadedGame, "Detaliile jocului nu au fost încărcate.");
        assertEquals(gameId, loadedGame.getId(), "ID-ul jocului nu se potrivește.");
    }
}

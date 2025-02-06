package testeunitare;

import model.Game;
import bd.GameDBC;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class GLBT {

    @Test
    public void testDeleteGame_RemovesGameSuccessfully() {
        int gameId = 1;
        boolean result = GameDBC.deleteGame(gameId);
        assertTrue(result, "Jocul nu a fost șters din baza de date.");
    }

    @Test
    public void testDeleteGame_FailsForInvalidId() {
        int invalidGameId = -1;
        boolean result = GameDBC.deleteGame(invalidGameId);
        assertFalse(result, "Metoda returnează succes pentru un ID invalid.");
    }

    @Test
    public void testLoadGames_ReturnsGameList() {
        List<Game> games = GameDBC.getAllGames();
        assertNotNull(games, "Lista de jocuri nu a fost încărcată.");
        assertFalse(games.isEmpty(), "Lista de jocuri este goală.");
    }

    @Test
    public void testAddGame_AddsGameSuccessfully() {
        Game newGame = new Game(0, "New Game", "Description", 5, "Adventure", 2024, "Developer", "iconPath.png");
        int gameId = GameDBC.addGame(newGame);
        assertTrue(gameId > 0, "Jocul nu a fost adăugat cu succes.");
    }

    @Test
    public void testUpdateGame_UpdatesExistingGame() {
        Game updatedGame = new Game(1, "Updated Game", "Updated Description", 4, "RPG", 2025, "Updated Dev", "updatedIcon.png");
        boolean result = GameDBC.updateGame(updatedGame);
        assertTrue(result, "Jocul nu a fost actualizat cu succes.");
    }
}

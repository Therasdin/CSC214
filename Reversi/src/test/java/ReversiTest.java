
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReversiTest {

    @Test
    void testNewGameInitialization() {
        // Initialize game state
        Reversi.newGame();
        
        // Check if the initial game board is correctly set up
        int[][] board = Reversi.board;
        assertEquals(2, board[3][3]);
        assertEquals(2, board[4][4]);
        assertEquals(1, board[3][4]);
        assertEquals(1, board[4][3]);
        
        // Check all other positions are empty
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i == 3 && j == 3) || (i == 4 && j == 4) || (i == 3 && j == 4) || (i == 4 && j == 3)) {
                    continue;
                }
                assertEquals(0, board[i][j], "Board position [" + i + "][" + j + "] should be empty.");
            }
        }
    }
    
    @Test
    void testMoreMovesForBlack() {
        // Set up a scenario to test for more moves available for black
        Reversi.newGame();
        assertTrue(Reversi.moreMoves(Reversi.BLACK), "Black should have moves available at the start of the game.");
    }
    
    @Test
    void testNoMoreMoves() {
        // Set up a board state with no moves for a given player
        Reversi.newGame();
        // Artificially set up a board state with no possible moves for black
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Reversi.board[i][j] = Reversi.WHITE;
            }
        }
        assertFalse(Reversi.moreMoves(Reversi.BLACK), "Black should have no moves available with a fully white board.");
    }
}

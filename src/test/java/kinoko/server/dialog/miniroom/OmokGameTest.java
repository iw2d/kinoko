package kinoko.server.dialog.miniroom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OmokGameTest {

    @Test
    void testIsValid() {
        OmokGame omokGame = new OmokGame();
        // out of bounds
        assertFalse(omokGame.isValid(-1, -1));
        assertFalse(omokGame.isValid(15, 15));
        // already placed
        assertTrue(omokGame.isValid(0, 0));
        omokGame.putStone(0, 0, 1);
        assertFalse(omokGame.isValid(0, 0));
    }

    @Test
    void testThreeThree() {
        // horizontal/vertical three-three
        OmokGame omokGame = new OmokGame();
        omokGame.putStone(4, 5, 1);
        omokGame.putStone(6, 5, 1);
        omokGame.putStone(5, 4, 1);
        omokGame.putStone(5, 6, 1);
        assertTrue(omokGame.checkThreeThree(5, 5, 1));

        // horizontal/vertical broken three-three
        omokGame = new OmokGame();
        omokGame.putStone(3, 5, 1);
        omokGame.putStone(6, 5, 1);
        omokGame.putStone(5, 6, 1);
        omokGame.putStone(5, 8, 1);
        assertTrue(omokGame.checkThreeThree(5, 5, 1));

        // diagonal three-three
        omokGame = new OmokGame();
        omokGame.putStone(4, 4, 1);
        omokGame.putStone(6, 6, 1);
        omokGame.putStone(4, 6, 1);
        omokGame.putStone(6, 4, 1);
        assertTrue(omokGame.checkThreeThree(5, 5, 1));

        // diagonal broken three-three
        omokGame = new OmokGame();
        omokGame.putStone(3, 3, 1);
        omokGame.putStone(6, 6, 1);
        omokGame.putStone(7, 3, 1);
        omokGame.putStone(8, 2, 1);
        assertTrue(omokGame.checkThreeThree(5, 5, 1));

        // diagonal broken three-four
        omokGame = new OmokGame();
        omokGame.putStone(3, 3, 1);
        omokGame.putStone(6, 6, 1);
        omokGame.putStone(7, 7, 1);
        omokGame.putStone(7, 3, 1);
        omokGame.putStone(8, 2, 1);
        assertFalse(omokGame.checkThreeThree(5, 5, 1));

        // three-four
        omokGame = new OmokGame();
        omokGame.putStone(3, 5, 1);
        omokGame.putStone(4, 5, 1);
        omokGame.putStone(6, 5, 1);
        omokGame.putStone(5, 4, 1);
        omokGame.putStone(5, 6, 1);
        assertFalse(omokGame.checkThreeThree(5, 5, 1));

        // three-five
        omokGame = new OmokGame();
        omokGame.putStone(2, 5, 1);
        omokGame.putStone(3, 5, 1);
        omokGame.putStone(4, 5, 1);
        omokGame.putStone(6, 5, 1);
        omokGame.putStone(5, 4, 1);
        omokGame.putStone(5, 6, 1);
        assertFalse(omokGame.checkThreeThree(5, 5, 1));

        // blocked by wall
        omokGame.putStone(0, 0, 1);
        omokGame.putStone(1, 1, 1);
        omokGame.putStone(3, 1, 1);
        omokGame.putStone(1, 3, 1);
        assertFalse(omokGame.checkThreeThree(2, 2, 1));

        // blocked by other
        omokGame.putStone(4, 5, 1);
        omokGame.putStone(6, 5, 1);
        omokGame.putStone(5, 4, 1);
        omokGame.putStone(5, 6, 1);
        omokGame.putStone(5, 7, 2);
        assertFalse(omokGame.checkThreeThree(5, 5, 1));
    }

    @Test
    void testCheckWin() {
        OmokGame omokGame = new OmokGame();
        omokGame.putStone(5, 5, 1);
        omokGame.putStone(6, 5, 1);
        // 7, 5 for 5 in a row
        omokGame.putStone(8, 5, 1);
        omokGame.putStone(9, 5, 1);
        assertTrue(omokGame.checkWin(7, 5, 1));

        omokGame = new OmokGame();
        omokGame.putStone(1, 1, 1);
        omokGame.putStone(2, 2, 1);
        // 3, 3 for 5 in a row
        omokGame.putStone(4, 4, 1);
        omokGame.putStone(5, 5, 1);
        assertTrue(omokGame.checkWin(3, 3, 1));

        omokGame = new OmokGame();
        omokGame.putStone(5, 5, 1);
        omokGame.putStone(6, 5, 1);
        // 7, 5 for 6 in a row, which does not count as a win
        omokGame.putStone(8, 5, 1);
        omokGame.putStone(9, 5, 1);
        omokGame.putStone(10, 5, 1);
        assertFalse(omokGame.checkWin(7, 5, 1));
    }
}
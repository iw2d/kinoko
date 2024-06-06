package kinoko.server.dialog.miniroom;

import kinoko.util.Triple;

import java.util.ArrayList;
import java.util.List;

public final class OmokGame {
    public static final int BOARD_SIZE = 15;
    private final int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
    private final List<Triple<Integer, Integer, Integer>> history = new ArrayList<>(); // x, y, type

    public boolean isScorePenalty() {
        return history.size() < 6;
    }

    public boolean isValid(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE && board[x][y] == 0;
    }

    public void putStone(int x, int y, int type) {
        board[x][y] = type;
        history.add(new Triple<>(x, y, type));
    }

    public int retreat() {
        int count;
        for (count = 0; count < 2; count++) {
            if (history.isEmpty()) {
                break;
            }
            final var lastMove = history.remove(history.size() - 1);
            board[lastMove.getFirst()][lastMove.getSecond()] = 0;
        }
        return count;
    }

    public boolean checkWin(int x, int y, int type) {
        // horizontal
        int left = 0, right = 0;
        for (int i = x - 1; i >= 0; i--) {
            if (board[i][y] == type) {
                left++;
            } else {
                break;
            }
        }
        for (int i = x + 1; i < BOARD_SIZE; i++) {
            if (board[i][y] == type) {
                right++;
            } else {
                break;
            }
        }
        if (left + right + 1 == 5) {
            return true;
        }
        // vertical
        int top = 0, bottom = 0;
        for (int j = y - 1; j >= 0; j--) {
            if (board[x][j] == type) {
                top++;
            } else {
                break;
            }
        }
        for (int j = y + 1; j < BOARD_SIZE; j++) {
            if (board[x][j] == type) {
                bottom++;
            } else {
                break;
            }
        }
        if (top + bottom + 1 == 5) {
            return true;
        }
        // diagonal TL-BR
        int topLeft = 0, bottomRight = 0;
        for (int k = 1; (x - k >= 0 && y - k >= 0); k++) {
            if (board[x - k][y - k] == type) {
                topLeft++;
            } else {
                break;
            }
        }
        for (int k = 1; (x + k < BOARD_SIZE && y + k < BOARD_SIZE); k++) {
            if (board[x + k][y + k] == type) {
                bottomRight++;
            } else {
                break;
            }
        }
        if (topLeft + bottomRight + 1 == 5) {
            return true;
        }
        // diagonal BL-TR
        int bottomLeft = 0, topRight = 0;
        for (int k = 1; (x - k >= 0 && y + k < BOARD_SIZE); k++) {
            if (board[x - k][y + k] == type) {
                bottomLeft++;
            } else {
                break;
            }
        }
        for (int k = 1; (x + k < BOARD_SIZE && y - k >= 0); k++) {
            if (board[x + k][y - k] == type) {
                topRight++;
            } else {
                break;
            }
        }
        return bottomLeft + topRight + 1 == 5;
    }

    public boolean checkThreeThree(int x, int y, int type) {
        int a, b, threeCount = 0;
        boolean isBroken;
        // horizontal
        int left = 0, right = 0;
        isBroken = false;
        for (a = x - 1; a >= 0; a--) {
            if (board[a][y] == type) {
                left++;
            } else if (!isBroken && a - 1 >= 0 && board[a][y] == 0 && board[a - 1][y] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        for (b = x + 1; b < BOARD_SIZE; b++) {
            if (board[b][y] == type) {
                right++;
            } else if (!isBroken && b + 1 < BOARD_SIZE && board[a][y] == 0 && board[b + 1][y] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        if (left + right + 1 == 3 && a >= 0 && b < BOARD_SIZE && board[a][y] == 0 && board[b][y] == 0) {
            threeCount++;
        }
        // vertical
        int top = 0, bottom = 0;
        isBroken = false;
        for (a = y - 1; a >= 0; a--) {
            if (board[x][a] == type) {
                top++;
            } else if (!isBroken && a - 1 >= 0 && board[x][a] == 0 && board[x][a - 1] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        for (b = y + 1; b < BOARD_SIZE; b++) {
            if (board[x][b] == type) {
                bottom++;
            } else if (!isBroken && b + 1 < BOARD_SIZE && board[x][b] == 0 && board[x][b + 1] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        if (top + bottom + 1 == 3 && a >= 0 && b < BOARD_SIZE && board[x][a] == 0 && board[x][b] == 0) {
            threeCount++;
        }
        // diagonal TL-BR
        int topLeft = 0, bottomRight = 0;
        isBroken = false;
        for (a = 1; (x - a >= 0 && y - a >= 0); a++) {
            if (board[x - a][y - a] == type) {
                topLeft++;
            } else if (!isBroken && x - a - 1 >= 0 && y - a - 1 >= 0 &&
                    board[x - a][y - a] == 0 && board[x - a - 1][y - a - 1] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        for (b = 1; (x + b < BOARD_SIZE && y + b < BOARD_SIZE); b++) {
            if (board[x + b][y + b] == type) {
                bottomRight++;
            } else if (!isBroken && x + b + 1 < BOARD_SIZE && y + b + 1 < BOARD_SIZE &&
                    board[x + b][y + b] == 0 && board[x + b + 1][y + b + 1] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        if (topLeft + bottomRight + 1 == 3 && x - a >= 0 && y - a >= 0 && x + b < BOARD_SIZE && y + b < BOARD_SIZE &&
                board[x - a][y - a] == 0 && board[x + b][y + b] == 0) {
            threeCount++;
        }
        // diagonal BL-TR
        int bottomLeft = 0, topRight = 0;
        isBroken = false;
        for (a = 1; (x - a >= 0 && y + a < BOARD_SIZE); a++) {
            if (board[x - a][y + a] == type) {
                bottomLeft++;
            } else if (!isBroken && x - a - 1 >= 0 && y + a + 1 < BOARD_SIZE &&
                    board[x - a][y + a] == 0 && board[x - a - 1][y + a + 1] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        for (b = 1; (x + b < BOARD_SIZE && y - b >= 0); b++) {
            if (board[x + b][y - b] == type) {
                topRight++;
            } else if (!isBroken && x + b + 1 < BOARD_SIZE && y - b - 1 >= 0 &&
                    board[x + b][y - b] == 0 && board[x + b + 1][y - b - 1] == type) {
                isBroken = true;
            } else {
                break;
            }
        }
        if (bottomLeft + topRight + 1 == 3 && x - a >= 0 && y + a < BOARD_SIZE && x + b < BOARD_SIZE && y - b >= 0 &&
                board[x - a][y + a] == 0 && board[x + b][y - b] == 0) {
            threeCount++;
        }
        return threeCount >= 2;
    }
}

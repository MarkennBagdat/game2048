import java.util.Random;
import java.util.Scanner;

public class Game2048 {
    private final int size = 4;
    private int[][] board;
    private int score;
    private boolean gameOver;
    private int bestScore;
    private boolean botEnabled;
    private int[][] testBoard;

    public Game2048() {
        board = new int[size][size];
        score = 0;
        gameOver = false;
        bestScore = 0;
        botEnabled = false;
    }

    public void play() {
        initializeBoard();

        while (!gameOver) {
            printBoard();
            System.out.println("Score: " + score);
            System.out.println("Best Score: " + bestScore);
            System.out.println("Use WASD keys (W - up,A - left,S - down,D - right)");
            System.out.println("Press B to toggle bot mode");
            System.out.print("Enter your move: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("B")) {
                botEnabled = !botEnabled;
                continue;
            }
            moveTiles(testBoard, input);
            addRandomTile();

            if (score > bestScore) {
                bestScore = score;
            }
            if (isGameOver()) {
                printBoard();
                System.out.println("Game Over! Your score is: " + score);
                gameOver = true;
            }
            if (botEnabled) {
                makeBotMove();
            }
        }
    }

    private void initializeBoard() {
        addRandomTile();
        addRandomTile();
    }

    private void addRandomTile() {
        Random random = new Random();
        int row, col;

        do {
            row = random.nextInt(size);
            col = random.nextInt(size);
        } while (board[row][col] != 0);

        board[row][col] = random.nextInt(2) + 1; // рандомная генерация 2 и 4
    }

    private void printBoard() {
        System.out.println("-------------");

        for (int[] row : board) {
            for (int cell : row) {
                System.out.print("|" + (cell == 0 ? "    " : String.format("%4d", cell)));
            }
            System.out.println("|\n-------------");
        }
    }

    private void moveTiles(int[][] testBoard, String direction) {
        boolean moved = false;

        switch (direction) {
            case "W":
                moved = moveUp();
                break;
            case "A":
                moved = moveLeft();
                break;
            case "S":
                moved = moveDown();
                break;
            case "D":
                moved = moveRight();
                break;
        }
        if (moved) {
            addRandomTile();
        }
    }

    private boolean moveUp() {
        boolean moved = false;
        for (int col = 0; col < size; col++) {
            for (int row = 1; row < size; row++) {
                if (board[row][col] != 0) {
                    int currentRow = row;
                    while (currentRow > 0 && board[currentRow - 1][col] == 0) {
                        board[currentRow - 1][col] = board[currentRow][col];
                        board[currentRow][col] = 0;
                        currentRow--;
                        moved = true;
                    }

                    if (currentRow > 0 && board[currentRow - 1][col] == board[currentRow][col]) {
                        board[currentRow - 1][col] *= 2;
                        score += board[currentRow - 1][col];
                        board[currentRow][col] = 0;
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    private boolean moveLeft() {
        boolean moved = false;
        for (int row = 0; row < size; row++) {
            for (int col = 1; col < size; col++) {
                if (board[row][col] != 0) {
                    int currentCol = col;
                    while (currentCol > 0 && board[row][currentCol - 1] == 0) {
                        board[row][currentCol - 1] = board[row][currentCol];
                        board[row][currentCol] = 0;
                        currentCol--;
                        moved = true;
                    }

                    if (currentCol > 0 && board[row][currentCol - 1] == board[row][currentCol]) {
                        board[row][currentCol - 1] *= 2;
                        score += board[row][currentCol - 1];
                        board[row][currentCol] = 0;
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    private boolean moveDown() {
        boolean moved = false;
        for (int col = 0; col < size; col++) {
            for (int row = size - 2; row >= 0; row--) {
                if (board[row][col] != 0) {
                    int currentRow = row;
                    while (currentRow < size - 1 && board[currentRow + 1][col] == 0) {
                        board[currentRow + 1][col] = board[currentRow][col];
                        board[currentRow][col] = 0;
                        currentRow++;
                        moved = true;
                    }
                    if (currentRow < size - 1
                            && board[currentRow + 1][col] == board[currentRow][col]) {
                        board[currentRow + 1][col] *= 2;
                        score += board[currentRow + 1][col];
                        board[currentRow][col] = 0;
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    private boolean moveRight() {
        boolean moved = false;
        for (int row = 0; row < size; row++) {
            for (int col = size - 2; col >= 0; col--) {
                if (board[row][col] != 0) {
                    int currentCol = col;
                    while (currentCol < size - 1 && board[row][currentCol + 1] == 0) {
                        board[row][currentCol + 1] = board[row][currentCol];
                        board[row][currentCol] = 0;
                        currentCol++;
                        moved = true;
                    }
                    if (currentCol < size - 1
                            && board[row][currentCol + 1] == board[row][currentCol]) {
                        board[row][currentCol + 1] *= 2;
                        score += board[row][currentCol + 1];
                        board[row][currentCol] = 0;
                        moved = true;
                    }
                }
            }
        }
        return moved;
    }

    private boolean isGameOver() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == 0 ||
                        (row > 0 && board[row][col] == board[row - 1][col]) ||
                        (row < size - 1 && board[row][col] == board[row + 1][col]) ||
                        (col > 0 && board[row][col] == board[row][col - 1]) ||
                        (col < size - 1 && board[row][col] == board[row][col + 1])) {
                    return false;
                }
            }
        }
        return true;
    }

    private void makeBotMove() {
        int maxTileValue = 0;
        String bestMove = "";
        for (String move : new String[] { "W", "A", "S", "D" }) {
            int[][] testBoard = copyBoard(board);

            moveTiles(testBoard, move);
            int tileValue = getMaxTileValue(testBoard);

            if (tileValue > maxTileValue) {
                maxTileValue = tileValue;
                bestMove = move;
            }
        }
        moveTiles(board, bestMove);
        addRandomTile();
    }

    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[size][size];

        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, size);
        }

        return copy;
    }

    private int getMaxTileValue(int[][] board) {
        int maxTileValue = 0;
        for (int[] row : board) {
            for (int tile : row) {
                maxTileValue = Math.max(maxTileValue, tile);
            }
        }
        return maxTileValue;
    }

    public static void main(String[] args) {
        Game2048 game = new Game2048();
        game.play();
    }
}

package Model;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Game {

    Deque<Player> players;
    Board gameBoard;
    
    // Count arrays for rows, columns, diagonals, and anti-diagonals
    private int[] rowCounts;
    private int[] columnCounts;
    private int diagonalCount;
    private int antiDiagonalCount;
    private int boardSize;

    public void initializeGame() {
        // Creating 2 Players
        players = new LinkedList<>();
        PlayingPieceX crossPiece = new PlayingPieceX();
        Player player1 = new Player("Player1", crossPiece);

        PlayingPieceO noughtsPiece = new PlayingPieceO();
        Player player2 = new Player("Player2", noughtsPiece);

        players.add(player1);
        players.add(player2);

        // Initialize Board
        boardSize = 3;
        gameBoard = new Board(boardSize);
        
        // Initialize count arrays
        rowCounts = new int[boardSize];
        columnCounts = new int[boardSize];
        diagonalCount = 0;
        antiDiagonalCount = 0;
    }

    @SuppressWarnings("resource")
    public String startGame() {
        boolean noWinner = true;
        while (noWinner) {
            // Take out the player whose turn it is and also put the player back in the list
            Player playerTurn = players.removeFirst();

            // Get the free space from the board
            gameBoard.printBoard();
            List<Pair<Integer, Integer>> freeSpaces = gameBoard.getFreeCells();
            if (freeSpaces.isEmpty()) {
                noWinner = false;
                continue;
            }

            // Read the user input
            System.out.print("Player: " + playerTurn.name + " Enter row,column: ");
            Scanner inputScanner = new Scanner(System.in);
            String s = inputScanner.nextLine();
            String[] values = s.split(",");
            int inputRow = Integer.parseInt(values[0]);
            int inputColumn = Integer.parseInt(values[1]);

            // Place the piece
            boolean pieceAddedSuccessfully = gameBoard.addPiece(inputRow, inputColumn, playerTurn.playingPiece);
            if (!pieceAddedSuccessfully) {
                // Player cannot insert the piece into this cell; player has to choose another cell
                System.out.println("Incorrect position chosen, try again");
                players.addFirst(playerTurn);
                continue;
            }

            // Update counts
            boolean isWinningMove = updateCounts(inputRow, inputColumn, playerTurn.playingPiece.pieceType);
            if (isWinningMove) {
                return playerTurn.name;
            }

            players.addLast(playerTurn);
        }

        return "tie";
    }

    private boolean updateCounts(int row, int column, PieceType pieceType) {
        // Update row and column counts
        int pieceValue = pieceType == PieceType.X ? 1 : -1; // Use 1 for X and -1 for O
        
        rowCounts[row] += pieceValue;
        columnCounts[column] += pieceValue;

        // Check row and column for win
        if (Math.abs(rowCounts[row]) == boardSize || Math.abs(columnCounts[column]) == boardSize) {
            return true;
        }

        // Update diagonal counts if on a diagonal
        if (row == column) {
            diagonalCount += pieceValue;
            if (Math.abs(diagonalCount) == boardSize) {
                return true;
            }
        }

        // Update anti-diagonal counts if on an anti-diagonal
        if (row + column == boardSize - 1) {
            antiDiagonalCount += pieceValue;
            if (Math.abs(antiDiagonalCount) == boardSize) {
                return true;
            }
        }

        return false;
    }
}

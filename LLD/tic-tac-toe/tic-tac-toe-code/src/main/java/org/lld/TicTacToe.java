package org.lld;

import java.util.Scanner;

public class TicTacToe {

    // ----- Domain: symbols on the board -----
    enum Mark { EMPTY, X, O }

    // ----- Entity: Player -----
    static class Player {
        private final String name;
        private final Mark mark;

        public Player(String name, Mark mark) {
            this.name = name;
            this.mark = mark;
        }
        public String getName() { return name; }
        public Mark getMark() { return mark; }
    }

    // ----- Entity: Board -----
    static class Board {
        private final int size = 3;
        private final Mark[][] cells = new Mark[size][size];

        public Board() {
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    cells[r][c] = Mark.EMPTY;
                }
            }
        }

        public int getSize() { return size; }

        public boolean isCellEmpty(int row, int col) {
            return cells[row][col] == Mark.EMPTY;
        }

        public boolean placeMark(int row, int col, Mark mark) {
            if (row < 0 || row >= size || col < 0 || col >= size) return false;
            if (!isCellEmpty(row, col)) return false;
            cells[row][col] = mark;
            return true;
        }

        public boolean isFull() {
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    if (cells[r][c] == Mark.EMPTY) return false;
                }
            }
            return true;
        }

        public boolean hasWinningLine(Mark mark) {
            // rows
            for (int r = 0; r < size; r++) {
                if (cells[r][0] == mark && cells[r][1] == mark && cells[r][2] == mark) return true;
            }
            // cols
            for (int c = 0; c < size; c++) {
                if (cells[0][c] == mark && cells[1][c] == mark && cells[2][c] == mark) return true;
            }
            // diagonals
            if (cells[0][0] == mark && cells[1][1] == mark && cells[2][2] == mark) return true;
            if (cells[0][2] == mark && cells[1][1] == mark && cells[2][0] == mark) return true;

            return false;
        }

        public void printBoard() {
            System.out.println();
            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    char ch = switch (cells[r][c]) {
                        case X -> 'X';
                        case O -> 'O';
                        default -> ' ';
                    };
                    System.out.print(" " + ch + " ");
                    if (c < size - 1) System.out.print("|");
                }
                System.out.println();
                if (r < size - 1) System.out.println("---+---+---");
            }
            System.out.println();
        }
    }

    // ----- Orchestrator: Game Controller -----
    static class Game {
        private final Board board = new Board();
        private final Player playerX;
        private final Player playerO;
        private Player currentPlayer;

        public Game(Player playerX, Player playerO) {
            this.playerX = playerX;
            this.playerO = playerO;
            this.currentPlayer = playerX; // X typically goes first
        }

        private void switchTurn() {
            currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
        }

        public void start() {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to Tic Tac Toe!");
            System.out.println("Enter moves as: row col  (1-3 for both)");
            board.printBoard();

            while (true) {
                System.out.printf("%s (%s), your move: ", currentPlayer.getName(),
                        currentPlayer.getMark() == Mark.X ? "X" : "O");

                int rowInput, colInput;
                if (!scanner.hasNextInt()) { scanner.next(); continue; }
                rowInput = scanner.nextInt();
                if (!scanner.hasNextInt()) { scanner.next(); continue; }
                colInput = scanner.nextInt();

                // Convert to 0-based indexes
                int row = rowInput - 1;
                int col = colInput - 1;

                boolean placed = board.placeMark(row, col, currentPlayer.getMark());
                if (!placed) {
                    System.out.println("Invalid move (out of range or occupied). Try again.");
                    continue;
                }

                board.printBoard();

                if (board.hasWinningLine(currentPlayer.getMark())) {
                    System.out.printf("Winner: %s (%s)\n", currentPlayer.getName(),
                            currentPlayer.getMark() == Mark.X ? "X" : "O");
                    break;
                }

                if (board.isFull()) {
                    System.out.println("It's a draw!");
                    break;
                }

                switchTurn();
            }
        }
    }

    // ----- Entry point -----
    public static void main(String[] args) {
        Player p1 = new Player("Player 1", Mark.X);
        Player p2 = new Player("Player 2", Mark.O);
        new Game(p1, p2).start();
    }
}

/*
C:\openjdk\zuluJDK17.0.14\bin\java.exe "-javaagent:C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.3.1.1\lib\idea_rt.jar=63954" -Dfile.encoding=UTF-8 -classpath C:\tmp\git\system-design\LLD\tic-tac-toe\tic-tac-toe-code\target\classes org.lld.TicTacToe
Welcome to Tic Tac Toe!
Enter moves as: row col  (1-3 for both)

   |   |
---+---+---
   |   |
---+---+---
   |   |

Player 1 (X), your move: 11
11
Invalid move (out of range or occupied). Try again.
Player 1 (X), your move: 1 1

 X |   |
---+---+---
   |   |
---+---+---
   |   |

Player 2 (O), your move: 3 3

 X |   |
---+---+---
   |   |
---+---+---
   |   | O

Player 1 (X), your move: 1 2

 X | X |
---+---+---
   |   |
---+---+---
   |   | O

Player 2 (O), your move: 3 1

 X | X |
---+---+---
   |   |
---+---+---
 O |   | O

Player 1 (X), your move: 1 3

 X | X | X
---+---+---
   |   |
---+---+---
 O |   | O

Winner: Player 1 (X)

Process finished with exit code 0

 */



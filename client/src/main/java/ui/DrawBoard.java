package ui;
import chess.ChessGame;

import static ui.EscapeSequences.*;

public class DrawBoard {

    public static void main(String[] args) {
        drawBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
    }

    private void putPiece(int row, int col, ChessGame.TeamColor team) {
        int file;
        int rank;
        switch (team) {
            case WHITE -> {
                rank = row;
                file = col;
            }
        }
    }

    private static boolean annotate(int row, int col, int size, ChessGame.TeamColor team) {
        if (row < 1 || size - row <= 1) {
            if (col > 0 && col < size - 1) {
                char file;
                switch (team) {
                    case WHITE -> file = (char) ('a' + col - 1);
                    case BLACK -> file = (char) ('a' + 8 - col);
                    case null, default -> file = 'a';
                }
                System.out.print(SET_BG_COLOR_DARK_BROWN + SET_TEXT_BOLD);
                System.out.print(" " + file + " ");
                System.out.print(RESET_TEXT_BOLD_FAINT);
            } else {
                System.out.print(SET_BG_COLOR_DARK_BROWN);
                System.out.print(EMPTY);
            }
            return true;
        } else if ((col < 1 || size - col <= 1) && row < size - 1) {
            int rank;
            switch (team) {
                case WHITE -> rank = 9 - row;
                case BLACK -> rank = row;
                case null, default -> rank = 1;
            }
            System.out.print(SET_BG_COLOR_DARK_BROWN + SET_TEXT_BOLD);
            System.out.print(" " + (rank) + " ");
            System.out.print(RESET_TEXT_BOLD_FAINT);
            return true;
            }
        return false;
    }

    public static void drawBoard(ChessGame game, ChessGame.TeamColor team) {
        int size = 10;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (annotate(row, col, size, team)) {
                    continue;
                }
                boolean light = (row + col) % 2 == 0;
                System.out.print(light ? SET_BG_COLOR_BEIGE : SET_BG_COLOR_DARK_GREY);
                System.out.print(EMPTY);
            }
            System.out.print(RESET_BG_COLOR);
            System.out.println();
        }
    }



}

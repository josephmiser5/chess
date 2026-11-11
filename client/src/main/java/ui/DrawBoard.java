package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class DrawBoard {

    ChessGame game;
    ChessGame.TeamColor team;

    public DrawBoard(ChessGame game, ChessGame.TeamColor team) {
        this.team = team;
        this.game = game;
    }

    private static String whichPiece(ChessPiece piece) {
        String strPiece;
        switch (piece.getTeamColor()) {
            case WHITE -> {
                switch (piece.getPieceType()) {
                    case KING -> strPiece = WHITE_KING;
                    case PAWN -> strPiece = WHITE_PAWN;
                    case ROOK -> strPiece = WHITE_ROOK;
                    case QUEEN -> strPiece = WHITE_QUEEN;
                    case BISHOP -> strPiece = WHITE_BISHOP;
                    case KNIGHT -> strPiece = WHITE_KNIGHT;
                    case null, default -> strPiece = "E";
                }
            }
            case BLACK -> {
                switch (piece.getPieceType()) {
                    case KING -> strPiece = BLACK_KING;
                    case PAWN -> strPiece = BLACK_PAWN;
                    case ROOK -> strPiece = BLACK_ROOK;
                    case QUEEN -> strPiece = BLACK_QUEEN;
                    case BISHOP -> strPiece = BLACK_BISHOP;
                    case KNIGHT -> strPiece = BLACK_KNIGHT;
                    case null, default -> strPiece = "E";
                }
            }
            case null, default -> strPiece = "E";
        }
        return strPiece;
        }

    private boolean putPiece(ChessPosition position, boolean squareColor) {
        ChessPiece piece = game.getBoard().getPiece(position);
        if (piece == null) {
            return false;
        }
        System.out.print(squareColor ? SET_BG_COLOR_BEIGE : SET_BG_COLOR_DARK_GREY);
        System.out.print(whichPiece(piece));
        System.out.print(RESET_BG_COLOR);
        return true;
    }

    private boolean annotate(int row, int col, int size) {
        if (row < 1 || size - row <= 1) {
            if (col > 0 && col < size - 1) {
                char file;
                switch (team) {
                    case WHITE -> {
                        file = (char) ('a' + col - 1);
                    }
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

    public void drawBoard() {
        int size = 10;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (annotate(row, col, size)) {
                    continue;
                }
                boolean light = (row + col) % 2 == 0;
                boolean placed;
                switch (team) {
                    case WHITE -> placed = putPiece(new ChessPosition(9 - row, col), light);
                    case BLACK -> placed = putPiece(new ChessPosition(row, 9 - col), light);
                    case null, default -> placed = false;
                }
                if (placed) {continue;}
                System.out.print(light ? SET_BG_COLOR_BEIGE : SET_BG_COLOR_DARK_GREY);
                System.out.print(EMPTY);
            }
            System.out.print(RESET_BG_COLOR);
            System.out.println();
        }
    }



}

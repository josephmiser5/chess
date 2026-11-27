package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;

import java.util.HashSet;

import static ui.PrintStyles.*;

public class GameHandler {

    ServerFacade facade;

    public GameHandler(ServerFacade facade) {
        this.facade = facade;
    }

    public void move(int gameID, String[] tokens) {
        ChessMove move;
        try {
            move = parse(tokens);
        } catch (Exception e) {
            return;
        }
        try {
            facade.move(gameID, move);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void leave(int gameID) {
        try {
            facade.leave(gameID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void resign(int gameID) {
        try {
            facade.resign(gameID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ChessMove parse(String[] tokens) throws Exception {
        if (tokens.length != 3) {
            printError("Please enter a starting and ending square. Example: move e1 e2");
            throw new Exception("Invalid move syntax.");
        }
        String[] start = tokens[1].split("");
        String[] end = tokens[2].split("");
        if (start.length != 2 || end.length != 2) {
            printError("Must enter row letter and column number for start and end positions. Example: move e1 e2");
        }
        int startRow;
        int endRow;
        try {
            startRow = Integer.parseInt(start[1]);
            endRow = Integer.parseInt(end[1]);
        } catch (NumberFormatException e) {
            printError("Row number must be entered after column letter. Example: move e1 e2");
            throw new Exception("Invalid column entry.");
        }
        int startCol = start[0].charAt(0) - 'a' + 1;
        int endCol = end[0].charAt(0) - 'a' + 1;
        if (startCol > 8 || startCol < 1 || endCol > 8 || endCol < 1) {
            printError("Column letters must be a-h. Example: move a1 g1");
            throw new Exception("Invalid row entry.");
        }
        return new ChessMove(new ChessPosition(startRow, startCol),
                new ChessPosition(endRow, endCol), null);
    }

    public void highlightMoves(String[] tokens, ChessGame game) {
        if (tokens.length != 2) {
            printError("Please enter a starting and ending square. Example: move e1 e2");
            return;
        }
        String[] position = tokens[1].split("");
        if (position.length != 2) {
            printError("Must enter row letter and column number for piece. Example: highlight e2");
        }
        int row;
        try {
            row = Integer.parseInt(position[1]);
        } catch (NumberFormatException e) {
            printError("Row number must be entered after column letter. Example: highlight e2");
            return;
        }
        int col = position[0].charAt(0) - 'a' + 1;
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
        ChessPosition chessPosition = new ChessPosition(row, col);
        HashSet<ChessMove> moveHashSet = new HashSet<>(game.validMoves(chessPosition));
        DrawBoard drawBoard = new DrawBoard(piece.getTeamColor(), game, chessPosition, moveHashSet);
        drawBoard.drawBoard();
    }
}

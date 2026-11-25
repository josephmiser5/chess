package ui;

import chess.ChessMove;
import chess.ChessPosition;
import client.ServerFacade;
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
}

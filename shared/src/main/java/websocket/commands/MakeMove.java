package websocket.commands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    ChessMove move;
    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        this.move = move;
        super(CommandType.MAKE_MOVE, authToken, gameID);
    }

    public ChessMove getMove() {
        return move;
    }
}

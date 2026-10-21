package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.Vector;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard currentBoard = new ChessBoard();
    ChessGame.TeamColor turn = TeamColor.WHITE;
    public ChessGame() {
        currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = currentBoard.getPiece(startPosition);
        Vector<ChessMove> allMoves = new Vector<ChessMove>();
        if (piece == null) {
            return new Vector<>();
        }
        Collection<ChessMove> moves = piece.pieceMoves(currentBoard, startPosition);
        for (ChessMove i : moves) {
            ChessBoard tempBoard = new ChessBoard(new BitBoard(currentBoard.board));
            applyMove(tempBoard, i);
            if (!tempBoard.board.isInCheck(piece.getTeamColor())) {
                allMoves.add(i);
            }
        }
        return allMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        boolean isOk = false;
        Collection<ChessMove> validMoves = validMoves(move.startPosition);
        for (ChessMove i : validMoves) {
            if (i.endPosition.equals(move.endPosition)) {
                isOk = true;
                break;
            }
        }
        if (!isOk) {
             throw new chess.InvalidMoveException();
         }
        if (!currentBoard.getPiece(move.startPosition).getTeamColor().equals(turn)) {
            throw new chess.InvalidMoveException();
        }
        applyMove(currentBoard, move);
        turn = (turn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    public void applyMove(ChessBoard board, ChessMove move) {
        ChessPiece piece = board.getPiece(move.startPosition);
        board.board.removeBitPiece(move.startPosition, piece);

        ChessPiece captured = board.getPiece(move.endPosition);
        if (captured != null) {
            board.board.removeBitPiece(move.endPosition, captured);
        }

        if (move.promotionPiece != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.promotionPiece);
        }
        board.addPiece(move.endPosition, piece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return currentBoard.board.isInCheck(teamColor);
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!currentBoard.board.isInCheck(teamColor)) {
            return false;
        }
        return inCheckNext(teamColor);
    }

    boolean inCheckNext(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int y = 1; y <= 8; y++) {
                ChessPiece piece = currentBoard.board.getBitPiece(new ChessPosition(i, y));
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> chessMoves = piece.pieceMoves(currentBoard, new ChessPosition(i, y));
                for (ChessMove move : chessMoves) {
                    ChessBoard tempBoard = new ChessBoard(new BitBoard(currentBoard.board));
                    applyMove(tempBoard, move);
                    if (!tempBoard.board.isInCheck(teamColor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor) && inCheckNext(teamColor)) {
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "_board=" + currentBoard +
                ", _turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(currentBoard, chessGame.currentBoard) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(currentBoard);
        result = 31 * result + Objects.hashCode(turn);
        return result;
    }
}

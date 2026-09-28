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
    ChessBoard _currentBoard = new ChessBoard();
    ChessGame.TeamColor _turn = TeamColor.WHITE;
    public ChessGame() {
        _currentBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return _turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (_turn == TeamColor.WHITE) {
            _turn = TeamColor.BLACK;
        } else {
            _turn = TeamColor.WHITE;
        }
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
        ChessPiece piece = _currentBoard.getPiece(startPosition);
        Vector<ChessMove> allMoves = new Vector<ChessMove>();
        if (piece == null) return new Vector<>();
        Collection<ChessMove> moves = piece.pieceMoves(_currentBoard, startPosition);
        for (ChessMove i : moves) {
            ChessBoard tempBoard = new ChessBoard(new BitBoard(_currentBoard._board));
            applyMove(tempBoard, i);
            if (!tempBoard._board.isInCheck(piece.getTeamColor())) {
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
        Collection<ChessMove> validMoves = validMoves(move.startPosition_);
        for (ChessMove i : validMoves) {
            if (i.endPosition_.equals(move.endPosition_)) {
                isOk = true;
                break;
            }
        }
        if (!isOk) {
             throw new chess.InvalidMoveException();
         }
         applyMove(_currentBoard, move);

        if (_turn == TeamColor.WHITE) {
            _turn = TeamColor.BLACK;
        } else {
            _turn = TeamColor.WHITE;
        }
    }

    public void applyMove(ChessBoard board, ChessMove move) {
        ChessPiece piece = board.getPiece(move.startPosition_);
        board._board.removeBitPiece(move.startPosition_, piece);

        ChessPiece captured = board.getPiece(move.endPosition_);
        if (captured != null) {
            board._board.removeBitPiece(move.endPosition_, captured);
        }

        if (move.promotionPiece_ != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.promotionPiece_);
        }
        board.addPiece(move.endPosition_, piece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return _currentBoard._board.isInCheck(teamColor);
    }

    boolean inCheck(TeamColor teamColor, ChessBoard board) {
        int[] kingPos;
        long kingBoard;
        if (teamColor == TeamColor.WHITE) {
            kingBoard = board._board.whiteKing;
        } else {
            kingBoard = board._board.blackKing;
        }

        if (kingBoard != 0) {
            kingPos = board._board.posGen(kingBoard);
        } else {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int y = 1; y <= 8; y++) {
                ChessPiece piece = board._board.getBitPiece(new ChessPosition(i, y));
                if (piece == null || piece.getTeamColor() == teamColor) continue;
                Collection<ChessMove> chessMoves = piece.pieceMoves(board, new ChessPosition(i, y));
                for (ChessMove move : chessMoves) {
                    if (move.endPosition_.getRow() == kingPos[0] + 1 && move.endPosition_.getColumn()
                            == kingPos[1] + 1) return true;
                }
            }
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!_currentBoard._board.isInCheck(teamColor)) return false;
        return inCheckNext(teamColor);
    }

    boolean inCheckNext(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int y = 1; y <= 8; y++) {
                ChessPiece piece = _currentBoard._board.getBitPiece(new ChessPosition(i, y));
                if (piece == null || piece.getTeamColor() != teamColor) continue;
                Collection<ChessMove> chessMoves = piece.pieceMoves(_currentBoard, new ChessPosition(i, y));
                for (ChessMove move : chessMoves) {
                    ChessBoard tempBoard = new ChessBoard(new BitBoard(_currentBoard._board));
                    applyMove(tempBoard, move);
                    if (!tempBoard._board.isInCheck(teamColor)) return false;
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
        _currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return _currentBoard;
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "_board=" + _currentBoard +
                ", _turn=" + _turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(_currentBoard, chessGame._currentBoard) && _turn == chessGame._turn;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(_currentBoard);
        result = 31 * result + Objects.hashCode(_turn);
        return result;
    }
}

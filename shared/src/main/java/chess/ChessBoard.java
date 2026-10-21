package chess;

import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public BitBoard board;
    public ChessBoard() {
        board = new BitBoard();
    }
    public ChessBoard(BitBoard other) {
        board = other;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        long bitPiece = 1L << col - 1 + (8 * (row - 1));
        board.addBitPiece(piece, bitPiece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board.getBitPiece(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new BitBoard();
        board.whitePawns   = 0x000000000000FF00L;
        board.whiteRooks   = 0x0000000000000081L;
        board.whiteKnights = 0x0000000000000042L;
        board.whiteBishops = 0x0000000000000024L;
        board.whiteQueens  = 0x0000000000000008L;
        board.whiteKing    = 0x0000000000000010L;
        board.blackPawns   = 0x00FF000000000000L;
        board.blackRooks   = 0x8100000000000000L;
        board.blackKnights = 0x4200000000000000L;
        board.blackBishops = 0x2400000000000000L;
        board.blackQueens  = 0x0800000000000000L;
        board.blackKing    = 0x1000000000000000L;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
        return Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "positions_=" + board +
                '}';
    }
}


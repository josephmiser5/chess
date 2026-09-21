package chess;

import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public static String fenStringStart = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    //public ChessPiece[][] board_;
    public BitBoard _board;
    public ChessBoard() {
        //board_ = new ChessPiece[8][8];
        _board = new BitBoard();
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
        //board_[row - 1][col - 1] = piece;
        long bitPiece = 1L << col - 1 + (8 * (row - 1));
        _board.addBitPiece(piece, bitPiece);
    }

    public ChessPiece typeFromFen(char letter) {
        ChessPiece.PieceType piece;
        ChessGame.TeamColor color;
        if (Character.isUpperCase(letter)) {
            color = ChessGame.TeamColor.WHITE;
        } else {
            color = ChessGame.TeamColor.BLACK;
        }
        letter = Character.toLowerCase(letter);
        if (letter == 'r') {
            piece = ChessPiece.PieceType.ROOK;
        } else if (letter == 'n') {
            piece = ChessPiece.PieceType.KNIGHT;
        } else if (letter == 'b') {
            piece = ChessPiece.PieceType.BISHOP;
        } else if (letter == 'q') {
            piece = ChessPiece.PieceType.QUEEN;
        } else if (letter == 'k') {
            piece = ChessPiece.PieceType.KING;
        } else {
            piece = ChessPiece.PieceType.PAWN;
        }
        return new ChessPiece(color, piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return _board.getBitPiece(position);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        _board = new BitBoard();
        _board.whitePawns   = 0x000000000000FF00L;
        _board.whiteRooks   = 0x0000000000000081L;
        _board.whiteKnights = 0x0000000000000042L;
        _board.whiteBishops = 0x0000000000000024L;
        _board.whiteQueens  = 0x0000000000000008L;
        _board.whiteKing    = 0x0000000000000010L;
        _board.blackPawns   = 0x00FF000000000000L;
        _board.blackRooks   = 0x8100000000000000L;
        _board.blackKnights = 0x4200000000000000L;
        _board.blackBishops = 0x2400000000000000L;
        _board.blackQueens  = 0x0800000000000000L;
        _board.blackKing    = 0x1000000000000000L;

        //board_ = new ChessPiece[8][8];
        //int rank = 7;
        //int file = 0;
        //int i = 0;
        //while (fenStringStart.charAt(i) != ' ') {
        //    char square = fenStringStart.charAt(i);
        //    if (square == '/' ) {
        //        file = 0;
        //        rank--;
        //        i++;
        //        continue;
        //    } else if (Character.isDigit(square)) {
        //        file += Character.getNumericValue(square);
        //    } else {
        //        board_[rank][file] = typeFromFen(square);
        //    }
        //    file++;
        //    i++;
        //}
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
        return Objects.equals(_board, that._board);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(_board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "positions_=" + _board +
                '}';
    }

    //@Override
    //public boolean equals(Object o) {
    //    if (o == null || getClass() != o.getClass()) {
    //        return false;
    //    }

    //    ChessBoard that = (ChessBoard) o;
    //    return Arrays.deepEquals(board_, that.board_);
    //}

    //@Override
    //public int hashCode() {
    //    return Arrays.deepHashCode(board_);
    //}
}


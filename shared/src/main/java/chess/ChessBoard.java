package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    public static String fenStringStart = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    private ChessPiece[][] board;
    BitBoard positions;
    public ChessBoard() {
        board = new ChessPiece[8][8];
        positions = new BitBoard();
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
        board[row - 1][col - 1] = piece;
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
        return board[position.getRow() - 1][position.getColumn() - 1];
    }
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        positions = new BitBoard();
        board = new ChessPiece[8][8];
        int rank = 7;
        int file = 0;
        int i = 0;
        while (fenStringStart.charAt(i) != ' ') {
            char square = fenStringStart.charAt(i);
            if (square == '/' ) {
                file = 0;
                rank--;
                i++;
                continue;
            } else if (Character.isDigit(square)) {
                file += Character.getNumericValue(square);
            } else {
                board[rank][file] = typeFromFen(square);
            }
            file++;
            i++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessBoard that = (ChessBoard) o;
        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}


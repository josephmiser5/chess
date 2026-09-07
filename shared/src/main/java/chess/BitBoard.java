package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BitBoard {
    long whitePawns;
    long whiteKing;
    long whiteQueens;
    long whiteRooks;
    long whiteKnights;
    long whiteBishops;
    long blackPawns;
    long blackKing;
    long blackQueens;
    long blackRooks;
    long blackKnights;
    long blackBishops;

    public BitBoard() {
        whitePawns   = 0x000000000000FF00L;
        whiteRooks   = 0x0000000000000081L;
        whiteKnights = 0x0000000000000042L;
        whiteBishops = 0x0000000000000024L;
        whiteQueens  = 0x0000000000000008L;
        whiteKing    = 0x0000000000000010L;
        blackPawns   = 0x00FF000000000000L;
        blackRooks   = 0x8100000000000000L;
        blackKnights = 0x4200000000000000L;
        blackBishops = 0x2400000000000000L;
        blackQueens  = 0x0800000000000000L;
        blackKing    = 0x1000000000000000L;
    }

    public Collection<ChessMove> movedCollection(long pieceBit, ChessPiece.PieceType type, ChessGame.TeamColor color, ChessPosition position) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        if (type.compareTo(ChessPiece.PieceType.QUEEN) == 0) {
            allMoves = moveQueen(pieceBit, position, color);
        }
        return allMoves;
    }

    public long whiteBoard() {
        return whitePawns ^ whiteRooks ^ whiteKnights ^ whiteBishops ^ whiteQueens ^ whiteKing;
    }

    public long blackBoard() {
        return blackPawns ^ blackRooks ^ blackKnights ^ blackBishops ^ blackQueens ^ blackKing;
    }

    public Collection<ChessMove> moveQueen(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        long moveBit = pieceBit;
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }

        while (row < 8) {
            row++;
            moveBit = moveBit << 8;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove  move = new ChessMove(position, end, ChessPiece.PieceType.QUEEN);
            if ((moveBit & teamColor) != 0) {
                break;
            }
            if ((moveBit & opColor) != 0) {
                allMoves.add(move);
                break;
            }
            allMoves.add(move);
        }

        row = position.getRow();
        moveBit = pieceBit;

        while (row > 1) {
            row--;
            moveBit = moveBit >> 8;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, ChessPiece.PieceType.QUEEN);

            if ((moveBit & teamColor) != 0) {
                break;
            }
            if ((moveBit & opColor) != 0) {
                allMoves.add(move);
                break;
            }
            allMoves.add(move);
        }

        row = position.getRow();
        moveBit = pieceBit;

        while (col < 8) {
            col++;
            moveBit = moveBit >> 1;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, ChessPiece.PieceType.QUEEN);
        }

        return allMoves;
        }
    }

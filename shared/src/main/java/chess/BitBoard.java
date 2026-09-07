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
        whitePawns   = 0L;
        whiteRooks   = 0L;
        whiteKnights = 0L;
        whiteBishops = 0L;
        whiteQueens  = 0L;
        whiteKing    = 0L;
        blackPawns   = 0L;
        blackRooks   = 0L;
        blackKnights = 0L;
        blackBishops = 0L;
        blackQueens  = 0L;
        blackKing    = 0L;
    }

    public void addBitPiece(ChessPiece piece, long bitPiece) {
        if (piece.getTeamColor().compareTo(ChessGame.TeamColor.WHITE) == 0) {
            if (piece.getPieceType().compareTo(ChessPiece.PieceType.PAWN) == 0) {
                whitePawns = bitPiece |  whitePawns;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.KING) == 0) {
                whiteKing = bitPiece | whiteKing;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.QUEEN) == 0) {
                whiteQueens = bitPiece |  whiteQueens;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.ROOK) == 0) {
                whiteRooks = bitPiece | whiteRooks;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.KNIGHT) == 0) {
                whiteKnights = bitPiece | whiteKnights;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.BISHOP) == 0) {
                whiteBishops = bitPiece | whiteBishops;
            }
        } else {
            if (piece.getPieceType().compareTo(ChessPiece.PieceType.PAWN) == 0) {
                blackPawns = bitPiece | blackPawns;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.KING) == 0) {
                blackKing = bitPiece ^ blackKing;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.QUEEN) == 0) {
                blackQueens = bitPiece | blackQueens;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.ROOK) == 0) {
                blackRooks = bitPiece | blackRooks;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.KNIGHT) == 0) {
                blackKnights = bitPiece | blackKnights;
            } else if (piece.getPieceType().compareTo(ChessPiece.PieceType.BISHOP) == 0) {
                blackBishops = bitPiece | blackBishops;
            }
        }
    }

    public Collection<ChessMove> movedCollection(long pieceBit, ChessPiece.PieceType type, ChessGame.TeamColor color, ChessPosition position) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        if (type.compareTo(ChessPiece.PieceType.QUEEN) == 0) {
            allMoves = moveQueen(pieceBit, position, color);
        }
        for (ChessMove move : allMoves) {
            System.out.println(move.endPosition_.getRow() + ", " +  move.endPosition_.getColumn());
        }
        return allMoves;
    }

    public long whiteBoard() {
        return whitePawns ^ whiteRooks ^ whiteKnights ^ whiteBishops ^ whiteQueens ^ whiteKing;
    }

    public long blackBoard() {
        return blackPawns ^ blackRooks ^ blackKnights ^ blackBishops ^ blackQueens ^ blackKing;
    }

    public void moveRank(Collection<ChessMove> allMoves, long teamColor, long opColor,
                                          long pieceBit, int row, int col, ChessPosition position) {

        long moveBit = pieceBit;
        while (row < 8) {
            row++;
            moveBit = moveBit << 8;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove  move = new ChessMove(position, end, null);
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
            moveBit = moveBit >>> 8;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, null);

            if ((moveBit & teamColor) != 0) {
                break;
            }
            if ((moveBit & opColor) != 0) {
                allMoves.add(move);
                break;
            }
            allMoves.add(move);
        }
    }


    public void moveFile(Collection<ChessMove> allMoves, long teamColor, long opColor,
                                              long pieceBit, int row, int col, ChessPosition position) {
        long moveBit = pieceBit;
        while (col < 8) {
            col++;
            moveBit = moveBit << 1;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, null);

            if ((moveBit & teamColor) != 0) {
                break;
            }
            if ((moveBit & opColor) != 0) {
                allMoves.add(move);
                break;
            }
            allMoves.add(move);
        }


        col = position.getColumn();
        moveBit = pieceBit;

        while (col > 1) {
            col--;
            moveBit = moveBit >>> 1;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, null);

            if ((moveBit & teamColor) != 0) {
                break;
            }
            if ((moveBit & opColor) != 0) {
                allMoves.add(move);
                break;
            }
            allMoves.add(move);
        }
    }


    public void movePositiveD(Collection<ChessMove> allMoves, long teamColor, long opColor,
                         long pieceBit, int row, int col, ChessPosition position) {
        long moveBit = pieceBit;
        while (col < 8 & row < 8) {
            row++;
            col++;
            moveBit = moveBit << 9;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, null);

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
        col = position.getColumn();
        moveBit = pieceBit;

        while (col > 1 & row > 1) {
            row--;
            col--;
            moveBit = moveBit >>> 9;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, null);

            if ((moveBit & teamColor) != 0) {
                break;
            }
            if ((moveBit & opColor) != 0) {
                allMoves.add(move);
                break;
            }
            allMoves.add(move);
        }
    }

    public void moveNegativeD(Collection<ChessMove> allMoves, long teamColor, long opColor,
                              long pieceBit, int row, int col, ChessPosition position) {
        long moveBit = pieceBit;

        while (col > 1 & row < 8) {
            row++;
            col--;
            moveBit = moveBit << 7;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, null);

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
        col = position.getColumn();
        moveBit = pieceBit;

        while (col < 8 & row > 1) {
            row--;
            col++;
            moveBit = moveBit >>> 7;
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(position, end, null);

            if ((moveBit & teamColor) != 0) {
                break;
            }
            if ((moveBit & opColor) != 0) {
                allMoves.add(move);
                break;
            }
            allMoves.add(move);
        }
    }

    public Collection<ChessMove> moveQueen(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }

        moveRank(allMoves, teamColor, opColor, pieceBit, row, col, position);

        moveFile(allMoves, teamColor, opColor, pieceBit, row, col, position);

        movePositiveD(allMoves, teamColor, opColor, pieceBit, row, col, position);

        moveNegativeD(allMoves, teamColor, opColor, pieceBit, row, col, position);

        return allMoves;
        }
    }

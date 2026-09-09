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
        if (piece == null) {
            return;
        }
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
        } else if (type.compareTo(ChessPiece.PieceType.ROOK) == 0) {
            allMoves = moveRook(pieceBit, position, color);
        } else if (type.compareTo(ChessPiece.PieceType.BISHOP) == 0) {
            allMoves = moveBishop(pieceBit, position, color);
        } else if (type.compareTo(ChessPiece.PieceType.PAWN) == 0) {
            allMoves = movePawn(pieceBit, position, color);
        } else if (type.compareTo(ChessPiece.PieceType.KNIGHT) == 0) {
            allMoves = moveKnight(pieceBit, position, color);
        } else if (type.compareTo(ChessPiece.PieceType.KING) == 0) {
            allMoves = moveKing(pieceBit, position, color);
        }
        for (ChessMove i : allMoves) {
            System.out.println(i.endPosition_.getRow() + ", " + i.endPosition_.getColumn());
        }
        return allMoves;
    }

    private long whiteBoard() {
        return whitePawns ^ whiteRooks ^ whiteKnights ^ whiteBishops ^ whiteQueens ^ whiteKing;
    }

    private long blackBoard() {
        return blackPawns ^ blackRooks ^ blackKnights ^ blackBishops ^ blackQueens ^ blackKing;
    }

    private void moveRank(Collection<ChessMove> allMoves, long teamColor, long opColor,
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


    private void moveFile(Collection<ChessMove> allMoves, long teamColor, long opColor,
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


    private void movePositiveD(Collection<ChessMove> allMoves, long teamColor, long opColor,
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

    private void promotionType(Collection<ChessMove> allMoves, ChessPosition start, ChessPosition end) {
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
    }

    private boolean inBounds(int row, int col) {
        if (row <= 8 & row >= 1 & col <= 8 & col >= 1) {
            return true;
        }
        return false;
    }

    private Collection<ChessMove> moveKing(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }

        record Entry(long bit, int row, int col) {}

        Entry[] kingBits = {new Entry(pieceBit << 8, 1, 0), new Entry(pieceBit << 9, 1, 1),
                new Entry(pieceBit << 7, 1, -1), new Entry(pieceBit >>> 1, 0, -1),
                new Entry(pieceBit << 1, 0, 1), new Entry(pieceBit >>> 8, -1, 0),
                new Entry(pieceBit >>> 9, -1, -1), new Entry(pieceBit >>> 7, -1, 1), };

        for (Entry i : kingBits) {
            if (inBounds(row + i.row, col + i.col) & ((i.bit & teamColor) == 0)) {
                ChessPosition end = new ChessPosition(row + i.row, col + i.col);
                ChessMove move = new ChessMove(position, end, null);
                allMoves.add(move);
            }
        }

        return allMoves;
    }

    private Collection<ChessMove> moveKnight(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        long teamColor = whiteBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
        }

        record Entry(long bit, int row, int col) {}

        Entry[] knightBits = {new Entry(pieceBit << 17, 2, 1), new Entry(pieceBit << 15, 2, -1), new Entry(pieceBit << 10, 1, 2),
                new Entry(pieceBit << 6, 1, -2), new Entry(pieceBit >>> 17, -2, -1), new Entry(pieceBit >>> 15, -2, 1),
                new Entry(pieceBit >>> 10, -1, -2), new Entry(pieceBit >>> 6, -1, 2), };

        for (Entry i : knightBits) {
            if (inBounds(row + i.row, col + i.col) & ((i.bit & teamColor) == 0)) {
                ChessPosition end = new ChessPosition(row + i.row, col + i.col);
                ChessMove move = new ChessMove(position, end, null);
                allMoves.add(move);
            }
        }

        return allMoves;
    }

    private Collection<ChessMove> movePawn(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }

        long upOne = pieceBit << 8;
        long upTwo = (pieceBit << 8) | (pieceBit << 16);
        long takeRightW = pieceBit << 9;
        long takeLeftW = pieceBit << 7;

        long downOne = pieceBit >>> 8;
        long downTwo = (pieceBit >>> 8) | (pieceBit >>> 16);
        long takeRightB = pieceBit >>> 9;
        long takeLeftB = pieceBit >>> 7;

        if (color.compareTo(ChessGame.TeamColor.WHITE) == 0) {
            if (row < 8 & ((upOne & teamColor) == 0) & ((upOne & opColor) == 0)) {
                ChessPosition end = new ChessPosition(row + 1, col);
                if (end.getRow() == 8) {
                    promotionType(allMoves, position, end);
                } else {
                    ChessMove move = new ChessMove(position, end, null);
                    allMoves.add(move);
                }
            }
            if (row == 2 & ((upTwo & teamColor) == 0) & ((upTwo & opColor) == 0)) {
                ChessPosition end = new ChessPosition(row + 2, col);
                ChessMove move = new ChessMove(position, end, null);
                allMoves.add(move);
            }
            if ((row < 8) & (col < 8) & ((takeRightW & opColor) != 0)) {
                ChessPosition end = new ChessPosition(row + 1, col + 1);
                if (end.getRow() == 8) {
                    promotionType(allMoves, position, end);
                } else {
                    ChessMove move = new ChessMove(position, end, null);
                    allMoves.add(move);
                }
            }
            if ((row < 8) & (col > 1) & ((takeLeftW & opColor) != 0)) {
                ChessPosition end = new ChessPosition(row + 1, col - 1);
                if (end.getRow() == 8) {
                    promotionType(allMoves, position, end);
                } else {
                    ChessMove move = new ChessMove(position, end, null);
                    allMoves.add(move);
                }
            }
            } else {
                if (row > 1 & ((downOne & teamColor) == 0) & ((downOne & opColor) == 0)) {
                    ChessPosition end = new ChessPosition(row - 1, col);
                    if (end.getRow() == 1) {
                        promotionType(allMoves, position, end);
                    } else {
                        ChessMove move = new ChessMove(position, end, null);
                        allMoves.add(move);
                    }
                }
                if (row == 7 & ((downTwo & teamColor) == 0) & ((downTwo & opColor) == 0)) {
                    ChessPosition end = new ChessPosition(row - 2, col);
                    ChessMove move = new ChessMove(position, end, null);
                    allMoves.add(move);
                }
                if ((row > 1) & (col > 1) & ((takeRightB & opColor) != 0)) {
                    ChessPosition end = new ChessPosition(row - 1, col - 1);
                    if (end.getRow() == 1) {
                        promotionType(allMoves, position, end);
                    } else {
                        ChessMove move = new ChessMove(position, end, null);
                        allMoves.add(move);
                    }
                }
                if ((row > 1) & (col < 8) & ((takeLeftB & opColor) != 0)) {
                    ChessPosition end = new ChessPosition(row - 1, col + 1);
                    if (end.getRow() == 1) {
                        promotionType(allMoves, position, end);
                    } else {
                        ChessMove move = new ChessMove(position, end, null);
                        allMoves.add(move);
                    }
                }
            }


        return allMoves;
    }

    private Collection<ChessMove> moveBishop(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        int row = position.getRow();
        int col = position.getColumn();
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }
        movePositiveD(allMoves, teamColor, opColor, pieceBit, row, col, position);

        moveNegativeD(allMoves, teamColor, opColor, pieceBit, row, col, position);

        return allMoves;
    }


    private Collection<ChessMove> moveRook(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
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

        return allMoves;
    }

    private Collection<ChessMove> moveQueen(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
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

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
    private static final int[][] KNIGHT = {{2,-1},{2,1},{1,-2},{1,2},{-1,-2},{-1,2},{-2,-1},{-2,1}};
    private static final int[][] KING   = {{1,-1},{1,0},{1,1},{0,-1},{0,1},{-1,-1},{-1,0},{-1,1}};
    private static final int[][] ROOK_DIRS = {{1,0},{-1,0},{0,1},{0,-1}};
    private static final int[][] BISHOP_DIRS = {{1,1},{-1,-1},{1,-1},{-1,1}};

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
    public BitBoard(BitBoard other) {
        this.whiteKing = other.whiteKing;
        this.blackKing = other.blackKing;
        this.whiteQueens = other.whiteQueens;
        this.blackQueens = other.blackQueens;
        this.whiteRooks = other.whiteRooks;
        this.blackRooks = other.blackRooks;
        this.whiteBishops = other.whiteBishops;
        this.blackBishops = other.blackBishops;
        this.whiteKnights = other.whiteKnights;
        this.blackKnights = other.blackKnights;
        this.whitePawns = other.whitePawns;
        this.blackPawns = other.blackPawns;
    }

    private long getBoard(ChessGame.TeamColor color, ChessPiece.PieceType type) {
        boolean white = color == ChessGame.TeamColor.WHITE;
        return switch (type) {
            case PAWN -> white ? whitePawns : blackPawns;
            case KNIGHT -> white ? whiteKnights : blackKnights;
            case BISHOP -> white ? whiteBishops : blackBishops;
            case ROOK -> white ? whiteRooks : blackRooks;
            case QUEEN -> white ? whiteQueens : blackQueens;
            case KING -> white ? whiteKing : blackKing;
        };
    }

    private void setBoard(ChessGame.TeamColor color, ChessPiece.PieceType type, long value) {
        boolean white = color == ChessGame.TeamColor.WHITE;
        switch (type) {
            case PAWN -> { if (white) { whitePawns = value; } else { blackPawns = value; } }
            case KNIGHT -> { if (white) { whiteKnights = value; } else { blackKnights = value; } }
            case BISHOP -> { if (white) { whiteBishops = value; } else { blackBishops = value; } }
            case ROOK -> { if (white) { whiteRooks = value; } else { blackRooks = value; } }
            case QUEEN -> { if (white) { whiteQueens = value; } else { blackQueens = value; } }
            case KING -> { if (white) { whiteKing = value; } else { blackKing = value; } }
        }
    }

    public void removeBitPiece(ChessPosition position, ChessPiece piece) {
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();
        setBoard(color, type, getBoard(color, type) & ~toBit(position.getRow(), position.getColumn()));
    }

    public void addBitPiece(ChessPiece piece, long bitPiece) {
        if (piece == null) {
            return;
        }
        ChessGame.TeamColor color = piece.getTeamColor();
        ChessPiece.PieceType type = piece.getPieceType();
        setBoard(color, type, getBoard(color, type) | bitPiece);
    }

    public ChessPiece getBitPiece(ChessPosition position) {
        long bit = toBit(position.getRow(), position.getColumn());
        for (ChessGame.TeamColor color : ChessGame.TeamColor.values()) {
            for(ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                if ((getBoard(color, type) & bit) != 0) {
                    return new ChessPiece(color, type);
                }
            }
        }
        return null;
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
            allMoves = moveKnight(position, color);
        } else if (type.compareTo(ChessPiece.PieceType.KING) == 0) {
            allMoves = moveKing(position, color);
        }
        return allMoves;
    }

    private long whiteBoard() {
        return whitePawns ^ whiteRooks ^ whiteKnights ^ whiteBishops ^ whiteQueens ^ whiteKing;
    }

    private long blackBoard() {
        return blackPawns ^ blackRooks ^ blackKnights ^ blackBishops ^ blackQueens ^ blackKing;
    }
    long fileGen(int col) {
        long file = 0x0101010101010101L;
        file = file << col;
        return file;
    }

    long diagonalGen(boolean dir, int[] pos) {
        int up = pos[0] - pos[1];
        int down = pos[0] - (7 - pos[1]);
        if (dir) {
            long positive = 0x8040201008040201L;
            if (up < 0) {
                positive = positive >>> 8 * (Math.abs(up));
            } else {
                positive = positive << 8 * up;
            }
            return positive;
        } else {
            long negative = 0x0102040810204080L;
            if (down < 0) {
                negative = negative >>> 8 * (Math.abs(down));
            } else {
                negative = negative << 8 * down;
            }
            return negative;
        }
    }

    int[] posGen(long pieceBoard) {
        int index = Long.numberOfTrailingZeros(pieceBoard);
        int row = index / 8;
        int col = Integer.remainderUnsigned(index, 8);
        return new int[] {row, col};
    }


    boolean attacks(int[] piece, int[] king, int[][] offsets) {
        for (int[] i : offsets) {
            if (king[0] == piece[0] + i[0] && king[1] == piece[1] + i[1] ) {
                return true;
            }
        }
        return false;
    }

    boolean pawnAttacks(int[] piece, int[] king, ChessGame.TeamColor color) {
        int dir = (color == ChessGame.TeamColor.WHITE) ? 1 : -1;
        return (king[0] == piece[0] + dir && Math.abs(piece[1] - king[1]) == 1);
    }

    boolean linearDiagonalFill(ChessGame.TeamColor color, int[] other, boolean isBishopQueen) {
        int[] king;
        if (color == ChessGame.TeamColor.WHITE) {
            king = posGen(blackKing);
        } else {
            king = posGen(whiteKing);
        }
        int kingRow = king[0];
        int kingCol = king[1];
        int row = other[0];
        int col = other[1];
        int team = kingRow * 8 + kingCol;
        long fileMask = fileGen(col);
        int piece = row * 8 + col;
        int lo = Math.min(team, piece); int hi = Math.max(team, piece);
        long between = ((1L << hi) - (1L << lo)) - (1L << lo);

        if (isBishopQueen) {
            if (row - king[0] == col - king[1]) {
                between = between & diagonalGen(true, other);
            } else {
                between = between & diagonalGen(false, other);
            }
        } else {
            if (kingCol == col) {between = between & fileMask;}
        }
        return (between & (whiteBoard() | blackBoard())) == 0;
    }


    boolean crossFill(int[] king, ChessGame.TeamColor color) {
        for (int i = 0; i < 8; i++) {
            for (int y = 0; y < 8; y ++) {
                ChessPiece piece = getBitPiece(new ChessPosition(i + 1, y + 1));
                if (piece == null || piece.getTeamColor() != color) {continue;}
                ChessPiece.PieceType type = piece.getPieceType();
                boolean isRookOrQueen = type == ChessPiece.PieceType.QUEEN || type == ChessPiece.PieceType.ROOK;
                boolean onLine = king[0] == i || king[1] == y;
                boolean isBishopOrQueen = type == ChessPiece.PieceType.QUEEN || type == ChessPiece.PieceType.BISHOP;
                boolean isKnight = type == ChessPiece.PieceType.KNIGHT;
                boolean isPawn = type == ChessPiece.PieceType.PAWN;
                boolean isKing = type == ChessPiece.PieceType.KING;
                boolean onDiagonal = Math.abs(i - king[0]) == Math.abs(y - king[1]);
                if (isRookOrQueen && onLine) {
                    if (linearDiagonalFill(color, new int[] {i, y}, false)) {
                        return true;
                    }
                } else if (isBishopOrQueen && onDiagonal) {
                    if (linearDiagonalFill(color, new int[] {i, y}, true)) {
                        return true;
                    }
                } else if (isKnight) {
                    if (attacks(new int[] {i, y}, king, KNIGHT)) {
                        return true;
                    }
                } else if (isPawn) {
                    if (pawnAttacks(new int[]{i, y}, king, color)) {
                        return true;
                    }
                } else if (isKing) {
                    if (attacks(new int[] {i, y}, king, KING)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean inCheckWhite() {
        if (whiteKing == 0) {
            return false;
        }
        int[] kingPos = posGen(whiteKing);
        return crossFill(kingPos, ChessGame.TeamColor.BLACK);
    }

    public boolean inCheckBlack() {
        if (blackKing == 0) {
            return false;
        }
        int[] kingPos = posGen(blackKing);
        return crossFill(kingPos, ChessGame.TeamColor.WHITE);
    }

    public boolean isInCheck(ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            return inCheckWhite();
        }
        else {
            return inCheckBlack();
        }
    }

    private void promotionType(Collection<ChessMove> allMoves, ChessPosition start, ChessPosition end) {
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        allMoves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
    }

    private boolean inBounds(int row, int col) {
        if (row <= 8 && row >= 1 && col <= 8 && col >= 1) {
            return true;
        }
        return false;
    }

    private void steps(Collection<ChessMove> allMoves, ChessPosition start, int[][] offsets, long team) {
        for (int[] i : offsets) {
            int row = start.getRow() + i[0];
            int col = start.getColumn() + i[1];
            if (inBounds(row, col) & ((toBit(row, col) & team) == 0)) {
                allMoves.add(new ChessMove(start, new ChessPosition(row, col), null));
            }
        }
    }

    private Collection<ChessMove> moveKing(ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        long teamColor = whiteBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
        }
        steps(allMoves, position, KING, teamColor);
        return allMoves;
    }

    private Collection<ChessMove> moveKnight(ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        long teamColor = whiteBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
        }
        steps(allMoves, position, KNIGHT, teamColor);
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

    private void slide(Collection<ChessMove> allMoves, ChessPosition start, int[][] dirs, long team, long enemy) {
        for (int[] d : dirs) {
            int row = start.getRow() + d[0];
            int col = start.getColumn() + d[1];
            while (inBounds(row, col)) {
                long bit = toBit(row, col);
                if ((bit & team) != 0) {
                    break;
                }
                allMoves.add(new ChessMove(start, new ChessPosition(row, col), null));
                if ((bit & enemy) != 0) {
                    break;
                }
                row += d[0];
                col += d[1];
            }
        }
    }
    private long toBit(int row, int col) {
        return 1L << ((row - 1) * 8 + (col - 1));
    }

    private Collection<ChessMove> moveBishop(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }
        slide(allMoves, position, BISHOP_DIRS, teamColor, opColor);
        return allMoves;
    }


    private Collection<ChessMove> moveRook(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }
        slide(allMoves, position, ROOK_DIRS, teamColor, opColor);
        return allMoves;
    }

    private Collection<ChessMove> moveQueen(long pieceBit, ChessPosition position, ChessGame.TeamColor color) {
        Collection<ChessMove> allMoves = new ArrayList<>();
        long teamColor = whiteBoard();
        long opColor = blackBoard();

        if (color.compareTo(ChessGame.TeamColor.BLACK) == 0) {
            teamColor = blackBoard();
            opColor = whiteBoard();
        }
        slide(allMoves, position, ROOK_DIRS, teamColor, opColor);
        slide(allMoves, position, BISHOP_DIRS, teamColor, opColor);
        return allMoves;
        }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BitBoard bitBoard = (BitBoard) o;
        return whitePawns == bitBoard.whitePawns && whiteKing == bitBoard.whiteKing &&
                whiteQueens == bitBoard.whiteQueens && whiteRooks == bitBoard.whiteRooks &&
                whiteKnights == bitBoard.whiteKnights && whiteBishops == bitBoard.whiteBishops &&
                blackPawns == bitBoard.blackPawns && blackKing == bitBoard.blackKing &&
                blackQueens == bitBoard.blackQueens && blackRooks == bitBoard.blackRooks &&
                blackKnights == bitBoard.blackKnights && blackBishops == bitBoard.blackBishops;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(whitePawns);
        result = 31 * result + Long.hashCode(whiteKing);
        result = 31 * result + Long.hashCode(whiteQueens);
        result = 31 * result + Long.hashCode(whiteRooks);
        result = 31 * result + Long.hashCode(whiteKnights);
        result = 31 * result + Long.hashCode(whiteBishops);
        result = 31 * result + Long.hashCode(blackPawns);
        result = 31 * result + Long.hashCode(blackKing);
        result = 31 * result + Long.hashCode(blackQueens);
        result = 31 * result + Long.hashCode(blackRooks);
        result = 31 * result + Long.hashCode(blackKnights);
        result = 31 * result + Long.hashCode(blackBishops);
        return result;
    }
}

package chess;

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
}
package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    ChessPosition startPosition_;
    ChessPosition endPosition_;
    ChessPiece.PieceType promotionPiece_;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        startPosition_ = startPosition;
        endPosition_ = endPosition;
        promotionPiece_ = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition_;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition_;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece_;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChessMove move = (ChessMove) o;
        return Objects.equals(startPosition_, move.startPosition_) && Objects.equals(endPosition_, move.endPosition_) && promotionPiece_ == move.promotionPiece_;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(startPosition_);
        result = 31 * result + Objects.hashCode(endPosition_);
        result = 31 * result + Objects.hashCode(promotionPiece_);
        return result;
    }
}

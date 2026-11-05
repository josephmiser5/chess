package client;

import chess.*;
import ui.Repl;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var facade = new ServerFacade("http://localhost:8080");
        Repl repl = new Repl();
    }
}

package client;

import chess.*;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        Scanner scanner = new Scanner(System.in);
        System.out.println("♕ 240 Chess Client: " + piece);
        System.out.print("\u001b[H\u001b[2J");
        System.out.flush();
        System.out.println("Welcome to ChessIsDaBomb.com");

        while (true) {
            System.out.println("[LOGGED_OUT] >>> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine();

            if (line.equalsIgnoreCase("help")) {
                break;
            }

            var numbers = line.split(" ");
            int result = 0;
            try {
                for (var number : numbers) {
                    result += Integer.parseInt(number);
                }
                var equation = String.join(" + ", numbers);
                System.out.printf("%s = [31m%d[0m%n", equation, result);
            } catch (NumberFormatException e) {
                System.err.println("Error: Please provide only integers.");
            }
        }
    }
}

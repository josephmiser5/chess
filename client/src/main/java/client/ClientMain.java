package client;

import chess.*;
import ui.Repl;

import java.net.URISyntaxException;


public class ClientMain {
    public static void main(String[] args) throws ResponseException, URISyntaxException {
        System.out.println("♕ welcome to chessIsDaBomb.com ♕");
        System.out.println("♕ type - help - to get started ♕");
        var facade = new ServerFacade("http://localhost:8080");
        Repl repl = new Repl(facade);
        repl.run();
    }
}

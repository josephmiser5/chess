package client;

import chess.*;
import ui.Repl;

import java.net.URISyntaxException;


public class ClientMain {
    public static void main(String[] args) throws ResponseException, URISyntaxException {
        var facade = new ServerFacade("http://localhost:8080");
        Repl repl = new Repl(facade);
        repl.run();
    }
}

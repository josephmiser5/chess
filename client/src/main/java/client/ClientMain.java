package client;
import chess.*;
import ui.Repl;

import java.net.URISyntaxException;


public class ClientMain {
    public static void main(String[] args) throws ResponseException, URISyntaxException {
        String url = "http://localhost:8080";
        Repl repl = new Repl(url);
        repl.run();
    }
}

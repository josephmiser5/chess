package ui;
import client.ResponseException;
import client.ServerFacade;
import result.LoginResult;

import java.net.URISyntaxException;
import java.util.Scanner;

public class Repl {
    ServerFacade facade;

    public Repl(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() throws ResponseException, URISyntaxException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\u001b[H\u001b[2J");
        System.out.flush();
        while (true) {
            System.out.printf("[LOGGED_OUT]%n>>> ");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("help")) {
                help();
            } else if (line.startsWith("login")) {
                String[] authentication =  line.split(" ");
                if (authentication.length != 3) {
                    System.out.println("Please provide both username and password.");
                    continue;
                }
                login(authentication[1], authentication[2]);
            } else if (line.equalsIgnoreCase("exit")) {
                break;
            }
        }
    }

    public void help() {
        System.out.println("register - <USERNAME> <PASSWORD> <EMAIL> - to create account");
        System.out.println("login - <USERNAME> <PASSWORD> - to login");
        System.out.println("exit - to exit game");
        System.out.println("help - for information");
    }

    public void login(String username, String password) throws ResponseException, URISyntaxException {
        LoginResult res = facade.login(username, password);
        System.out.println(res.authToken());
    }


}

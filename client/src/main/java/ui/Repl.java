package ui;
import client.ServerFacade;
import java.util.Scanner;

public class Repl {
    ServerFacade facade;

    public Repl(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\u001b[H\u001b[2J");
        System.out.flush();
        while (true) {
            System.out.printf("[LOGGED_OUT]%n>>> ");
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("help")) {
                help();
            } else if (line.equalsIgnoreCase("login")) {

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

    public void login() {
        
    }


}

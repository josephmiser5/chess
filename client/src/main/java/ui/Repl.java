package ui;
import client.ResponseException;
import client.ServerFacade;
import result.LoginResult;
import result.RegisterResult;
import static ui.EscapeSequences.*;

import java.net.URISyntaxException;
import java.util.Scanner;

public class Repl {
    ServerFacade facade;
    State state = State.SIGNEDOUT;

    public Repl(ServerFacade facade) {
        this.facade = facade;
    }

    private enum State {
        SIGNEDOUT,
        SIGNEDIN,
        QUIT,
    }

    private void handlePreLogin(Scanner input) throws ResponseException, URISyntaxException {
        printPrompt("[LOGGED_OUT]");
        String line = input.nextLine();
        if (line.equalsIgnoreCase("help")) {
            help();
        } else if (line.startsWith("register")) {
            String[] details = line.split(" ");
            if (details.length != 4) {
                printError("Please provide both username, password, and email.");
                return;
            }
            register(details[1], details[2], details[3]);
        } else if (line.startsWith("login")) {
            String[] authentication =  line.split(" ");
            if (authentication.length != 3) {
                printError("Please provide both username and password.");
                return;
            }
            login(authentication[1], authentication[2]);
        } else if (line.equalsIgnoreCase("exit")) {
            state = State.QUIT;
        }
    }

    private void handlePostLogin(Scanner input) throws URISyntaxException {
        printPrompt("LOGGED_IN");
        String line = input.nextLine();
        if (line.equalsIgnoreCase("help")) {
            help();
        } else if (line.equalsIgnoreCase("logout")) {
            logout();
        }
    }


    public void run() throws ResponseException, URISyntaxException {
        System.out.print(ERASE_SCREEN);
        System.out.flush();
        System.out.println("Welcome to chessIsDaBomb.com! Type help to get started.");
        while (!state.equals(State.QUIT)) {
            Scanner input = new Scanner(System.in);
            if (state.equals(State.SIGNEDOUT)) {
                handlePreLogin(input);
            } else if (state.equals(State.SIGNEDIN)) {
                handlePostLogin(input);
            }
        }
    }

    public void help() {
        printCommand("register", "<USERNAME> <PASSWORD> <EMAIL> - to create account");
        printCommand("login", "<USERNAME> <PASSWORD> - to login");
        printCommand("exit", "to exit game");
        printCommand("help", "for information");
    }

    public void login(String username, String password) throws ResponseException, URISyntaxException {
        try {
            facade.login(username, password);
        } catch (ResponseException e) {
            printError("username or password is incorrect");
            return;
        }
        printSuccess("Your logged in, lets start playing.");
        state = State.SIGNEDIN;
    }

    public void register(String username, String password, String email) throws ResponseException, URISyntaxException {
        try {
            facade.register(username, password, email);
        } catch (ResponseException e) {
            if (e.getMessage().equalsIgnoreCase("Error: already taken")) {
                printError("username already taken");
            }
            return;
        }
        printSuccess("Welcome to chessIsDaBomb.com " + username + "!");
        state = State.SIGNEDIN;
    }

    public void logout() throws URISyntaxException {
        try {
            facade.logout();
        } catch (ResponseException e) {
            printError(e.getMessage());
            return;
        }
        printSuccess("Thanks for playing!");
        state = State.SIGNEDOUT;
    }

    private void printSuccess(String message) {
        System.out.println(SET_TEXT_COLOR_GREEN + message + RESET_TEXT_COLOR);
    }

    private void printError(String message) {
        System.out.println(SET_TEXT_COLOR_RED + message + RESET_TEXT_COLOR);
    }

    private void printPrompt(String prompt) {
        System.out.print(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + prompt
                + RESET_TEXT_BOLD_FAINT + RESET_TEXT_COLOR + " >>> ");
    }

    private void printCommand(String command, String description) {
        System.out.println(SET_TEXT_COLOR_YELLOW + command
        + SET_TEXT_COLOR_LIGHT_GREY + " - " + description + RESET_TEXT_COLOR);
    }
}

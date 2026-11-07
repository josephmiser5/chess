package ui;
import client.ResponseException;
import client.ServerFacade;
import result.LoginResult;
import result.RegisterResult;
import static ui.EscapeSequences.*;

import java.net.URISyntaxException;
import java.util.NoSuchElementException;
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
        printPrompt("[LOGGED_IN]");
        String line = input.nextLine();
        if (line.equalsIgnoreCase("help")) {
            help();
        } else if (line.equalsIgnoreCase("logout")) {
            logout();
        } else if (line.equalsIgnoreCase("quit")) {
            state = State.QUIT;
        }
    }


    public void run() {
        System.out.print(ERASE_SCREEN);
        System.out.flush();
        System.out.println("Welcome to chessIsDaBomb.com! Type help to get started.");
        Scanner input = new Scanner(System.in);
        while (!state.equals(State.QUIT)) {
            try {
                if (state.equals(State.SIGNEDOUT)) {
                    handlePreLogin(input);
                } else if (state.equals(State.SIGNEDIN)) {
                    handlePostLogin(input);
                }
            } catch (NoSuchElementException e) {
                state = State.QUIT;
            } catch (Exception e) {
                printError("Something went wrong. Please try again.");
            }
        }
    }

    public void help() {
        if (state == State.SIGNEDOUT) {
            printCommand("register", "<USERNAME> <PASSWORD> <EMAIL> - to create account");
            printCommand("login", "<USERNAME> <PASSWORD> - to login");
            printCommand("logout", "when you are done");
            printCommand("exit", "to exit game");
            printCommand("help", "for information");
        } else if (state == State.SIGNEDIN) {
            printCommand("create", "<NAME> - a game");
            printCommand("list", "games");
            printCommand("join", "<ID> [WHITE|BLACK] - a game");
            printCommand("observe", "<ID> - a game");
            printCommand("logout", "when you are done");
            printCommand("exit", "to exit game");
            printCommand("help", "for information");
        }
    }

    public void login(String username, String password) {
        try {
            facade.login(username, password);
        } catch (ResponseException e) {
            if (e.getStatus() == 401) {
                printError("username or password is incorrect");
            } else if (e.getStatus() == 400) {
                printError("Please provide both username and password");
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
            return;
        }
        printSuccess("Your logged in, lets start playing.");
        state = State.SIGNEDIN;
    }

    public void register(String username, String password, String email) {
        try {
            facade.register(username, password, email);
        } catch (ResponseException e) {
            if (e.getStatus() == 403) {
                printError("username already taken");
            } else if (e.getStatus() == 400) {
                printError("Please provide username, password, and email.");
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
            return;
        }
        printSuccess("Welcome to chessIsDaBomb.com " + username + "!");
        state = State.SIGNEDIN;
    }

    public void logout() {
        try {
            facade.logout();
        } catch (ResponseException e) {
            if (e.getStatus() == 401) {
                printError("Your session has expired. Please log in again.");
                state = State.SIGNEDOUT;
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
            return;
        }
        printSuccess("Thanks for playing!");
        state = State.SIGNEDOUT;
    }

    public void createGame(String gameName) {
        try {
            facade.createGame(gameName);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
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

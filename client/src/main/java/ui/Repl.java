package ui;
import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.GameData;
import result.LoginResult;
import result.RegisterResult;
import static ui.EscapeSequences.*;

import java.net.URISyntaxException;
import java.util.*;

public class Repl {
    private final ServerFacade facade;
    private State state = State.SIGNEDOUT;
    private HashMap<Integer, Integer> gameIDMap;

    public Repl(ServerFacade facade) {
        this.facade = facade;
        gameIDMap = new HashMap<>();
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
        } else if (line.equalsIgnoreCase("exit")) {
            state = State.QUIT;
        } else if (line.startsWith("create")) {
            String[] authentication = line.split(" ");
            if (authentication.length < 2) {
                printError("Please provide a game name.");
                return;
            }
            StringBuilder gameName = new StringBuilder();
            for (int i = 1; i < authentication.length; i++) {
                gameName.append(authentication[i]).append(" ");
            }
            createGame(gameName.toString());
        } else if (line.equalsIgnoreCase("list")) {
            listGames();
        } else if (line.startsWith("join")) {
            String[] gameDetails = line.split(" ");
            if (gameDetails.length < 3) {
                printError("Please provide an ID and a team color");
                return;
            }
            joinGame(Integer.parseInt(gameDetails[1]), gameDetails[2]);
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

    public void listGames() {
        try {
            ArrayList<GameData> games = facade.listGames();
            gameIDMap.clear();
            int number = 1;
            for (GameData game : games) {
                printSuccess("ID: " + number + ". Name: " + game.gameName() + ". " + "White: " +
                        game.whiteUsername() + ". Black: " + game.blackUsername() + ".");
                gameIDMap.put(number, game.gameID());
                number = number + 1;
            }
        } catch (ResponseException e) {
            if (e.getStatus() == 401) {
                printError("Your session has expired. Please log in again.");
            } else if (e.getStatus() == 400) {
                printError("Something went wrong. Please log in again.");
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }

        }
    }

    public void createGame(String gameName) {
        try {
            facade.createGame(gameName);
            printSuccess("Created game: " + gameName);
        } catch (ResponseException e) {
            if (e.getStatus() == 400) {
                printError("Please provide a game name.");
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
        }
    }

    public void joinGame(int id, String team) {
        ChessGame.TeamColor teamColor = null;
        int gameID = gameIDMap.get(id);
        if (team.equalsIgnoreCase("white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (team.equalsIgnoreCase("black")) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else {
            printError("Please enter a valid team color: [WHITE|BLACK].");
        }
        try {
            facade.joinGame(teamColor, gameID);
        } catch (ResponseException e) {
            System.out.println(e.getStatus());
            if (e.getStatus() == 403) {
                printError("Seat for " + team.toUpperCase() + " is already taken.");
            } else if (e.getStatus() == 401) {
                printError("Your session has expired. Please log in again.");
            } else if (e.getStatus() == 400) {
                printError("Please enter ID, and team color. If that does not work, the game may not exist.");
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
        }
        printSuccess("Game joined");
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

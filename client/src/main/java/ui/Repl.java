package ui;
import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import client.ServerMessageObserver;
import model.GameData;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import static ui.PrintStyles.*;

import static ui.EscapeSequences.*;

import java.util.*;

public class Repl implements ServerMessageObserver {
    private final ServerFacade facade;
    private State state = State.SIGNEDOUT;
    private HashMap<Integer, Integer> gameIDMap;
    private GameHandler gameHandler;
    public ChessGame game;
    public ChessGame.TeamColor teamColor;
    private int gameID;

    public Repl(String url, String port) {
        this.facade = new ServerFacade(url, port, this);
        this.gameHandler = new GameHandler(facade);
        gameIDMap = new HashMap<>();
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGame updatedGame = (LoadGame) message;
                game = updatedGame.getGame();
                System.out.println("\n");
                DrawBoard board = new DrawBoard(teamColor, game);
                board.drawBoard();
            }
            case NOTIFICATION -> {
                Notification notification = (Notification) message;
                printSuccess("\n" + notification.getMessage());
            }
            case ERROR -> {
                Error error = (Error) message;
                printError("\n" + error.getMessage());
            }
        }
        printPrompt("[IN_GAME]");
    }

    private enum State {
        SIGNEDOUT,
        SIGNEDIN,
        INGAME,
        QUIT,
    }

    private void handlePreLogin(Scanner input) {
        printPrompt("[LOGGED_OUT]");
        String[] tokens = input.nextLine().trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        switch (command) {
            case "help" -> help();
            case "register" -> {
                if (tokens.length != 4) {
                    printError("Please provide username, password, and email.");
                    return;
                }
                register(tokens[1], tokens[2], tokens[3]);
            }
            case "login" -> {
                if (tokens.length != 3) {
                    printError("Please provide both username and password.");
                    return;
                }
                login(tokens[1], tokens[2]);
            }
            case "exit", "quit" -> state = State.QUIT;
            default -> printError("Unknown command. Type 'help' to see commands.");
        }
    }

    private void handlePostLogin(Scanner input) {
        printPrompt("[LOGGED_IN]");
        String[] tokens = input.nextLine().trim().split("\\s+");
        String command = tokens[0].toLowerCase();
        switch (command) {
            case "help" -> help();
            case "logout" -> logout();
            case "exit", "quit" -> state = State.QUIT;
            case "create" -> {
                if (tokens.length < 2) {
                    printError("Please provide a game name.");
                    return;
                }
                String gameName = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
                createGame(gameName);
            }
            case "list" -> listGames();
            case "join" -> {
                if (tokens.length != 3) {
                    printError("Please provide a game number and a team color");
                    return;
                }
                int gameKey;
                try {
                    gameKey = Integer.parseInt(tokens[1]);
                } catch (NumberFormatException e) {
                    printError("Game number must be a number. Type 'list' to see games.");
                    return;
                }
                joinGame(gameKey, tokens[2]);
            }
            case "observe" -> {
                if (tokens.length != 2) {
                    printError("Please provide a game number.");
                    return;
                }
                int gameKey;
                try {
                    gameKey = Integer.parseInt(tokens[1]);
                } catch (NumberFormatException e) {
                    printError("Game number must be a number. Type 'list' to see games.");
                    return;
                }
                observeGame(gameKey);
            }
            default -> printError("Unknown command. Type 'help' to see commands.");
        }
    }

    private void handleInGame(Scanner input) {
        String[] tokens = input.nextLine().trim().split("\\s+");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help" -> {
                help();
                printPrompt("[IN_GAME]");
            }
            case "move" -> {
                gameHandler.move(gameID, tokens);
            }
            case "leave" -> {
                gameHandler.leave(gameID);
                gameID = -1;
                state = State.SIGNEDIN;
            }
            case "quit" -> {
                state = State.QUIT;
            }
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
                } else if (state.equals(State.INGAME)) {
                    handleInGame(input);
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
            printCommand("exit", "to exit game");
            printCommand("help", "for information");
        } else if (state == State.SIGNEDIN) {
            printCommand("create", "<NAME> - a game");
            printCommand("list", "games");
            printCommand("join", "<game number> [WHITE|BLACK] - a game");
            printCommand("observe", "<game number> - a game");
            printCommand("logout", "when you are done");
            printCommand("exit", "to exit game");
            printCommand("help", "for information");
        } else if (state == State.INGAME) {
            printCommand("help", "for information");
            printCommand("redraw", "to redraw chessboard");
            printCommand("leave", "to leave game");
            printCommand("move", "<piece> <square> - example: move N e4 moves knight to e4");
            printCommand("resign", "to resign");
            printCommand("legal moves", "<piece> - to highlight legal moves for a piece. Example: legal moves Q.");
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
        printSuccess("You're logged in, let's start playing.");
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
            if (games.isEmpty()) {
                printSuccess("No games yet. Type 'create' to make one.");
            }
            int number = 1;
            for (GameData game : games) {
                String white = game.whiteUsername() == null ? "open" : game.whiteUsername();
                String black = game.blackUsername() == null ? "open" : game.blackUsername();
                printSuccess(number + ". Name: " + game.gameName() + ". " + "White: " +
                        white + ". Black: " + black + ".");
                gameIDMap.put(number, game.gameID());
                number = number + 1;
            }
        } catch (ResponseException e) {
            if (e.getStatus() == 401) {
                printError("Your session has expired. Please log in again.");
                state = State.SIGNEDOUT;
            } else if (e.getStatus() == 400) {
                printError("Something went wrong. Please log in again.");
                state = State.SIGNEDOUT;
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
            } else if (e.getStatus() == 401) {
                printError("Your session has expired. Please log in again.");
                state = State.SIGNEDOUT;
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
        }
    }

    public void observeGame(int gameKey) {
        if (!gameIDMap.containsKey(gameKey)) {
            printError("Game does not exist. Type 'list' to see games.");
            return;
        }
        int gameID = gameIDMap.get(gameKey);
        try {
            this.teamColor = ChessGame.TeamColor.WHITE;
            facade.observeGame(gameID);
            state = State.INGAME;
            this.gameID = gameID;
        } catch (ResponseException e) {
            switch (e.getStatus()) {
                case 403 -> {
                    printError("Your session has expired. Please log in again.");
                    state = State.SIGNEDOUT;
                }
                case 400 -> printError("Please enter a game number. If that does not work game may not exist.");
            }
            if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
        }
    }

    public void joinGame(int gameKey, String team) {
        ChessGame.TeamColor teamColor;
        if (!gameIDMap.containsKey(gameKey)) {
            printError("Game does not exist. Type 'list' to see games.");
            return;
        }
        int gameID = gameIDMap.get(gameKey);
        if (team.equalsIgnoreCase("white")) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (team.equalsIgnoreCase("black")) {
            teamColor = ChessGame.TeamColor.BLACK;
        } else {
            printError("Please enter a valid team color: [WHITE|BLACK].");
            return;
        }
        try {
            this.teamColor = teamColor;
            facade.joinGame(teamColor, gameID);
            state = State.INGAME;
            this.gameID = gameID;
            printSuccess("Game joined");
        } catch (ResponseException e) {
            if (e.getStatus() == 403) {
                printError("Seat for " + team.toUpperCase() + " is already taken.");
            } else if (e.getStatus() == 401) {
                printError("Your session has expired. Please log in again.");
                state = State.SIGNEDOUT;
            } else if (e.getStatus() == 400) {
                printError("Please enter a game number and team color. If that does not work, the game may not exist.");
            } else if ("Error: could not reach server".equals(e.getMessage())) {
                printError("Could not reach server. Try again later.");
            } else {
                printError("Something went wrong. Please try again.");
            }
        }
    }
}

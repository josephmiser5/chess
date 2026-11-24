package client;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;
import request.*;
import result.*;
import websocket.commands.MakeMove;
import websocket.commands.UserGameCommand;

import java.util.ArrayList;

public class ServerFacade {
    private final WebClient http;
    private String authToken;
    private WebsocketCommunicator ws;
    String port;
    ServerMessageObserver client;

    public ServerFacade(String url, String port, ServerMessageObserver client) {
         http = new WebClient(url);
         this.port = port;
         this.client = client;
    }

    public LoginResult login(String username, String password) throws ResponseException {
        LoginResult res = http.post("/session", new LoginRequest(username, password), null, LoginResult.class);
        authToken = res.authToken();
        return res;
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException {
        RegisterResult res = http.post("/user", new RegisterRequest(username, password, email), null, RegisterResult.class);
        authToken = res.authToken();
        return res;
    }

    public void createGame(String gameName) throws ResponseException {
        http.post("/game", new CreateGameRequest(authToken, gameName), authToken, CreateGameResult.class);
    }

    public void logout() throws ResponseException {
        http.delete("/session", new LogoutRequest(authToken), authToken, LogoutResult.class);
        authToken = null;
    }

    public void clear() throws ResponseException {
        http.delete("/db", null, null, null);
        authToken = null;
    }

    public ArrayList<GameData> listGames() throws ResponseException{
        return (ArrayList<GameData>) http.get("/game", new ListGamesRequest(authToken), authToken, ListGamesResult.class).games();
    }

    public void joinGame(ChessGame.TeamColor team, int gameID) throws ResponseException {
        http.put("/game", new JoinGameRequest(authToken, team, gameID), authToken, JoinGameResult.class);
        try {
            connectUser(gameID);
        } catch (Exception e) {
            throw new ResponseException(500,e.getMessage());
        }
    }

    public void observeGame(int gameID) throws ResponseException {
        try {
            connectUser(gameID);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void connectUser(int gameID) throws Exception {
        ws = new WebsocketCommunicator(port, client);
        ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
    }

    public void move(int gameID, ChessMove move) throws Exception {
        ws.send(new MakeMove(authToken, gameID, move));
    }

    public void leave(int gameID) throws Exception {
        ws.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }
}

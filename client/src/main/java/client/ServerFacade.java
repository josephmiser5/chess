package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import request.*;
import result.*;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class ServerFacade {
    private final webClient http;
    private String authToken;

    public ServerFacade(String url) {
         http = new webClient(url);
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
    }

    public boolean isLoggedIn() {
        return authToken != null;
    }
}

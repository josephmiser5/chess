package client;

import model.AuthData;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import java.net.URISyntaxException;

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

    public CreateGameResult createGame(String gameName) throws ResponseException {
        return http.post("/game", new CreateGameRequest(authToken, gameName), authToken, CreateGameResult.class);
    }

    public void logout() throws ResponseException {
        http.delete("/session", new LogoutRequest(authToken), authToken, LogoutResult.class);
        authToken = null;
    }

    public void clear() throws ResponseException {
        http.delete("/db", null, null, null);
        authToken = null;
    }

    public void create() throws ResponseException{}

    public boolean isLoggedIn() {
        return authToken != null;
    }
}

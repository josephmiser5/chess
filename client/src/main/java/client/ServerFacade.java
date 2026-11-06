package client;

import model.AuthData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
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

    public LoginResult login(String username, String password) throws ResponseException, URISyntaxException {
        LoginResult res = http.post("/session", new LoginRequest(username, password), null, LoginResult.class);
        authToken = res.authToken();
        return res;
    }

    public RegisterResult register(String username, String password, String email) throws ResponseException, URISyntaxException {
        RegisterResult res = http.post("/user", new RegisterRequest(username, password, email), null, RegisterResult.class);
        authToken = res.authToken();
        return res;
    }

    public void logout() throws ResponseException, URISyntaxException {
        http.post("/session", new LogoutRequest(authToken), authToken, LogoutResult.class);
        authToken = null;
    }

    public boolean isLoggedIn() {
        return authToken != null;
    }
}

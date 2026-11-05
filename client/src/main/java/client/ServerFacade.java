package client;

import model.AuthData;
import request.LoginRequest;
import result.LoginResult;

import java.net.URISyntaxException;

public class ServerFacade {
    private final webClient http;

    public ServerFacade(String url) {
         http = new webClient(url);
    }
    public LoginResult login(String username, String password) throws ResponseException, URISyntaxException {
        return http.post("/session", new LoginRequest(username, password), null, LoginResult.class);
    }
}

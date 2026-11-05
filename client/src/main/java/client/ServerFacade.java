package client;

import model.AuthData;
import request.LoginRequest;

public class ServerFacade {
    private final webClient http;

    public ServerFacade(String url) {
         http = new webClient(url);
    }

    public AuthData login(String username, String password) {
        return http.post("/session", new LoginRequest(username, password), null, AuthData.class);
    }
}

package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import request.CreateGameRequest;
import result.CreateGameResult;

public class GameService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(UserDAO userDAO_, AuthDAO authDAO_, GameDAO gameDAO_) {
        userDAO = userDAO_;
        authDAO = authDAO_;
        gameDAO = gameDAO_;
    }

    public CreateGameResult createGame(CreateGameRequest req) {

        return null;
    }
}

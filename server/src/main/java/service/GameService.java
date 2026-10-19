package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import request.CreateGameRequest;
import result.CreateGameResult;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

public class GameService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(UserDAO userDAO_, AuthDAO authDAO_, GameDAO gameDAO_) {
        userDAO = userDAO_;
        authDAO = authDAO_;
        gameDAO = gameDAO_;
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        if (req.authToken() == null || req.gameName() == null) throw new BadRequestException("Error: bad request");
        AuthData authData = authDAO.getAuth(req.authToken());
        if (authData == null) throw new UnauthorizedException("Error: unauthorized");
        int gameID = gameDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    }
}

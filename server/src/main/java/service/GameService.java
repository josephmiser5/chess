package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import java.util.ArrayList;

public class GameService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GameService(UserDAO userDAO_, AuthDAO authDAO_, GameDAO gameDAO_) {
        userDAO = userDAO_;
        authDAO = authDAO_;
        gameDAO = gameDAO_;
    }

    public ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {
        if (req.authToken() == null) throw new BadRequestException();
        AuthData authData = authDAO.getAuth(req.authToken());
        if (authData == null) throw new UnauthorizedException();
        ArrayList<GameData> games = (ArrayList<GameData>) gameDAO.getAllGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        if (req.authToken() == null || req.gameName() == null) throw new BadRequestException();
        AuthData authData = authDAO.getAuth(req.authToken());
        if (authData == null) throw new UnauthorizedException();
        int gameID = gameDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    }
}

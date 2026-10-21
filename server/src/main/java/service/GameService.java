package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;
import service.exception.AlreadyTakenException;
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

    private AuthData getThrowAuth(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        if (authData == null) throw new UnauthorizedException();
        return authData;
    }

    public ListGamesResult listGames(ListGamesRequest req) throws DataAccessException {
        if (req.authToken() == null) throw new BadRequestException();
        getThrowAuth(req.authToken());
        ArrayList<GameData> games = (ArrayList<GameData>) gameDAO.getAllGames();
        return new ListGamesResult(games);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        if (req.authToken() == null || req.gameName() == null) throw new BadRequestException();
        getThrowAuth(req.authToken());
        int gameID = gameDAO.createGame(req.gameName());
        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest req) throws DataAccessException {
        if (req.authToken() == null || req.gameID() == null || req.playerColor() == null) throw new BadRequestException();
        int gameID = req.gameID();
        AuthData authData = getThrowAuth(req.authToken());
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData == null) throw new BadRequestException();

        GameData newGameData;

        if (req.playerColor().equals(ChessGame.TeamColor.WHITE) && gameData.whiteUsername() == null) {
            newGameData = new GameData(gameID, authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        } else if (req.playerColor().equals(ChessGame.TeamColor.WHITE)) {
            throw new AlreadyTakenException();
        } else if (req.playerColor().equals(ChessGame.TeamColor.BLACK) && gameData.blackUsername() == null) {
            newGameData = new GameData(gameID, gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(newGameData);
        } else if (req.playerColor().equals(ChessGame.TeamColor.BLACK)) {
            throw new AlreadyTakenException();
        } else throw new BadRequestException();
    }
}

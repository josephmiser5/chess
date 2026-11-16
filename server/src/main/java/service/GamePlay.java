package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import model.UserSession;
import result.LoadGame;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.concurrent.ConcurrentHashMap;

public class GamePlay {
    AuthDAO authDAO;
    GameDAO gameDAO;
    ConcurrentHashMap<Integer, ConcurrentHashMap<String, UserSession>> connections = new ConcurrentHashMap<>();

    public GamePlay(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public LoadGame connect(UserGameCommand command, WsContext ctx) throws DataAccessException {
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            throw new BadRequestException();
        }
        connections.computeIfAbsent(command.getGameID(), id ->
            new ConcurrentHashMap<>()).put(ctx.sessionId(), new UserSession(authData.username(), ctx));
        return new LoadGame(gameData.game());
    }

}

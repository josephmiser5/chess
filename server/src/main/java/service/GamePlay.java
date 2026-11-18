package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import model.UserSession;
import websocket.messages.LoadGame;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;
import websocket.commands.UserGameCommand;

import java.util.concurrent.ConcurrentHashMap;

public class GamePlay {
    Gson gson = new Gson();
    AuthDAO authDAO;
    GameDAO gameDAO;
    ConcurrentHashMap<Integer, ConcurrentHashMap<String, UserSession>> connections = new ConcurrentHashMap<>();

    public GamePlay(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

   public void removeConnection(int gameID, WsContext ctx) {
        connections.get(gameID).remove(ctx.sessionId());
   }

    public void connect(UserGameCommand command, WsContext ctx) throws DataAccessException {
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
        ConcurrentHashMap<String, UserSession> sessions = connections.get(command.getGameID());
        String message = gson.toJson(new LoadGame(gameData.game()));
        sessions.values().forEach((value) -> {
            value.ctx().send(message);
        });
    }

    public void move(UserGameCommand command, WsContext ctx) throws Exception {
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            throw new BadRequestException();
        }
        switch (gameData.game().getTeamTurn()) {
            case WHITE -> {
                if (!gameData.whiteUsername().equals(authData.username())) {
                    if (gameData.blackUsername().equals(authData.username())) {
                        throw new Exception("It's " + gameData.whiteUsername() + "'s turn.");
                    }
                    throw new Exception("You are not a player.");
                    }
                }
            }
        }
    }

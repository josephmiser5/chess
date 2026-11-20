package service;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import model.UserSession;
import websocket.commands.MakeMove;
import websocket.messages.LoadGame;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;

import java.util.ArrayList;
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

    public void connect(UserGameCommand command, WsContext ctx) throws Exception {
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
        notify(sessions, gameData, ctx);
        String message = gson.toJson(new LoadGame(gameData.game()));
        ctx.send(message);
    }

    public void notify(ConcurrentHashMap<String, UserSession> sessions, GameData gameData, WsContext ctx) {
        String username = sessions.get(ctx.sessionId()).username();
        Notification notification;
        if (username.equals(gameData.whiteUsername())) {
            notification = new Notification(username + " is playing as white.");
        } else if (username.equals(gameData.blackUsername())) {
            notification = new Notification(username + " is playing as black.");
        } else {
            notification = new Notification(username + " is observing.");
        }
        sessions.values().forEach(session -> {
            if (!session.ctx().session.isOpen()) {
                sessions.remove(session.ctx().sessionId());
                return;
            }
            session.ctx().send(gson.toJson(notification));
        });
    }

    public void move(MakeMove move, WsContext ctx) throws Exception {
        AuthData authData = authDAO.getAuth(move.getAuthToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        GameData gameData = gameDAO.getGame(move.getGameID());
        if (gameData == null) {
            throw new BadRequestException();
        }
        if (!authData.username().equals(gameData.whiteUsername()) && !authData.username().equals(gameData.blackUsername())) {
            throw new Exception("You are not a player.");
        }
        if (gameData.whiteUsername() == null  || gameData.blackUsername() == null) {
            throw new Exception("Waiting for more players to join.");
        }
        if (gameData.game().gameOver) {
            throw new Exception("Game is over. ");
        }
        switch (gameData.game().getTeamTurn()) {
            case WHITE -> {
                if (!gameData.whiteUsername().equals(authData.username())) {
                    throw new Exception("It's " + gameData.whiteUsername() + "'s turn.");
                }
                }
            case BLACK -> {
                if (!gameData.blackUsername().equals(authData.username())) {
                    throw new Exception("It's " + gameData.blackUsername() + "'s turn.");
                }
            }
            }
            gameData.game().makeMove(move.getMove());
            gameDAO.updateGame(gameData);
            String updatedGame = gson.toJson(new LoadGame(gameDAO.getGame(move.getGameID()).game()));
            ctx.send(updatedGame);
        }
    }

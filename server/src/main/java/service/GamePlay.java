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
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMove;
import websocket.messages.LoadGame;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

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

    public void onClose(WsContext ctx) {
        connections.values().forEach(game -> {
            game.remove(ctx.sessionId());
        });
    }

    public void leave(UserGameCommand command, WsContext ctx) throws Exception {
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            throw new BadRequestException();
        }
        ConcurrentHashMap<String, UserSession> sessions = connections.get(command.getGameID());

        if (authData.username().equals(gameData.whiteUsername())) {
            gameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameData);
        }
        if (authData.username().equals(gameData.blackUsername())) {
            gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            gameDAO.updateGame(gameData);
        }


        if (sessions == null) {return;}
        sessions.remove(ctx.sessionId());

        sendToOthers(sessions, new Notification(authData.username() + " left the game."), ctx);
    }


    public void resign(UserGameCommand command) throws Exception {
        AuthData authData = authDAO.getAuth(command.getAuthToken());
        if (authData == null) {
            throw new UnauthorizedException();
        }
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData == null) {
            throw new BadRequestException();
        }
        if (gameData.game().gameOver) {
            throw new Exception("Game is already over.");
        }
        ConcurrentHashMap<String, UserSession> sessions = connections.get(command.getGameID());
        if (sessions == null) {return;}

        if (authData.username().equals(gameData.whiteUsername())) {
            gameData.game().gameOver = true;
            gameDAO.updateGame(gameData);
            sendToAll(sessions, new Notification(authData.username() + " resigned." + " Black wins!"));
        } else if (authData.username().equals(gameData.blackUsername())) {
            gameData.game().gameOver = true;
            gameDAO.updateGame(gameData);
            sendToAll(sessions, new Notification(authData.username() + " resigned." + " White wins!"));
        } else {
            throw new Exception("You are not a player.");
        }
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

        String username = sessions.get(ctx.sessionId()).username();
        Notification notification;
        if (username.equals(gameData.whiteUsername())) {
            notification = new Notification(username + " is playing as white.");
        } else if (username.equals(gameData.blackUsername())) {
            notification = new Notification(username + " is playing as black.");
        } else {
            notification = new Notification(username + " is observing.");
        }

        sendToOthers(sessions, notification, ctx);
        String message = gson.toJson(new LoadGame(gameData.game()));
        ctx.send(message);
    }

    private void sendToOthers(ConcurrentHashMap<String, UserSession> sessions, ServerMessage message, WsContext ctx) {
        String notification = gson.toJson(message);
        sessions.values().forEach(session -> {
            if (!session.ctx().session.isOpen()) {
                sessions.remove(session.ctx().sessionId());
                return;
            }
            if (!session.ctx().sessionId().equals(ctx.sessionId())) {
                session.ctx().send(notification);
            }
        });
    }

    private void sendToAll(ConcurrentHashMap<String, UserSession> sessions, ServerMessage message) {
        String notification = gson.toJson(message);
        sessions.values().forEach(session -> {
            if (!session.ctx().session.isOpen()) {
                sessions.remove(session.ctx().sessionId());
                return;
            }
            session.ctx().send(notification);
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

        ConcurrentHashMap<String, UserSession> sessions = connections.get(move.getGameID());

        ChessGame.TeamColor teamColor = gameData.game().getTeamTurn();
        String white = gameData.whiteUsername();
        String black = gameData.blackUsername();

        Notification inMortalDanger = null;

        if (gameData.game().isInCheckmate(teamColor)) {
            if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
                inMortalDanger = new Notification(white + " is in checkmate. Game Over.");
            } else {
                inMortalDanger = new Notification(black + " is in checkmate. Game Over.");
            }
            gameData.game().gameOver = true;
        } else if (gameData.game().isInStalemate(teamColor)) {
            inMortalDanger =  new Notification("Stalemate! Game Over.");
            gameData.game().gameOver = true;
        } else if (gameData.game().isInCheck(teamColor)) {
            if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
                inMortalDanger = new Notification(white + " is in check.");
            } else {
                inMortalDanger = new Notification(black + " is in check.");
            }
        }

        gameDAO.updateGame(gameData);

        if (sessions == null) {throw new Exception("No one is connected to game");}
        Notification notification = getNotification(move, authData);

        sendToAll(sessions, new LoadGame(gameData.game()));
        sendToOthers(sessions, notification, ctx);

        if (inMortalDanger != null) {
            sendToAll(sessions, inMortalDanger);
        }
    }

    @NotNull
    private static Notification getNotification(MakeMove move, AuthData authData) throws Exception {
        String username = authData.username();
        char startCol = (char) (move.getMove().getStartPosition().getColumn() + 'a' - 1);
        char endCol = (char) (move.getMove().getEndPosition().getColumn() + 'a' - 1);
        int startRow = move.getMove().getStartPosition().getRow();
        int endRow = move.getMove().getEndPosition().getRow();
        return new Notification(username + " made move: " + startCol + startRow + " " + endCol + endRow);
    }
}

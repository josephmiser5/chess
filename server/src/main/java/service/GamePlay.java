package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import websocket.messages.ServerMessage;

public class GamePlay {
    AuthDAO authDAO;
    GameDAO gameDAO;

    public GamePlay(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void connect(ServerMessage message) {
        message.
    }

}

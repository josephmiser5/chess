package ui;

import client.ServerFacade;

public class GameHandler {

    ServerFacade facade;

    public GameHandler(ServerFacade facade) {
        this.facade = facade;
    }

    public void move(int gameID) {
        try {
            facade.move(gameID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

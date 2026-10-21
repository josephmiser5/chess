package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    public ClearService(UserDAO userDAO_, AuthDAO authDAO_, GameDAO gameDAO_) {
        userDAO = userDAO_;
        authDAO = authDAO_;
        gameDAO = gameDAO_;
    }
    public void clear() {}
}

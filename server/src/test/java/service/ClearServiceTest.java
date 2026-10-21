package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.*;
import result.RegisterResult;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;
    private UserService userService;
    private ClearService clearService;


    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    @DisplayName("Check db is clear")
    public void dbIsClear() throws DataAccessException {
        RegisterRequest req = new RegisterRequest("joe doe", "123", "joedoe@email.com");
        RegisterResult user = userService.register(req);
        int gameID = gameService.createGame(new CreateGameRequest(user.authToken(), "super fun")).gameID();
        clearService.clear();
        AuthData authData = authDAO.getAuth(user.authToken());
        UserData userData = userDAO.getUser(user.username());
        GameData gameData = gameDAO.getGame(gameID);
        assertNull(authData);
        assertNull(userData);
        assertNull(gameData);
    }
}

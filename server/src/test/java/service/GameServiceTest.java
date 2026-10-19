package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.RegisterResult;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    private final String[] usernames = {"johnsmith", "janedoe"};
    private final String[] passwords = {"password", "12345"};
    private final String[] emails = {"johnsmith@email.com", "janedoe@email.com"};
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;
    private UserService userService;


    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("createGame succeeds")
    public void createGamePositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest(usernames[1], passwords[1], emails[1]);
        RegisterResult res = userService.register(req);

        CreateGameResult createRes = gameService.createGame(new CreateGameRequest(res.authToken(), "fun game"));
        assertNotNull(gameDAO.getGame(createRes.gameID()));
    }
}

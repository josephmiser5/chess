package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
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
    private final String[] gameNames = {"fun game"};
    private final String[] authTokens = {"asldkfjlkf23jl"};
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


    public RegisterResult registerUser() throws DataAccessException {
        RegisterRequest req = new RegisterRequest(usernames[1], passwords[1], emails[1]);
        return userService.register(req);
    }

    @Test
    @DisplayName("createGame succeeds")
    public void createGamePositive() throws DataAccessException {
        RegisterResult res = registerUser();
        CreateGameResult createRes = gameService.createGame(new CreateGameRequest(res.authToken(), gameNames[0]));
        GameData gameData = gameDAO.getGame(createRes.gameID());
        assertNotNull(gameData);
        assertEquals(gameNames[0], gameData.gameName());
    }

    @Test
    @DisplayName("createGame fails")
    public void createGameNegative() throws DataAccessException {
        registerUser();
        assertThrowsExactly(BadRequestException.class, () -> {
            gameService.createGame(new CreateGameRequest(null, gameNames[0]));
        });
        assertThrowsExactly(UnauthorizedException.class, () -> {
            gameService.createGame(new CreateGameRequest(authTokens[0], gameNames[0]));
        });
    }
}

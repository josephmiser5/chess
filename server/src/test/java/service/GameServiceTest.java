package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.RegisterResult;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    private final String[] usernames = {"john smith", "Jane doe"};
    private final String[] passwords = {"password", "12345"};
    private final String[] emails = {"johnsmith@email.com", "janedoe@email.com"};
    private final String[] gameNames = {"fun game"};
    private final String[] authTokens = {"Asldkfjf23jl"};
    private GameDAO gameDAO;
    private GameService gameService;
    private UserService userService;


    @BeforeEach
    public void setUp() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
    }


    private RegisterResult registerUser() throws DataAccessException {
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

    @Test
    @DisplayName("joinGame succeeds")
    public void joinGamePositive() throws DataAccessException {
        RegisterResult user = registerUser();
        CreateGameResult createResult = gameService.createGame(new CreateGameRequest(user.authToken(), "super fun"));
        gameService.joinGame(new JoinGameRequest(user.authToken(), ChessGame.TeamColor.WHITE, createResult.gameID()));
        GameData gameData = gameDAO.getGame(createResult.gameID());
        assertNotNull(gameData);
        assertEquals(user.username(), gameData.whiteUsername());
    }

    @Test
    @DisplayName("joinGame fails")
    public void joinGameNegative() throws DataAccessException {
        RegisterResult user = registerUser();
        CreateGameResult createResult = gameService.createGame(new CreateGameRequest(user.authToken(), "super fun"));
        JoinGameRequest joinRequest = new JoinGameRequest(user.authToken(), ChessGame.TeamColor.BLACK, createResult.gameID());
        assertThrowsExactly(UnauthorizedException.class, () -> {
            gameService.joinGame(new JoinGameRequest(authTokens[0], ChessGame.TeamColor.BLACK, createResult.gameID()));
        });
        gameService.joinGame(joinRequest);
        assertThrowsExactly(AlreadyTakenException.class, () -> {
            gameService.joinGame(joinRequest);
        });
    }

    @Test
    @DisplayName("listGames succeeds")
    public void listGamesPositive() throws DataAccessException {
        RegisterResult user = registerUser();
        CreateGameResult createResult = gameService.createGame(new CreateGameRequest(user.authToken(), "super fun"));
        JoinGameRequest joinRequest = new JoinGameRequest(user.authToken(), ChessGame.TeamColor.BLACK, createResult.gameID());
        gameService.joinGame(joinRequest);
        ListGamesResult games = gameService.listGames(new ListGamesRequest(user.authToken()));
        GameData testGame = games.games().getFirst();
        assertNotNull(games);
        assertEquals("super fun", testGame.gameName());
        assertEquals(createResult.gameID(), testGame.gameID());
        assertEquals(user.username(), testGame.blackUsername());
        assertNull(testGame.whiteUsername());
    }

    @Test
    @DisplayName("listGames fails")
    public void listGamesNegative() throws DataAccessException {
        assertThrowsExactly(UnauthorizedException.class, () -> {
            gameService.listGames(new ListGamesRequest(authTokens[0]));
        });
    }

}

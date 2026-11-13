package client;

import chess.ChessGame;
import org.junit.jupiter.api.*;
import result.LoginResult;
import result.RegisterResult;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static ServerFacade facade;
    private static Server server;
    private static final String USERNAME = "joseph";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static RegisterResult res;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setUp() throws ResponseException {
        facade.clear();
        res = facade.register(USERNAME, PASSWORD, EMAIL);
    }

    @Test
    @DisplayName("login succeeds")
    public void loginPositive() throws ResponseException {
        LoginResult res = facade.login(USERNAME, PASSWORD);
        assertEquals(USERNAME, res.username());
        assertNotNull(res.authToken());
    }

    @Test
    @DisplayName("login fails")
    public void loginNegative() throws ResponseException {
        ResponseException wrongPassword = assertThrows(ResponseException.class, () -> {
            facade.login(USERNAME, "incorrect_password");
        });
        assertEquals(401, wrongPassword.getStatus());
        ResponseException noPassword = assertThrows(ResponseException.class, () -> {
            facade.login(USERNAME, null);
        });
        assertEquals(400, noPassword.getStatus());
    }

    @Test
    @DisplayName("register succeeds")
    public void registerPositive() throws ResponseException {
        assertEquals(USERNAME, res.username());
        assertNotNull(res.authToken());
    }

    @Test
    @DisplayName("register fails")
    public void registerNegative() throws ResponseException {
        ResponseException alreadyExists = assertThrows(ResponseException.class, () -> {
            facade.register(USERNAME, PASSWORD, EMAIL);
        });
        assertEquals(403, alreadyExists.getStatus());
    }

    @Test
    @DisplayName("createGame succeeds")
    public void createGamePositive() throws ResponseException {
        String gameName = "fun game";
        facade.createGame(gameName);
        assertEquals(gameName, facade.listGames().getFirst().gameName());
    }

    @Test
    @DisplayName("createGame fails")
    public void createGameNegative() {
        assertThrows(ResponseException.class, () -> {
            facade.createGame(null);
        });
    }

    @Test
    @DisplayName("joinGame succeeds")
    public void joinGamePositive() throws ResponseException {
        String gameName = "fun game";
        facade.createGame(gameName);
        facade.joinGame(ChessGame.TeamColor.WHITE, 1);
        assertEquals(USERNAME, facade.listGames().getFirst().whiteUsername());
    }

    @Test
    @DisplayName("joinGame fails")
    public void joinGameNegative() throws ResponseException {
        String gameName = "fun game";
        facade.createGame(gameName);
        facade.joinGame(ChessGame.TeamColor.WHITE, 1);
        assertThrows(ResponseException.class, () -> {
            facade.joinGame(ChessGame.TeamColor.WHITE, -1);
        });
        assertThrows(ResponseException.class, () -> {
            facade.joinGame(ChessGame.TeamColor.WHITE, 1);
        });
    }

    @Test
    @DisplayName("logout")
    public void logoutPositive() throws ResponseException {
        facade.logout();
        assertThrows(ResponseException.class, () -> {
            facade.listGames();
        });
    }

    @Test
    @DisplayName("clear")
    public void clear() throws ResponseException {
        facade.clear();
        assertThrows(ResponseException.class, () -> {
            facade.login(USERNAME, PASSWORD);
        });
    }
}

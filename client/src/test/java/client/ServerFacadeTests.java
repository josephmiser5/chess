package client;

import org.junit.jupiter.api.*;
import result.LoginResult;
import result.RegisterResult;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static ServerFacade facade;
    private static Server server;
    private static String username = "josephmiser";
    private static String password = "password";
    private static String email = "email";
    private static RegisterResult res;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setUp() throws ResponseException {
        facade.clear();
        res = facade.register(username, password, email);
    }

    @Test
    @DisplayName("login succeeds")
    public void loginPositive() throws ResponseException {
        LoginResult res = facade.login(username, password);
        assertEquals(username, res.username());
        assertNotNull(res.authToken());
    }

    @Test
    @DisplayName("login fails")
    public void loginNegative() throws ResponseException {
        ResponseException wrongPassword = assertThrows(ResponseException.class, () -> {
            facade.login(username, "incorrect_password");
        });
        assertEquals(401, wrongPassword.getStatus());
        ResponseException noPassword = assertThrows(ResponseException.class, () -> {
            facade.login(username, null);
        });
        assertEquals(400, noPassword.getStatus());
    }

    @Test
    @DisplayName("register succeeds")
    public void registerPositive() throws ResponseException {
        assertEquals(username, res.username());
        assertNotNull(res.authToken());
    }

    @Test
    @DisplayName("register fails")
    public void registerNegative() throws ResponseException {
        ResponseException alreadyExists = assertThrows(ResponseException.class, () -> {
            facade.register(username, password, email);
        });
        assertEquals(403, alreadyExists.getStatus());
    }

    @Test
    @DisplayName("createGame succeeds")
    public void createGamePositive() {
        
    }

}

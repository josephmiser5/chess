package client;

import org.junit.jupiter.api.*;
import result.LoginResult;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    private static ServerFacade facade;
    private static Server server;
    private static String username = "josephmiser";
    private static String password = "password";

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
        try {
            facade.login(username, "incorrect_password");
        } catch (ResponseException e) {
            assertEquals(401, e.getStatus());
        }
        try {
            facade.login(username, null);
        } catch (ResponseException e) {
            assertEquals(400, e.getStatus());
        }
    }

    @Test
    @DisplayName("register succeeds")
    public void registerPositive() {

    }


}

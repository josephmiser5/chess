package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.exception.AlreadyTakenException;
import service.exception.BadRequestException;
import service.exception.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private final String[] usernames = {"johnsmith", "janedoe"};
    private final String[] passwords = {"password", "12345"};
    private final String[] emails = {"johnsmith@email.com", "janedoe@email.com"};
    private final String[] authTokens = {"adslkfj_dksjf_sidfoju", "slakfj_fanvx455"};
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;


    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Register succeeds")
    public void registerTestPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest(usernames[0], passwords[0], emails[0]);
        RegisterResult res = userService.register(req);
        UserData userData = userDAO.getUser(usernames[0]);
        AuthData authData = authDAO.getAuth(res.authToken());

        RegisterRequest req1 = new RegisterRequest(usernames[1], passwords[1], emails[1]);
        RegisterResult res1 = userService.register(req1);

        assertNotNull(res);
        assertNotNull(res.authToken());
        assertNotNull(authData);
        assertEquals(usernames[0], authData.username());
        assertEquals(usernames[0], res.username());
        assertNotNull(userData);
        assertEquals(usernames[0], userData.username());
        assertEquals(passwords[0], userData.password());
        assertEquals(emails[0], userData.email());

        assertNotEquals(res1.authToken(), res.authToken());
    }

    @Test
    @DisplayName("Register fails with missing password")
    public void registerNullField() throws DataAccessException {

        RegisterRequest req = new RegisterRequest(usernames[0], null, emails[0]);
        assertThrowsExactly(BadRequestException.class, () -> {
            userService.register(req);
        });
        assertNull(userDAO.getUser(usernames[0]));
    }

    @Test
    @DisplayName("Register fails when username is taken")
    public void registerDuplicateUsername() throws DataAccessException {
        userService.register(new RegisterRequest(usernames[0], passwords[0], emails[0]));

        RegisterRequest duplicate = new RegisterRequest(usernames[0], passwords[1], emails[1]);
        assertThrowsExactly(AlreadyTakenException.class, () -> {
            userService.register(duplicate);
        });

        UserData stored = userDAO.getUser(usernames[0]);
        assertNotNull(stored);
        assertEquals(passwords[0], stored.password());
        assertEquals(emails[0], stored.email());
    }

    @Test
    @DisplayName("DAOs handle registration")
    public void dataAccess() throws DataAccessException {

        RegisterRequest req = new RegisterRequest(usernames[0], passwords[0], emails[0]);
        RegisterResult res = userService.register(req);

        assertThrowsExactly(DataAccessException.class, () -> {
            userDAO.createUser(usernames[0], passwords[0], emails[0]);
        });

        assertThrowsExactly(DataAccessException.class, () -> {
            authDAO.createAuth(res.authToken(), usernames[0]);
        });
    }


    @Test
    @DisplayName("Login succeeds")
    public void loginPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest(usernames[1], passwords[1], emails[1]);
        RegisterResult res = userService.register(req);

        LoginRequest loginReq = new LoginRequest(usernames[1], passwords[1]);
        LoginResult loginRes = userService.login(loginReq);
        AuthData authData = authDAO.getAuth(loginRes.authToken());

        assertNotNull(loginRes);
        assertEquals(loginRes.authToken(), authData.authToken());
        assertEquals(usernames[1], authData.username());

    }

    @Test
    @DisplayName("Login fails")
    public void loginNegative() throws DataAccessException {

        assertThrowsExactly(BadRequestException.class, () -> {
            userService.login(new LoginRequest(null, passwords[0]));
        });

        RegisterRequest req = new RegisterRequest(usernames[1], passwords[1], emails[1]);
        userService.register(req);

        assertThrowsExactly(UnauthorizedException.class, () -> {
            userService.login(new LoginRequest(usernames[1], passwords[0]));
        });
    }

    @Test
    @DisplayName("Logout succeeds")
    public void logoutPositive() throws DataAccessException {
        RegisterRequest req = new RegisterRequest(usernames[1], passwords[1], emails[1]);
        RegisterResult res = userService.register(req);

        assertNotNull(authDAO.getAuth(res.authToken()));
        LogoutRequest logoutRequest = new LogoutRequest(res.authToken());
        userService.logout(logoutRequest);

        assertNull(authDAO.getAuth(res.authToken()));
    }

    @Test
    @DisplayName("Logout fails")
    public void logoutNegative() {
       assertThrowsExactly(UnauthorizedException.class, () -> {
           userService.logout(new LogoutRequest(authTokens[0]));
       });
    }
}

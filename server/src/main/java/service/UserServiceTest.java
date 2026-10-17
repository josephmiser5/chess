package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private final String[] usernames = {"johnsmith", "janedoe"};
    private final String[] passwords = {"password", "12345"};
    private final String[] emails = {"johnsmith@email.com", "janedoe@email.com"};
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
}

package dataaccess.sql;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseAuthDAO;
import model.AuthData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseAuthDAOTest {
    String authToken;
    String username = "Joe";
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new DatabaseAuthDAO();
        authToken = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10);
    }


    @AfterEach
    public void tearDown() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    @DisplayName("createAuth succeeds")
    public void createAuthPositive() throws DataAccessException {
        authDAO.createAuth(authToken, username);
        AuthData auth = authDAO.getAuth(authToken);
        assertEquals(authToken, auth.authToken());
        assertEquals(username, auth.username());
    }

    @Test
    @DisplayName("createAuth fails")
    public void createAuthNegative() throws DataAccessException {
        authDAO.createAuth(authToken, username);
        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(authToken, username);
        });
    }

    @Test
    @DisplayName("deleteAuth succeeds")
    public void deleteAuthPositive() throws DataAccessException {
        authDAO.createAuth(authToken, username);
        AuthData auth = authDAO.getAuth(authToken);
        assertNotNull(auth);
        authDAO.deleteAuth(authToken);
        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    @DisplayName("deleteAuth fails")
    public void deleteAuthNegative() throws DataAccessException {
        authDAO.createAuth(authToken, username);
        AuthData auth = authDAO.getAuth(authToken);
        assertNotNull(auth);
        authDAO.deleteAuth("alkdjflskdjfslkghal892384");
        assertNotNull(authDAO.getAuth(authToken));
    }


    @Test
    @DisplayName("clear")
    public void clear() throws DataAccessException {
        authDAO.createAuth(authToken, username);
        assertNotNull(authDAO.getAuth(authToken));
        authDAO.clear();
        assertNull(authDAO.getAuth(authToken));
    }
}

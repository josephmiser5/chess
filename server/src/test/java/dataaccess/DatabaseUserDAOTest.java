package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;


public class DatabaseUserDAOTest {
    String username;
    String password = "password";
    String email = "user@email.com";
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new DatabaseUserDAO();
        username = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10);
    }

    @Test
    @DisplayName("createUser succeeds")
    public void createUserPositive() throws DataAccessException {
        userDAO.createUser(username, password, email);
        UserData user = userDAO.getUser(username);
        assertEquals(username, user.username());
        assertTrue(BCrypt.checkpw(password, user.password()));
        assertEquals(email, user.email());
    }

    @Test
    @DisplayName("createUser fails")
    public void createUserNegative() throws DataAccessException {
        userDAO.createUser(username, password, email);
        assertThrowsExactly(DataAccessException.class, () -> {
            userDAO.createUser(username, password, email);
        });
    }


    @Test
    @DisplayName("clear succeeds")
    public void clearPositive() {
        
    }
}

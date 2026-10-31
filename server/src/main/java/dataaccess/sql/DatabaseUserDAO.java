package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DatabaseUserDAO implements UserDAO {

    public DatabaseUserDAO() throws DataAccessException {
        DatabaseManager.createDatabase();
        configureDatabase();
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String hash = passwordHash(password);
        var sql = """
                INSERT INTO user (username, password, email) VALUES(?, ?, ?);
                """;
        try (var con = DatabaseManager.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, email);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("unable to create user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var sql = """
                SELECT *
                FROM user
                WHERE username = ?
                """;
        try (var con = DatabaseManager.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new
                            UserData(rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to read user", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = """
                TRUNCATE TABLE user;
                """;
        try (var con = DatabaseManager.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("unable to clear database", e);
        }
    }

    private void configureDatabase() throws DataAccessException {
        var sql = """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

        try (var con = DatabaseManager.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("unable to configure database", e);
        }
    }

    private String passwordHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}

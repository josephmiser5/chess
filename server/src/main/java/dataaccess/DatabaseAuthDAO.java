package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class DatabaseAuthDAO implements AuthDAO{

   public DatabaseAuthDAO() throws DataAccessException {
       DatabaseManager.getConnection();
       configureDatabase();
   }

    @Override
    public void createAuth(String authToken, String username) throws DataAccessException {
       var sql = """
               INSERT INTO auth (authToken, username) VALUES(?, ?);
               """;
       try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)) {
           ps.setString(1, authToken);
           ps.setString(2, username);
           ps.executeUpdate();
       } catch (SQLException e) {
           throw new DataAccessException("unable to add auth", e);
       }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
       var sql = """
                SELECT * FROM auth WHERE authToken = ?;
                """;
       try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)) {
           ps.setString(1, authToken);
           try (var rs = ps.executeQuery()) {
               if (rs.next()) {
                   return new
                           AuthData(rs.getString("authToken"),
                           rs.getString("username"));
               }
               return null;
           }
       } catch (SQLException e) {
           throw new DataAccessException("unable to read auth", e);
       }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
       var sql = """
               DELETE FROM auth WHERE authToken = ?;
               """;
       try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)) {
           ps.setString(1, authToken);
           ps.executeUpdate();
       } catch (SQLException e) {
           throw new DataAccessException("unable to delete auth", e);
       }
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = """
                TRUNCATE TABLE auth;
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
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )""";

        try (var con = DatabaseManager.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("unable to configure database", e);
        }
    }
}

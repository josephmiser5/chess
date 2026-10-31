package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseGameDAO implements GameDAO {

    public DatabaseGameDAO() throws DataAccessException {
        DatabaseManager.getConnection();
        configureDatabase();
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        Gson gson = new Gson();
        ArrayList<GameData> allGames = new ArrayList<>();
        var sql = """
                SELECT * FROM game;
                """;
        try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)) {
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                    allGames.add(
                            new GameData(rs.getInt("gameID"),
                                    rs.getString("whiteUsername"),
                                    rs.getString("blackUsername"),
                                    rs.getString("gameName"),
                                    game)
                    );
                }
                return allGames;
            }
        } catch (SQLException e) {
            throw new DataAccessException("couldn't read all games", e);
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        Gson gson = new Gson();
        String gameJson = gson.toJson(new ChessGame());
        var sql = """
                INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?);
                """;
        try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, gameName);
            ps.setString(4, gameJson);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new DataAccessException("unable to get gameID");
        } catch (SQLException e) {
            throw new DataAccessException("unable to add game",e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        Gson gson = new Gson();
        var sql = """
                SELECT * FROM game WHERE gameID = ?;
                """;
        try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)){
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
                    return new GameData(rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            game);
                }
            } catch (SQLException e) {
                throw new DataAccessException("couldn't read game", e);
            }
        } catch (SQLException e) {
            throw new DataAccessException("failed to get game", e);
        }
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        Gson gson = new Gson();
        String game = gson.toJson(gameData.game(), ChessGame.class);
        var sql = """
                UPDATE game
                SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
                WHERE gameID = ?;
                """;
        try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)) {
            ps.setString(1, gameData.whiteUsername());
            ps.setString(2, gameData.blackUsername());
            ps.setString(3, gameData.gameName());
            ps.setString(4, game);
            ps.setInt(5, gameData.gameID());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("unable to get game");
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to update game", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        var sql = """
                TRUNCATE TABLE game;
                """;
        try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("failed to clear game", e);
        }
    }


    private void configureDatabase() throws DataAccessException {
        var sql = """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game TEXT NOT NULL
            )""";
        try (var con = DatabaseManager.getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("unable to configure database", e);
        }
    }
}

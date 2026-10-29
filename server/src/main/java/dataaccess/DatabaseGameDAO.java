package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.SQLException;
import java.util.List;

public class DatabaseGameDAO implements GameDAO{

    public DatabaseGameDAO() throws DataAccessException {
        DatabaseManager.getConnection();
        configureDatabase();
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        Gson gson = new Gson();
        String gameJson = gson.toJson(new ChessGame());
        var sql = """
                INSERT INTO game (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?);
                """;
        try (var con = DatabaseManager.getConnection();
            var ps = con.prepareStatement(sql)) {
            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, gameName);
            ps.setString(4, gameJson);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("unable to add game",e);
        }
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
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

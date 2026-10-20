package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.List;

public interface GameDAO {
    List<GameData> getAllGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData gameData) throws DataAccessException;
    void clear() throws DataAccessException;

}

package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    @Override
    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = nextID++;
        if (games.containsKey(gameID)) {
            throw new DataAccessException("Failed to create game");
        }
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
        return gameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }
        throw new DataAccessException("Failed to get game");
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if (!games.containsKey(gameData.gameID())) {
            throw new DataAccessException("Failed to get game");
        }
        games.put(gameData.gameID(), gameData);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
        nextID = 1;
        if (!games.isEmpty()) {
            throw new DataAccessException("Failed to clear games");
        }
    }
}

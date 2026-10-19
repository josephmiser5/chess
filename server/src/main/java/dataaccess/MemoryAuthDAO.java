package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> auth = new HashMap<>();

    @Override
    public void createAuth(String authToken, String username) throws DataAccessException {
        if (auth.containsKey(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        AuthData authData = new AuthData(authToken, username);
        auth.put(authToken, authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return auth.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (!auth.containsKey(authToken)) {
            throw new DataAccessException("unauthorized");
        }
        auth.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        auth.clear();
        if (!auth.isEmpty()) {
            throw new DataAccessException("Failed to clear auth");
        }
    }
}

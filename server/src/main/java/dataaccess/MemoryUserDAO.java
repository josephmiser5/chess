package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private final HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        if (users.containsKey(username)) {
            throw new DataAccessException("User already exists");
        }
        UserData user = new UserData(username, password, email);
        users.put(username, user);
    }

    @Override
    public UserData getUser(String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        if (!users.isEmpty()) {
            throw new DataAccessException("Failed to clear users");
        }
    }
}

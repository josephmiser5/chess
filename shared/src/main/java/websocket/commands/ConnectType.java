package websocket.commands;

public class ConnectType extends UserGameCommand {

    private final UserType type;

    public ConnectType(UserType type, String authToken, Integer gameID) {
        this.type = type;
        super(CommandType.CONNECT, authToken, gameID);
    }

    public enum UserType {
        PLAYER,
        OBSERVER,
    }

    public UserType getType() {
        return type;
    }
}

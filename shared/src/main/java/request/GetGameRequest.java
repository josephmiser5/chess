package request;

public record GetGameRequest(String authToken, Integer gameID) {
}

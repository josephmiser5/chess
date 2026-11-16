package model;

import io.javalin.websocket.WsContext;

public record UserSession(String username, WsContext ctx) {
}

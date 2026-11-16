package client;
import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;
import java.util.Scanner;

public class WebsocketCommunicator extends Endpoint {

    public Session session;
        Gson gson = new Gson();
    public WebsocketCommunicator(String port, ServerMessageObserver client) throws Exception {
        URI uri = new URI("ws://" + port + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    public void send(UserGameCommand command) throws Exception {
        this.session.getBasicRemote().sendText(gson.toJson(command));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                gson.fromJson(message, ServerMessage.class);
                System.out.println(message);
            }
        });
    }
}

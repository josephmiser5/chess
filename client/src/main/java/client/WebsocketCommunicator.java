package client;
import jakarta.websocket.*;
import java.net.URI;
import java.util.Scanner;

public class WebsocketCommunicator extends Endpoint {

    public static void main(String[] args) throws Exception {
        var ws = new WebsocketCommunicator();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Echo message");
        while (true) {
            ws.send(scanner.nextLine());
        }
    }

    public Session session;
    public WebsocketCommunicator() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                System.out.println(message);
            }
        });
    }
}

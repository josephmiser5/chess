package client;
import com.google.gson.Gson;
import model.AuthData;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class webClient {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private final String baseUrl;

    public webClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> T post(String path, Object body, String authToken, Class<T> responseClass) {
        return send("POST", path, body, authToken, responseClass);
    }

    private <T> T send(String method, String path, Object body, String authToken, Class<T> responseClass) throws URISyntaxException {
        String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", "localhost", 8080, path);
        try {
            var bodyPublisher = (body == null)
                    ?HttpRequest.BodyPublishers.noBody()
                    :HttpRequest.BodyPublishers.ofString(gson.toJson(body));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .method(method, bodyPublisher)
                    .build();

            if (body != n)

            HttpResponse httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void get(String host, int port, String path) throws URISyntaxException, IOException, InterruptedException {
        String urlString = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
            System.out.println(httpResponse.body());
        } else {
            System.out.println("Error: received status code " + httpResponse.statusCode());
        }
    }
}

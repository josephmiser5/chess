package client;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class webClient {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private final String baseUrl;

    public webClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T> T post(String path, Object body, String authToken, Class<T> responseClass) throws ResponseException {
        return send("POST", path, body, authToken, responseClass);
    }


    public <T> void delete(String path, Object body, String authToken, Class<T> responseClass) throws ResponseException {
        send("DELETE", path, body, authToken, responseClass);
    }

    private <T> T send(String method, String path, Object body, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            var bodyPublisher = (body == null)
                    ?HttpRequest.BodyPublishers.noBody()
                    :HttpRequest.BodyPublishers.ofString(gson.toJson(body));

            var httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + path))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .method(method, bodyPublisher);

            if (body != null) {
                httpRequest.header("Content-Type", "application/json");
            }
            if (authToken != null) {
                httpRequest.header("authorization", authToken);
            }

            HttpResponse<String> httpResponse = httpClient.send(httpRequest.build(), HttpResponse.BodyHandlers.ofString());

            int status = httpResponse.statusCode();
            if (status < 200 || status >= 300) {
                throw new ResponseException(status, message(httpResponse.body()));
            }

            if (responseClass == null || httpResponse.body().isEmpty()) {
                return null;
            }
            return gson.fromJson(httpResponse.body(), responseClass);

        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new ResponseException(500, "Error: could not reach server");
        }
    }
    private String message(String body) {
        try {
            var map = gson.fromJson(body, java.util.Map.class);
            return (String) map.get("message");
        } catch (Exception e) {
            return body;
        }
    }
}

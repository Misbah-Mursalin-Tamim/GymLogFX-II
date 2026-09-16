package service;

import model.FitnessTip;
import org.json.JSONArray;
import org.json.JSONObject;
import util.AppExecutor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class FitnessTipService {

    private static final String API_URL = "https://zenquotes.io/api/random";

    public void fetchTip(Consumer<FitnessTip> onSuccess, Consumer<Exception> onError) {

        AppExecutor.runInBackground(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());

                FitnessTip tip = parseTip(response.body());

                onSuccess.accept(tip);

            } catch (Exception exception) {
                onError.accept(exception);
            }
        });
    }

    private FitnessTip parseTip(String jsonBody) {
        // The API returns a JSON array like:
        // [ { "q": "Some quote text", "a": "Author Name" } ]
        JSONArray array = new JSONArray(jsonBody);
        JSONObject firstItem = array.getJSONObject(0);

        String quote = firstItem.getString("q");
        String author = firstItem.getString("a");

        String fullText = quote + " — " + author;

        return new FitnessTip(fullText);
    }
}
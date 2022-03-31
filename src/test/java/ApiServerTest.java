import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.*;

public class ApiServerTest {
    @Test
    public void testServer() throws InterruptedException, IOException {
        URI url = URI.create("http://127.0.0.1?from=USD&to=JPY");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        JSONObject result = new JSONObject(response.body());
        assertEquals("USD", result.get("from"));
        assertEquals("JPY", result.get("to"));
        assertNotNull(result.get("price"));
    }
}
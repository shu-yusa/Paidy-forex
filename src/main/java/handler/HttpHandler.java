package handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpHandler {
    public void handle(HttpExchange exchange) {
        System.out.println(exchange.getRequestMethod() + " / " + exchange.getProtocol());
        exchange.getRequestHeaders().forEach((k,v) -> System.out.println(k + ": "+v));
        System.out.println();

        String responseText = "<html><body>hello2</body></html>\n";
        respond(exchange, responseText);
    }

    public void respond(HttpExchange exchange, String responseText){
        byte[] responseBody = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        try {
            exchange.sendResponseHeaders(200, responseBody.length);  // 明示的に返す必要あり
            exchange.getResponseBody().write(responseBody);

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}

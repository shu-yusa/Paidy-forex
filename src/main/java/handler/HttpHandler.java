package handler;

import com.sun.net.httpserver.HttpExchange;
import domain.Currency;
import domain.CurrencyPair;
import domain.ExchangeRate;
import domain.ExchangeRateService;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

public class HttpHandler {
    ExchangeRateService exchangeRateService;

    public HttpHandler(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (!Objects.equals(exchange.getRequestMethod(), "GET") || !Objects.equals(path, "/")) {
            String responseText = "<html><body>Not Found</body></html>";
            byte[] responseBody = responseText.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(404, responseBody.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBody);
            os.close();
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> queryMap = queryToMap(query);
        JSONObject errors = this.validateQueryParameters(queryMap);

        if (!errors.isEmpty()) {
            String responseText = (new JSONObject()).put("errors", errors).toString();
            byte[] responseBody = responseText.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, responseBody.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBody);
            os.close();
            return;
        }

        ExchangeRate exchangeRate = this.exchangeRateService.getExchangeRate(new CurrencyPair(
            Currency.valueOf(queryMap.get("from")),
            Currency.valueOf(queryMap.get("to"))));

        String responseText = new JSONObject()
                .put("from", exchangeRate.currencyPair().fromCurrency())
                .put("to", exchangeRate.currencyPair().toCurrency())
                .put("price", exchangeRate.price())
                .toString();
        byte[] responseBody = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        try {
            exchange.sendResponseHeaders(200, responseBody.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBody);
            os.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private JSONObject validateQueryParameters(Map<String, String> queryMap) {
        String fromCurrency = queryMap.get("from");
        String toCurrency = queryMap.get("to");
        JSONObject errors = new JSONObject();
        if (fromCurrency == null) {
            errors.put("from", "Required parameter");
        } else {
            try {
                Currency.valueOf(fromCurrency);
            } catch (IllegalArgumentException e) {
                errors.put("from", "Unsupported currency code");
            }
        }
        if (toCurrency == null) {
            errors.put("to", "Required parameter");
        } else {
            try {
                Currency.valueOf(toCurrency);
            } catch (IllegalArgumentException e) {
                errors.put("to", "Unsupported currency code");
            }
        }
        return errors;
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null) {
            return params;
        }
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                params.put(entry[0], entry[1]);
            }
        }
        return params;
    }
}

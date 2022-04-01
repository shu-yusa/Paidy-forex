package handler;

import com.sun.net.httpserver.HttpExchange;
import domain.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;


public class HttpHandler {
    ExchangeRateService exchangeRateService;
    static final String CONTENT_TYPE_JSON = "application/json";
    static final String CONTENT_TYPE_HTML = "text/html";

    public HttpHandler(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (!Objects.equals(exchange.getRequestMethod(), "GET") || !Objects.equals(path, "/")) {
            String responseText = "<html><body>Not Found</body></html>";
            this.returnResponse(exchange, 404, CONTENT_TYPE_HTML, responseText);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        Map<String, String> queryMap = queryToMap(query);
        JSONObject errors = this.validateQueryParameters(queryMap);

        if (!errors.isEmpty()) {
            JSONObject responseObject = new JSONObject();
            responseObject.put("message", "Parameters are invalid").put("errors", errors);
            String responseText = responseObject.toString();
            this.returnResponse(exchange, 400, CONTENT_TYPE_JSON, responseText);
            return;
        }

        ExchangeRate exchangeRate;
        try {
            exchangeRate = this.exchangeRateService.getExchangeRate(new CurrencyPair(
                    Currency.valueOf(queryMap.get("from")),
                    Currency.valueOf(queryMap.get("to"))));
        } catch (ExchangeRateApiUnavailableException e) {
            String responseText = (new JSONObject()).put("message", "Service is temporarily unavailable").toString();
            this.returnResponse(exchange, 503, CONTENT_TYPE_JSON, responseText);
            return;
        } catch (Exception e) {
            return;
        }

        String responseText = new JSONObject()
                .put("from", exchangeRate.currencyPair().fromCurrency())
                .put("to", exchangeRate.currencyPair().toCurrency())
                .put("rate", exchangeRate.price())
                .toString();
        this.returnResponse(exchange, 200, CONTENT_TYPE_JSON, responseText);
    }

    private void returnResponse(HttpExchange exchange, int statusCode, String contentType, String responseText) throws IOException {
        byte[] responseBody = responseText.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, responseBody.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBody);
        os.close();
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

        if (toCurrency != null && toCurrency.equals(fromCurrency)) {
            errors.put("to", "Different currencies should be specified");
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

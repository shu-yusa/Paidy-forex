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
import java.util.ResourceBundle;


public class HttpHandler {
    private final ExchangeRateService exchangeRateService;
    private final ResourceBundle bundle;
    static final String CONTENT_TYPE_JSON = "application/json";
    static final String CONTENT_TYPE_HTML = "text/html";

    public HttpHandler(ExchangeRateService exchangeRateService, ResourceBundle bundle) {
        this.exchangeRateService = exchangeRateService;
        this.bundle = bundle;
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
            responseObject.put("message", this.bundle.getString("http_error_invalid_parameter")).put("errors", errors);
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
            String responseText = (new JSONObject()).put(
                    "message", this.bundle.getString("http_error_service_unavailable")).toString();
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
            errors.put("from", this.bundle.getString("http_error_required"));
        } else {
            try {
                Currency.valueOf(fromCurrency);
            } catch (IllegalArgumentException e) {
                errors.put("from", this.bundle.getString("http_error_unsupported_currency_code"));
            }
        }
        if (toCurrency == null) {
            errors.put("to", this.bundle.getString("http_error_required"));
        } else {
            try {
                Currency.valueOf(toCurrency);
            } catch (IllegalArgumentException e) {
                errors.put("to", this.bundle.getString("http_error_unsupported_currency_code"));
            }
        }

        if (toCurrency != null && toCurrency.equals(fromCurrency)) {
            errors.put("to", this.bundle.getString("http_error_same_currency_codes"));
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

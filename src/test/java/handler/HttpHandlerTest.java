package handler;

import adapter.InMemoryExchangeRateCache;
import com.sun.net.httpserver.HttpExchange;
import domain.*;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;


public class HttpHandlerTest {
    private ExchangeRateService create_exchange_rate_service(
            String fromCurrency, String toCurrency, double bid, double ask, double price, String timeStamp) throws ParseException {
        CurrencyPair pair = new CurrencyPair(Currency.valueOf(fromCurrency), Currency.valueOf(toCurrency));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = dateFormat.parse(timeStamp);
        return new ExchangeRateService(
                currencyPairs -> new ExchangeRate(pair, bid, ask, price, time),
                new InMemoryExchangeRateCache(),
                100);
    }

    @Test
    public void testServer() throws ParseException, IOException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        double price = 0.71;
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.61, 0.82, price,
                "2019-01-01T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?from=%s&to=%s", fromCurrency, toCurrency);
        HttpExchange exchange = new HttpExchangeStub("GET", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(200, exchange.getResponseCode());
        assertEquals("application/json", exchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("USD", response.get("from"));
        assertEquals("JPY", response.get("to"));
        BigDecimal rate = (BigDecimal) response.get("rate");
        assertEquals(price, rate.doubleValue(), 0.0001);
    }

    @Test
    public void test404IsReturnedForUnsupportedUrl() throws ParseException, IOException {
        ExchangeRateService service = this.create_exchange_rate_service(
                "JPY", "USD", 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        HttpExchange exchange = new HttpExchangeStub("GET", "/foo");

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        assertEquals(404, exchange.getResponseCode());
        assertEquals("text/html", exchange.getResponseHeaders().get("Content-Type").get(0));
    }

    @Test
    public void test404IsReturnedForUnsupportedHttpMethod() throws ParseException, IOException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?from=%s&to=%s", fromCurrency, toCurrency);
        HttpExchange exchange = new HttpExchangeStub("POST", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        assertEquals(404, exchange.getResponseCode());
    }

    @Test
    public void testMissingFromParameter() throws IOException, ParseException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?to=%s", toCurrency);
        HttpExchange exchange = new HttpExchangeStub("GET", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(400, exchange.getResponseCode());
        assertEquals("application/json", exchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("Parameters are invalid", response.get("message"));
        assertEquals("Required parameter", response.getJSONObject("errors").get("from"));
    }

    @Test
    public void testMissingToParameter() throws IOException, ParseException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?from=%s", fromCurrency);
        HttpExchange exchange = new HttpExchangeStub("GET", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(400, exchange.getResponseCode());
        assertEquals("application/json", exchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("Parameters are invalid", response.get("message"));
        assertEquals("Required parameter", response.getJSONObject("errors").get("to"));
    }

    @Test
    public void testMissingAllParameter() throws IOException, ParseException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        HttpExchange exchange = new HttpExchangeStub("GET", "/");

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(400, exchange.getResponseCode());
        assertEquals("application/json", exchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("Parameters are invalid", response.get("message"));
        assertEquals("Required parameter", response.getJSONObject("errors").get("from"));
        assertEquals("Required parameter", response.getJSONObject("errors").get("to"));
    }

    @Test
    public void testMissingInvalidFromCurrencyCodeParameter() throws IOException, ParseException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?from=%s&to=%s", "TWD", toCurrency);
        HttpExchange exchange = new HttpExchangeStub("GET", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(400, exchange.getResponseCode());
        assertEquals("application/json", exchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("Parameters are invalid", response.get("message"));
        assertEquals("Unsupported currency code", response.getJSONObject("errors").get("from"));
    }

    @Test
    public void testMissingInvalidToCurrencyCodeParameter() throws IOException, ParseException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?from=%s&to=%s", fromCurrency, "TWD");
        HttpExchange exchange = new HttpExchangeStub("GET", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(400, exchange.getResponseCode());
        assertEquals("application/json", exchange.getResponseHeaders().get("Content-Type").get(0));
        assertEquals("Parameters are invalid", response.get("message"));
        assertEquals("Unsupported currency code", response.getJSONObject("errors").get("to"));
    }

    @Test
    public void testSuperfluousParameterIsIgnored() throws IOException, ParseException {
        String fromCurrency = "USD";
        String toCurrency = "JPY";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, toCurrency, 0.41, 0.32, 0.51,
                "2019-01-02T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?from=%s&to=%s&foo=bar", fromCurrency, toCurrency);
        HttpExchange exchange = new HttpExchangeStub("GET", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        assertEquals(200, exchange.getResponseCode());
    }

    @Test
    public void test503IsReturnedWhenExternalExchangeRateServiceIsNotAvailable() throws IOException {
        ExchangeRateService service = new ExchangeRateService(currencyPairs -> {
            throw new ExchangeRateApiUnavailableException();
        }, new InMemoryExchangeRateCache(), 100);

        HttpHandler handler = new HttpHandler(service);
        HttpExchange exchange = new HttpExchangeStub("GET", "/?from=JPY&to=USD");

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(503, exchange.getResponseCode());
        assertEquals("Service is temporarily unavailable", response.get("message"));
    }

    @Test
    public void test400IsReturnedForSameCurrencies() throws IOException, ParseException {
        String fromCurrency = "USD";
        ExchangeRateService service = this.create_exchange_rate_service(
                fromCurrency, fromCurrency, 0.61, 0.82, 0.71,
                "2019-01-01T00:00:00.000");

        HttpHandler handler = new HttpHandler(service);
        String uri = String.format("/?from=%s&to=%s", fromCurrency, fromCurrency);
        HttpExchange exchange = new HttpExchangeStub("GET", uri);

        // Exercise SUT
        handler.handle(exchange);

        // Verify result
        JSONObject response = new JSONObject(exchange.getResponseBody().toString());
        assertEquals(400, exchange.getResponseCode());
        assertEquals("Parameters are invalid", response.get("message"));
        assertEquals("Different currencies should be specified", response.getJSONObject("errors").get("to"));
    }
}
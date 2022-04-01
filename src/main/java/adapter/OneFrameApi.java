package adapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.TimeZone;

import domain.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class OneFrameApi implements ExchangeRateApi {
    private final ApiConfig config;
    private final HttpClient client;

    public OneFrameApi(ApiConfig config) {
        this.config = config;
        this.client = HttpClient.newHttpClient();
    }

    private Date parseDate(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date timeStamp;
        try {
            timeStamp = dateFormat.parse(dateString);
        } catch (ParseException e) {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            timeStamp = dateFormat.parse(dateString);
        }
        return timeStamp;
    }

    @Override
    public final ExchangeRate exchangeRates(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        // Construct HTTP request
        String pair = String.format(
                "&pair=%s%s",
                currencyPair.fromCurrency(),
                currencyPair.toCurrency()).replaceFirst("&", "?");
        URI url = URI.create(String.format("%s/rates%s", config.host(), pair));
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Accept", "application/json")
                .header("token", config.token()).build();

        Date timeStamp;
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody.contains("error")) {
                JSONObject jsonObject = new JSONObject(response.body());
                throw new ExchangeRateApiUnavailableException(jsonObject.getString("error"));
            }

            JSONArray jsonArray = new JSONArray(responseBody);
            JSONObject obj = jsonArray.getJSONObject(0);
            timeStamp = this.parseDate(obj.getString("time_stamp"));
            return new ExchangeRate(
                    new CurrencyPair(
                            Currency.valueOf(obj.getString("from")),
                            Currency.valueOf(obj.getString("to"))),
                    Math.floor(obj.getFloat("bid") * 100.0) / 100.0,
                    Math.floor(obj.getFloat("ask") * 100.0) / 100.0,
                    Math.floor(obj.getFloat("price") * 100.0) / 100.0,
                    timeStamp);
        } catch (IOException | ParseException e) {
            System.out.println("Failed in parsing time");
            throw new ExchangeRateApiUnavailableException();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }
}

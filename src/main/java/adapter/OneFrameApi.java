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
    private final ExchangeRateCache exchangeRateCache;
    private final int stalePeriodInSecond;
    private final HttpClient client;

    public OneFrameApi(ApiConfig config, ExchangeRateCache exchangeRateCache, int stalePeriodInSecond) {
        this.config = config;
        this.exchangeRateCache = exchangeRateCache;
        this.stalePeriodInSecond = stalePeriodInSecond;
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

    public final ExchangeRate exchangeRates(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        String pair = "";
        pair = String.format("&pair=%s%s", currencyPair.fromCurrency(), currencyPair.toCurrency());
        pair = pair.replaceFirst("&", "?");
        URI url = URI.create(String.format("%s/rates%s", config.host(), pair));
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Accept", "application/json")
                .header("token", config.token()).build();

        ExchangeRate lastExchangeLate = this.exchangeRateCache.newest(currencyPair);
        if (lastExchangeLate != null) {
            Date now = new Date();
            long timeDiffInMillis = now.getTime() - lastExchangeLate.timeStamp().getTime();
            if (timeDiffInMillis < this.stalePeriodInSecond * 1000L) {
                return lastExchangeLate;
            }
        }

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
            ExchangeRate exchangeRate = new ExchangeRate(
                    new CurrencyPair(
                            Currency.valueOf(obj.getString("from")),
                            Currency.valueOf(obj.getString("to"))),
                    Math.floor(obj.getFloat("bid") * 100.0) / 100.0,
                    Math.floor(obj.getFloat("ask") * 100.0) / 100.0,
                    Math.floor(obj.getFloat("price") * 100.0) / 100.0,
                    timeStamp);
            this.exchangeRateCache.add(exchangeRate);
            return exchangeRate;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ExchangeRateApiUnavailableException(e.getMessage());
        } catch (ParseException e) {
            System.out.println("Failed in parsing time");
            throw new RuntimeException(e);
        }
    }
}

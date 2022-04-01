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
    ApiConfig config;

    public OneFrameApi(ApiConfig config) {
        this.config = config;
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

    public final ExchangeRate[] exchangeRates(CurrencyPair[] currencyPairs) throws ExchangeRateApiUnavailableException {
        String pairs = "";
        for (CurrencyPair pair : currencyPairs) {
            pairs = pairs.concat(String.format("&pair=%s%s", pair.fromCurrency(), pair.toCurrency()));
        }
        pairs = pairs.replaceFirst("&", "?");
        URI url = URI.create(String.format("%s/rates%s", config.host(), pairs));
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .header("Accept", "application/json")
                .header("token", config.token()).build();

        Date timeStamp;
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (responseBody.contains("error")) {
                JSONObject jsonObject = new JSONObject(response.body());
                throw new ExchangeRateApiUnavailableException(jsonObject.getString("error"));
            }

            JSONArray jsonArray = new JSONArray(responseBody);
            ExchangeRate[] exchangeRates = new ExchangeRate[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                timeStamp = this.parseDate(obj.getString("time_stamp"));
                exchangeRates[i] = new ExchangeRate(
                        new CurrencyPair(
                                Currency.valueOf(obj.getString("from")),
                                Currency.valueOf(obj.getString("to"))),
                        Math.floor(obj.getFloat("bid") * 100.0) / 100.0,
                        Math.floor(obj.getFloat("ask") * 100.0) / 100.0,
                        Math.floor(obj.getFloat("price") * 100.0) / 100.0,
                        timeStamp);
            }
            return exchangeRates;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ExchangeRateApiUnavailableException(e.getMessage());
        } catch (ParseException e) {
            System.out.println("Failed in parsing time");
            throw new RuntimeException(e);
        }
    }
}

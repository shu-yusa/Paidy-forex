import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.TimeZone;

import org.json.JSONObject;
import org.json.JSONArray;

public class OneFrameApi implements OneFrameApiInterface  {
    ApiConfig config;
    SimpleDateFormat dateFormat;

    public OneFrameApi(ApiConfig config) {
        this.config = config;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    final public ExchangeRate[] exchangeRates(CurrencyPair[] currencyPairs) {
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
            JSONArray jsonArray = new JSONArray(response.body());
            ExchangeRate[] exchangeRates = new ExchangeRate[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                try {
                    timeStamp = dateFormat.parse(obj.getString("time_stamp"));
                } catch (ParseException e) {
                    System.out.println("Failed in parsing time");
                    throw new RuntimeException(e);
                }
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
            System.out.println("Failed in http request");
            throw new RuntimeException(e);
        }
    }
}

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

public class OneFrameApi implements OneFrameApiInterface  {
    final public ExchangeRate[] exchangeRates(CurrencyPair[] currencyPairs) {
        // Call API
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date timeStamp;
        try {
            timeStamp = dateFormat.parse("2019-01-01T00:00:00.000");
        } catch (ParseException e) {
            System.out.println("Failed in parsing time");
            throw new RuntimeException(e);
        }
        return new ExchangeRate[]{new ExchangeRate(currencyPairs[0], 0.61f, 0.82f, 0.71f, timeStamp)};
    }
}

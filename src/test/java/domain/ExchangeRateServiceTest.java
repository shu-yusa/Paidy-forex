package domain;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExchangeRateServiceTest {
    @Test
    public void testGetExchangeRate() throws ParseException, ExchangeRateApiUnavailableException {
        CurrencyPair pair = new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date timeStamp = dateFormat.parse("2019-01-01T00:00:00.000");

        // Exercise SUT
        ExchangeRateService service = new ExchangeRateService(currencyPairs -> new ExchangeRate[]{
                new ExchangeRate(pair, 0.61, 0.82, 0.71, timeStamp)
        });
        ExchangeRate result = service.getExchangeRate(pair);

        // Verify result
        assertEquals(pair, result.currencyPair());
        assertEquals(0.61, result.bid(), 0.00001);
        assertEquals(0.82, result.ask(), 0.00001);
        assertEquals(0.71, result.price(), 0.00001);
        assertEquals(timeStamp, result.timeStamp());
    }

    @Test
    public void testGetExchangeRateThrowsExceptionIfExternalServiceIsUnavailable() {
        CurrencyPair pair = new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY"));

        // Exercise SUT
        ExchangeRateService service = new ExchangeRateService(currencyPairs -> {
            throw new ExchangeRateApiUnavailableException();
        });

        try {
            service.getExchangeRate(pair);
            fail();
        } catch (ExchangeRateApiUnavailableException ignored) {
        }
    }
}
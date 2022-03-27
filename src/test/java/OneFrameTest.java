import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class OneFrameTest {
    @Test
    public void testGetSingleExchangeRate() {
        int clockPrecisionRangeInMillis = 15000;
        ApiConfig config = new ApiConfig("http://localhost:8080", "10dc303535874aeccc86a8251e6992f5");
        OneFrameApi apiClient = new OneFrameApi(config);
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        Calendar date = Calendar.getInstance();
        Date timeBeforeApiCall = new Date(date.getTimeInMillis() - clockPrecisionRangeInMillis);

        // Exercise SUT
        ExchangeRate[] exchangeRates = apiClient.exchangeRates(new CurrencyPair[]{pair});

        // Verify result
        assertEquals(1, exchangeRates.length);
        ExchangeRate rate = exchangeRates[0];
        assertEquals(pair, rate.currencyPair());
        assertTrue(rate.timeStamp().after(timeBeforeApiCall));
        assertTrue(rate.timeStamp().before(new Date(date.getTimeInMillis() + clockPrecisionRangeInMillis)));
    }
}
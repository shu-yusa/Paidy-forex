import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class OneFrameTest {
    OneFrameApi apiClient;

    @Before
    public void setUp() {
        ApiConfig config = new ApiConfig("http://localhost:8080", "10dc303535874aeccc86a8251e6992f5");
        this.apiClient = new OneFrameApi(config);
    }

    @Test
    public void testGetSingleExchangeRate() {
        int clockPrecisionRangeInMillis = 15000;
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

    @Test
    public void testGetTwoExchangeRates() {
        CurrencyPair[] currencyPairs = new CurrencyPair[]{
            new CurrencyPair(Currency.USD, Currency.JPY),
            new CurrencyPair(Currency.USD, Currency.AUD)
        };

                // Exercise SUT
        ExchangeRate[] exchangeRates = apiClient.exchangeRates(currencyPairs);

        // Verify result
        assertEquals(2, exchangeRates.length);
        assertEquals(currencyPairs[0], exchangeRates[0].currencyPair());
        assertEquals(currencyPairs[1], exchangeRates[1].currencyPair());
    }
}
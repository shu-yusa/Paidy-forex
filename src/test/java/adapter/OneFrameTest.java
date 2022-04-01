package adapter;

import domain.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;


public class OneFrameTest {
    OneFrameApi apiClient;
    ApiConfig config;
    ExchangeRateCache exchangeRateCache;

    @Before
    public void setUp() {
        this.config = new ApiConfig("http://localhost:8080", "10dc303535874aeccc86a8251e6992f5");
        this.exchangeRateCache = new InMemoryExchangeRateCache();
    }

    @Test
    public void testGetExchangeRate() throws ExchangeRateApiUnavailableException {
        apiClient = new OneFrameApi(this.config);

        int clockPrecisionRangeInMillis = 15000;
        CurrencyPair pair = new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY"));
        Calendar date = Calendar.getInstance();
        Date timeBeforeApiCall = new Date(date.getTimeInMillis() - clockPrecisionRangeInMillis);

        // Exercise SUT
        ExchangeRate rate = apiClient.exchangeRates(pair);

        // Verify result
        assertEquals(pair, rate.currencyPair());
        assertTrue(rate.timeStamp().after(timeBeforeApiCall));
        assertTrue(rate.timeStamp().before(new Date(date.getTimeInMillis() + clockPrecisionRangeInMillis)));
    }

    @Test
    @Ignore("Run separately to test the rate limit")
    public void testServerRateLimit() {
        apiClient = new OneFrameApi(this.config);
        CurrencyPair currencyPair = new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY"));

        // Exercise SUT
        try {
            for (int i = 0; i < 1001; i++) {
                apiClient.exchangeRates(currencyPair);
            }
            fail();
        } catch (ExchangeRateApiUnavailableException e) {
            // Verify result
            assertTrue(e.getMessage().contains("Quota"));
        }
    }
}
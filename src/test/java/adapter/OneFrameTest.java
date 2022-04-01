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
    public void testGetSingleExchangeRate() throws ExchangeRateApiUnavailableException {
        apiClient = new OneFrameApi(this.config, this.exchangeRateCache, 100);

        int clockPrecisionRangeInMillis = 15000;
        CurrencyPair pair = new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY"));
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
    public void testGetTwoExchangeRates() throws ExchangeRateApiUnavailableException {
        apiClient = new OneFrameApi(this.config, this.exchangeRateCache, 100);

        CurrencyPair[] currencyPairs = new CurrencyPair[]{
            new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY")),
            new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("AUD"))
        };

        // Exercise SUT
        ExchangeRate[] exchangeRates = apiClient.exchangeRates(currencyPairs);

        // Verify result
        assertEquals(2, exchangeRates.length);
        assertEquals(currencyPairs[0], exchangeRates[0].currencyPair());
        assertEquals(currencyPairs[1], exchangeRates[1].currencyPair());
    }

    @Test
    @Ignore("Run separately to test the rate limit")
    public void testServerRateLimit() {
        apiClient = new OneFrameApi(this.config, this.exchangeRateCache, 0);

        CurrencyPair[] currencyPairs = new CurrencyPair[]{
                new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY")),
        };

        // Exercise SUT
        try {
            for (int i = 0; i < 1001; i++) {
                apiClient.exchangeRates(currencyPairs);
            }
            fail();
        } catch (ExchangeRateApiUnavailableException e) {
            // Verify result
            assertTrue(e.getMessage().contains("Quota"));
        }
    }

    @Test
    public void testCachedResultIsReturnedForSubsequentRequests() throws ExchangeRateApiUnavailableException {
        apiClient = new OneFrameApi(this.config, this.exchangeRateCache, 100);

        CurrencyPair[] currencyPairs = new CurrencyPair[]{
                new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY")),
        };

        // Exercise SUT
        ExchangeRate exchangeRate1 = apiClient.exchangeRates(currencyPairs)[0];
        ExchangeRate exchangeRate2 = apiClient.exchangeRates(currencyPairs)[0];

        // Verify result
        assertEquals(exchangeRate1.timeStamp(), exchangeRate2.timeStamp());
    }

    @Test
    public void testCachedResultIsNotReturnedForSubsequentRequestsIfCacheStalePeriodIsPassed() throws ExchangeRateApiUnavailableException, InterruptedException {
        apiClient = new OneFrameApi(this.config, this.exchangeRateCache, 0);

        CurrencyPair[] currencyPairs = new CurrencyPair[]{
                new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY")),
        };

        // Exercise SUT
        ExchangeRate exchangeRate1 = apiClient.exchangeRates(currencyPairs)[0];
        ExchangeRate exchangeRate2 = apiClient.exchangeRates(currencyPairs)[0];

        // Verify result
        assertNotEquals(exchangeRate1.timeStamp(), exchangeRate2.timeStamp());
    }

    @Test
    public void testServerAccepts10000RequestsWithoutReachingRateLimit() throws ExchangeRateApiUnavailableException {
        apiClient = new OneFrameApi(this.config, this.exchangeRateCache, 100);

        CurrencyPair[] currencyPairs = new CurrencyPair[]{
                new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY")),
        };

        // Exercise SUT
        ExchangeRate exchangeRate = apiClient.exchangeRates(currencyPairs)[0];
        for (int i = 0; i < 10000; i++) {
            ExchangeRate exchangeRate2 = apiClient.exchangeRates(currencyPairs)[0];
            assertEquals(exchangeRate, exchangeRate2);
        }
    }

    @Test
    public void testCachedResultIsNotReturnedForDifferentCurrencyPairs() throws ExchangeRateApiUnavailableException {
        apiClient = new OneFrameApi(this.config, this.exchangeRateCache, 100);

        // Exercise SUT
        CurrencyPair[] currencyPairs1 = new CurrencyPair[]{
                new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("JPY")),
        };
        ExchangeRate exchangeRate1 = apiClient.exchangeRates(currencyPairs1)[0];
        CurrencyPair[] currencyPairs2 = new CurrencyPair[]{
                new CurrencyPair(Currency.valueOf("USD"), Currency.valueOf("AUD")),
        };
        ExchangeRate exchangeRate2 = apiClient.exchangeRates(currencyPairs2)[0];

        // Verify result
        assertNotEquals(exchangeRate1.timeStamp(), exchangeRate2.timeStamp());
    }
}
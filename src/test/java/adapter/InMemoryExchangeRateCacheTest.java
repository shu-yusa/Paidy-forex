package adapter;

import domain.*;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;


public class InMemoryExchangeRateCacheTest {
    @Test
    public void testInMemoryExchangeRateCacheNewestReturnsEmptyIfNothingIsCached() {
        ExchangeRateCache cache = new InMemoryExchangeRateCache();
        assertNull(cache.newest(new CurrencyPair(Currency.USD, Currency.JPY)));
    }

    @Test
    public void testInMemoryExchangeRateCacheIsReturned() {
        ExchangeRateCache cache = new InMemoryExchangeRateCache();
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        ExchangeRate rate = new ExchangeRate(
                pair, 0.1, 0.1, 0.1, new Date());

        cache.add(rate);

        assertEquals(rate, cache.newest(pair));
    }

    @Test
    public void testInMemoryExchangeRateCacheNewestReturnsNewerCache() {
        ExchangeRateCache cache = new InMemoryExchangeRateCache();
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        ExchangeRate rate1 = new ExchangeRate(
                pair, 0.1, 0.1, 0.1, new Date());
        ExchangeRate rate2 = new ExchangeRate(
                pair, 0.2, 0.2, 0.2, new Date());

        cache.add(rate1);
        cache.add(rate2);

        assertEquals(rate2, cache.newest(pair));
    }

    @Test
    public void testInMemoryExchangeRateCacheDifferentCurrencyPairsAreCached() {
        ExchangeRateCache cache = new InMemoryExchangeRateCache();
        CurrencyPair pair1 = new CurrencyPair(Currency.USD, Currency.JPY);
        CurrencyPair pair2 = new CurrencyPair(Currency.USD, Currency.AUD);
        ExchangeRate rate1 = new ExchangeRate(
                pair1, 0.1, 0.1, 0.1, new Date());
        ExchangeRate rate2 = new ExchangeRate(
                pair2, 0.2, 0.2, 0.2, new Date());

        cache.add(rate1);
        cache.add(rate2);

        assertEquals(rate1, cache.newest(pair1));
        assertEquals(rate2, cache.newest(pair2));
    }
}
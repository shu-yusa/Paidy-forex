package domain;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class ExchangeRateTest {
    @Test
    public void testExchangeRateIsOlderThanFalse() {
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        ExchangeRate rate = new ExchangeRate(pair, 0.5, 0.5, 0.5, new Date());

        // Exercise SUT
        boolean result = rate.isOlderThan(new Date(), 100);

        // Verify result
        assertFalse(result);
    }

    @Test
    public void testExchangeRateIsOlderThanTrue() {
        Calendar date = Calendar.getInstance();
        int secondsAgo = 100;
        Date timeStamp = new Date(date.getTimeInMillis() - 100 * 1000);

        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        ExchangeRate rate = new ExchangeRate(pair, 0.5, 0.5, 0.5, timeStamp);

        // Exercise SUT
        boolean result = rate.isOlderThan(new Date(), secondsAgo);

        // Verify result
        assertTrue(result);
    }
}
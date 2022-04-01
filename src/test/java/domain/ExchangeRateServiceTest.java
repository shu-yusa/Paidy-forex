package domain;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class ExchangeRateServiceTest {
    private ExchangeRateCache createExchangeRateCacheStub(ExchangeRate exchangeRate) {
        return new ExchangeRateCache() {
            @Override
            public ExchangeRate newest(CurrencyPair currencyPair) {
                return exchangeRate;
            }

            @Override
            public void add(ExchangeRate exchangeRate) {

            }
        };
    }

    @Test
    public void testGetExchangeRate() throws ParseException, ExchangeRateApiUnavailableException {
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date timeStamp = dateFormat.parse("2019-01-01T00:00:00.000");

        // Exercise SUT
        ExchangeRate exchangeRate = new ExchangeRate(pair, 0.61, 0.82, 0.71, timeStamp);
        ExchangeRateService service = new ExchangeRateService(
                currencyPairs -> exchangeRate,
                this.createExchangeRateCacheStub(exchangeRate),
                100);
        ExchangeRate result = service.query(pair);

        // Verify result
        assertEquals(pair, result.currencyPair());
        assertEquals(0.61, result.bid(), 0.00001);
        assertEquals(0.82, result.ask(), 0.00001);
        assertEquals(0.71, result.price(), 0.00001);
        assertEquals(timeStamp, result.timeStamp());
    }

    @Test
    public void testGetExchangeRateThrowsExceptionIfExternalServiceIsUnavailable() {
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);

        // Exercise SUT
        ExchangeRateService service = new ExchangeRateService(
                currencyPairs -> {
                    throw new ExchangeRateApiUnavailableException();
                },
                this.createExchangeRateCacheStub(null),
                100);

        try {
            service.query(pair);
            fail();
        } catch (ExchangeRateApiUnavailableException ignored) {
        }
    }

    @Test
    public void testCachedResultIsReturnedForSubsequentCalls() throws ExchangeRateApiUnavailableException {
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);

        // Exercise SUT
        ExchangeRate oldExchangeRate = new ExchangeRate(pair, 0.61, 0.82, 0.71, new Date());
        ExchangeRate newExchangeRate = new ExchangeRate(pair, 0.31, 0.52, 0.51, new Date());
        ExchangeRateService service = new ExchangeRateService(
                currencyPairs -> newExchangeRate,
                this.createExchangeRateCacheStub(oldExchangeRate),
                100);
        ExchangeRate result = service.query(pair);

        // Verify result
        assertEquals(oldExchangeRate, result);

    }

    @Test
    public void testCachedResultIsNotReturnedForSubsequentCallsIfCacheStalePeriodIsPassed() throws ExchangeRateApiUnavailableException {
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar date = Calendar.getInstance();
        int stalePeriodInSecond = 100;
        Date timeStamp = new Date(date.getTimeInMillis() - 100 * 1000);

        // Exercise SUT
        ExchangeRate oldExchangeRate = new ExchangeRate(pair, 0.61, 0.82, 0.71, timeStamp);
        ExchangeRate newExchangeRate = new ExchangeRate(pair, 0.31, 0.52, 0.51, timeStamp);
        ExchangeRateService service = new ExchangeRateService(
                currencyPairs -> newExchangeRate,
                this.createExchangeRateCacheStub(oldExchangeRate),
                stalePeriodInSecond);
        ExchangeRate result = service.query(pair);

        // Verify result
        assertEquals(newExchangeRate, result);
    }
}
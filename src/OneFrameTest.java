import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class OneFrameTest {
    @Test
    public void testGetSingleExchangeRate() {
        OneFrameApi apiClient = new OneFrameApi();
        CurrencyPair pair = new CurrencyPair(Currency.USD, Currency.JPY);
        ExchangeRate[] exchangeRates = apiClient.exchangeRates(new CurrencyPair[]{pair});

        Date timeStamp = new GregorianCalendar(2019, Calendar.JANUARY, 1, 0, 0, 0).getTime();
        ExchangeRate expectedRate = new ExchangeRate(pair, 0.61f, 0.82f, 0.71f, timeStamp);
        assertArrayEquals(new ExchangeRate[]{expectedRate}, exchangeRates);
    }
}





























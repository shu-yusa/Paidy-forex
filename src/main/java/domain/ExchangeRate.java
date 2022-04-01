package domain;

import java.util.Date;

public record ExchangeRate(CurrencyPair currencyPair, double bid, double ask, double price, Date timeStamp) {
    public boolean isOlderThan(Date date, int bySecond) {
        long timeDiffInMillis = date.getTime() - this.timeStamp().getTime();
        return timeDiffInMillis >= bySecond * 1000L;
    }
}

package domain;

import java.util.Date;

public record ExchangeRate(CurrencyPair currencyPair, double bid, double ask, double price, Date timeStamp) {
}

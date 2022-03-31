package domain;

public interface ExchangeRateApi {
    ExchangeRate[] exchangeRates(CurrencyPair[] currencyPairs);
}

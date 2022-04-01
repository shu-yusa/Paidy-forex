package domain;

public interface ExchangeRateCache {
    ExchangeRate newest(CurrencyPair currencyPair);
    void add(ExchangeRate exchangeRate);
}

package domain;

public interface ExchangeRateCache {
    public ExchangeRate newest(CurrencyPair currencyPair);
    public void add(ExchangeRate exchangeRate);
}

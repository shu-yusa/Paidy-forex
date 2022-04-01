package domain;

public interface ExchangeRateCache {
    public ExchangeRate newest();
    public void add(ExchangeRate exchangeRate);
}

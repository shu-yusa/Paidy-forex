public interface ExchangeRateApi {
    abstract public ExchangeRate[] exchangeRates(CurrencyPair[] currencyPairs);
}

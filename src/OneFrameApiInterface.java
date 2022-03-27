import java.util.List;

public interface OneFrameApiInterface {
    abstract public ExchangeRate[] exchangeRates(CurrencyPair[] currencyPairs);
}

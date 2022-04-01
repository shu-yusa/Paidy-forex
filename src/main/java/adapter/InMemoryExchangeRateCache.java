package adapter;

import domain.CurrencyPair;
import domain.ExchangeRate;
import domain.ExchangeRateCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryExchangeRateCache implements ExchangeRateCache {
    private final Map<CurrencyPair, ExchangeRate> cachedExchangeRates;

    public InMemoryExchangeRateCache() {
        this.cachedExchangeRates = new HashMap<>();
    }

    public ExchangeRate newest(CurrencyPair currencyPair) {
        if (this.cachedExchangeRates.isEmpty()) {
            return null;
        }
        return this.cachedExchangeRates.get(currencyPair);
    }

    @Override
    public void add(ExchangeRate exchangeRate) {
        this.cachedExchangeRates.put(exchangeRate.currencyPair(), exchangeRate);
    }
}

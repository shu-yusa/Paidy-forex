package adapter;

import domain.ExchangeRate;
import domain.ExchangeRateCache;

import java.util.ArrayList;

public class InMemoryExchangeRateCache implements ExchangeRateCache {
    private final ArrayList<ExchangeRate> cachedExchangeRates;
    private final int cacheSize;

    public InMemoryExchangeRateCache(int cacheSize) {
        this.cachedExchangeRates = new ArrayList<>(cacheSize);
        this.cacheSize = cacheSize;
    }

    public ExchangeRate newest() {
        if (this.cachedExchangeRates.isEmpty()) {
            return null;
        }
        return this.cachedExchangeRates.get(this.cachedExchangeRates.size() - 1);
    }

    @Override
    public void add(ExchangeRate exchangeRate) {
        if (this.cachedExchangeRates.size() == this.cacheSize) {
            this.cachedExchangeRates.remove(0);
        }
        this.cachedExchangeRates.add(exchangeRate);
    }
}

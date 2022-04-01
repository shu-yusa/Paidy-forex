package domain;

import java.util.Date;

public class ExchangeRateService {
    private final ExchangeRateApi exchangeRateApi;
    private final ExchangeRateCache exchangeRateCache;
    private final int stalePeriodInSecond;

    public ExchangeRateService(ExchangeRateApi exchangeRateApi, ExchangeRateCache exchangeRateCache, int stalePeriodInSecond) {
        this.exchangeRateApi = exchangeRateApi;
        this.exchangeRateCache = exchangeRateCache;
        this.stalePeriodInSecond = stalePeriodInSecond;
    }

    public ExchangeRate getExchangeRate(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        // If cached exchange rate is fresh enough to meet the requirement, return it.
        ExchangeRate lastExchangeLate = this.exchangeRateCache.newest(currencyPair);
        if (lastExchangeLate != null) {
            Date now = new Date();
            if (!lastExchangeLate.isOlderThan(now, this.stalePeriodInSecond)) {
                return lastExchangeLate;
            }
        }

        ExchangeRate exchangeRate = this.exchangeRateApi.exchangeRates(currencyPair);

        this.exchangeRateCache.add(exchangeRate);
        return exchangeRate;
    }
}

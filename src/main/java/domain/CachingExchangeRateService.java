package domain;

import java.util.Date;

public class CachingExchangeRateService implements QueryService<CurrencyPair, ExchangeRate> {
    private final QueryService<CurrencyPair, ExchangeRate> exchangeRateQueryService;
    private final ExchangeRateCache exchangeRateCache;
    private final int stalePeriodInSecond;

    public CachingExchangeRateService(QueryService<CurrencyPair, ExchangeRate> exchangeRateQueryService, ExchangeRateCache exchangeRateCache, int stalePeriodInSecond) {
        this.exchangeRateQueryService = exchangeRateQueryService;
        this.exchangeRateCache = exchangeRateCache;
        this.stalePeriodInSecond = stalePeriodInSecond;
    }

    @Override
    public ExchangeRate query(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        // If cached exchange rate is fresh enough to meet the requirement, return it.
        ExchangeRate lastExchangeLate = this.exchangeRateCache.newest(currencyPair);
        if (lastExchangeLate != null) {
            Date now = new Date();
            if (!lastExchangeLate.isOlderThan(now, this.stalePeriodInSecond)) {
                return lastExchangeLate;
            }
        }

        ExchangeRate exchangeRate = this.exchangeRateQueryService.query(currencyPair);

        this.exchangeRateCache.add(exchangeRate);
        return exchangeRate;
    }
}

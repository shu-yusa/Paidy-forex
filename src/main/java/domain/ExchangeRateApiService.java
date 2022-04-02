package domain;

public class ExchangeRateApiService extends ExchangeRateService {
    private final ExchangeRateApi exchangeRateApi;

    public ExchangeRateApiService(ExchangeRateApi exchangeRateApi) {
        this.exchangeRateApi = exchangeRateApi;
    }

    @Override
    public ExchangeRate query(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        // If cached exchange rate is fresh enough to meet the requirement, return it.
        return this.exchangeRateApi.exchangeRates(currencyPair);
    }
}

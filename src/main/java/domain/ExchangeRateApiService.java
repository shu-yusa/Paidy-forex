package domain;

public class ExchangeRateApiService extends ExchangeRateService {
    private final ExchangeRateApi exchangeRateApi;

    public ExchangeRateApiService(ExchangeRateApi exchangeRateApi) {
        this.exchangeRateApi = exchangeRateApi;
    }

    @Override
    public ExchangeRate query(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        return this.exchangeRateApi.exchangeRates(currencyPair);
    }
}

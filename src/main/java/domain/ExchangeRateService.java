package domain;

public class ExchangeRateService {
    ExchangeRateApi exchangeRateApi;
    public ExchangeRateService(ExchangeRateApi exchangeRateApi) {
        this.exchangeRateApi = exchangeRateApi;
    }

    public ExchangeRate getExchangeRate(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        return this.exchangeRateApi.exchangeRates(currencyPair);
    }
}

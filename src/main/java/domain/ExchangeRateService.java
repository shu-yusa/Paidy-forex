package domain;

public class ExchangeRateService {
    ExchangeRateApi exchangeRateApi;
    public ExchangeRateService(ExchangeRateApi exchangeRateApi) {
        this.exchangeRateApi = exchangeRateApi;
    }

    public ExchangeRate getExchangeRate(CurrencyPair currencyPair) {
        // Exercise SUT
        ExchangeRate[] exchangeRates = this.exchangeRateApi.exchangeRates(new CurrencyPair[]{currencyPair});
        return exchangeRates[0];
    }
}

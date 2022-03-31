package domain;

public class ExchangeRateService {
    ExchangeRateApi exchangeRateApi;
    public ExchangeRateService(ExchangeRateApi exchangeRateApi) {
        this.exchangeRateApi = exchangeRateApi;
    }

    public ExchangeRate getExchangeRate(CurrencyPair currencyPair) throws ExchangeRateApiUnavailableException {
        ExchangeRate[] exchangeRates = this.exchangeRateApi.exchangeRates(new CurrencyPair[]{currencyPair});
        if (exchangeRates.length < 1) {
            System.out.println("Unexpected API response");
            throw new ExchangeRateApiUnavailableException();
        }
        return exchangeRates[0];
    }
}

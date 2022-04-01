package domain;

public class ExchangeRateApiUnavailableException extends Exception {
    public ExchangeRateApiUnavailableException() {
    }

    public ExchangeRateApiUnavailableException(String message) {
        super(message);
    }
}

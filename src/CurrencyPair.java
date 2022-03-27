import java.util.Objects;

public class CurrencyPair {
    private final Currency fromCurrency;
    private final Currency toCurrency;

    public CurrencyPair(Currency fromCurrency, Currency toCurrency) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CurrencyPair otherPair)) {
            return  false;
        }

        return Objects.equals(this.fromCurrency, otherPair.fromCurrency)
                && Objects.equals(this.toCurrency, otherPair.toCurrency);
    }

    public int hashCode() {
        return Objects.hash(this.fromCurrency, this.toCurrency);
    }
}

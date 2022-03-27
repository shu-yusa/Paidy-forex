import java.util.Date;
import java.util.Objects;


public class ExchangeRate {
    private final CurrencyPair currencyPair;
    private final float bid;
    private final float ask;
    private final float price;
    private final Date timeStamp;

    public ExchangeRate(CurrencyPair currencyPair, float bid, float ask, float price, Date timeStamp) {
        this.currencyPair = currencyPair;
        this.bid = bid;
        this.ask = ask;
        this.price = price;
        this.timeStamp = timeStamp;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExchangeRate otherRate)) {
            return false;
        }

        return this.currencyPair.equals(otherRate.currencyPair) &&
                this.bid == otherRate.bid && this.ask == otherRate.ask &&
                this.price == otherRate.price && this.timeStamp.equals(otherRate.timeStamp);
    }

    public int hashCode() {
        return Objects.hash(this.currencyPair, this.bid, this.ask, this.price, this.timeStamp);
    }
}

package domain;

public interface QueryService<S, T>  {
    T query(S input) throws ExchangeRateApiUnavailableException;
}

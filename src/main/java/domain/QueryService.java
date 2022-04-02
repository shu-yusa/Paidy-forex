package domain;

public interface QueryService<S, T, E extends Throwable>  {
    T query(S input) throws E;
}

package orderbook;

public record PriceLevelSnapshot (
    long price,
    long quantity
) {}

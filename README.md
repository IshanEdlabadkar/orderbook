# orderbook

An in-memory limit order book with price-time priority matching, written in Java 21 (Maven project under `v1/`).

## Domain model

- `Order` — a limit order (price, side, order id, quantity, ticker). Tracks `remainingQuantity` as it gets filled.
- `PriceLevel` — FIFO queue of resting orders at a single price.
- `OrderBook` — maintains the buy and sell sides as price-sorted maps of `PriceLevel`, matches incoming orders, and rests whatever doesn't fill.
- `Trade` — a completed fill between a buy order and a sell order.

## Matching

`addOrder` matches the incoming order against the opposite side before resting any of it:

- Price-time priority: best price first, then FIFO within a price level.
- A BUY crosses sell levels priced at or below its limit; a SELL crosses buy levels priced at or above its limit.
- Whatever quantity doesn't fill rests in the book at the order's limit price.
- Fills are recorded to a running trade log, readable via `getTrades()`.

Each `OrderBook` instance is single-instrument — it does not check `ticker` when matching. Routing orders to the right book per symbol is expected to happen one layer above this class.

## Build & test

```
cd v1
mvn test
```

## Status

Implemented: adding orders, matching, top-of-book, depth snapshot (`getOrderBook`), trade log.

Not yet implemented: order cancellation, market orders.

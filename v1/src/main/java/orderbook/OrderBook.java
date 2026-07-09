package orderbook;

import java.util.*;
import java.util.stream.*;

public class OrderBook {
  TreeMap<Long, PriceLevel> buySide = new TreeMap<>();
  TreeMap<Long, PriceLevel> sellSide = new TreeMap<>();
  List<Trade> trades = new ArrayList<>();

  public void addOrder(Order order) {
    match(order);
    if (order.getRemainingQuantity() > 0) {
      Map<Long, PriceLevel> targetSide = (order.getSide() == Side.BUY ? buySide : sellSide);
      targetSide.computeIfAbsent(order.getPrice(), p -> new PriceLevel(p)).addOrder(order);
    }
  }

  public List<Trade> getTrades() {
    return trades;
  }

  public long getTopOfBook(Side side) {
    if (side == Side.BUY) {
      if (buySide.isEmpty()) {
        return -1;
      } 
      return buySide.lastKey();
    }
    else {
      if (sellSide.isEmpty()) {
        return -1;
      } 
      return sellSide.firstKey();
    }
  }

  public List<PriceLevelSnapshot> getOrderBook(int depth, Side side) {
   NavigableMap<Long, PriceLevel> sourceMap = (side == Side.BUY) ? buySide.descendingMap() : sellSide;{
    return sourceMap.entrySet().stream()
      .limit(depth)
      .map(e -> new PriceLevelSnapshot(e.getKey(), e.getValue().getTotalQuantity()))
      .collect(Collectors.toList());
   }
  }

  private void match(Order incoming) {
    NavigableMap<Long, PriceLevel> oppositeSide =
        (incoming.getSide() == Side.BUY) ? sellSide : buySide.descendingMap();
    Iterator<Map.Entry<Long, PriceLevel>> levelIter = oppositeSide.entrySet().iterator();

    while (levelIter.hasNext() && incoming.getRemainingQuantity() > 0) {
      Map.Entry<Long, PriceLevel> levelEntry = levelIter.next();
      long levelPrice = levelEntry.getKey();
      PriceLevel level = levelEntry.getValue();

      if (!crosses(incoming.getPrice(), levelPrice, incoming.getSide())) {
        break;
      }

      Iterator<Map.Entry<Long, Order>> orderIter = level.orders.entrySet().iterator();
      while (orderIter.hasNext() && incoming.getRemainingQuantity() > 0) {
        Order resting = orderIter.next().getValue();

        long fillQty = Math.min(incoming.getRemainingQuantity(), resting.getRemainingQuantity());
        incoming.setRemainingQuantity(incoming.getRemainingQuantity() - fillQty);
        resting.setRemainingQuantity(resting.getRemainingQuantity() - fillQty);

        long buyOrderId = (incoming.getSide() == Side.BUY) ? incoming.getOrderId() : resting.getOrderId();
        long sellOrderId = (incoming.getSide() == Side.BUY) ? resting.getOrderId() : incoming.getOrderId();
        trades.add(new Trade(levelPrice, fillQty, buyOrderId, sellOrderId));

        if (resting.getRemainingQuantity() == 0) {
          orderIter.remove();
        }
      }

      if (level.orders.isEmpty()) {
        levelIter.remove();
      }
    }
  }

  private boolean crosses(long incomingPrice, long levelPrice, Side side) {
    return (side == Side.BUY) ? levelPrice <= incomingPrice : levelPrice >= incomingPrice;
  }
}

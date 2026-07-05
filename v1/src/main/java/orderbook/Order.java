import java.time.Instant;
public class Order {
  final long price;
  final Side side;
  final long orderId;
  final long quantity;
  long remainingQuantity;
  final String ticker;
  final Instant eventTime;

  public Order (long price, Side side, long orderId, long quantity, String ticker) {
    this.price = price;
    this.side = side;
    this.orderId = orderId;
    this.quantity = quantity;
    this.remainingQuantity = quantity;
    this.ticker = ticker;
    this.eventTime = Instant.now();
  }

  public void setRemainingQuantity(long remainingQuantity) {
    this.remainingQuantity = remainingQuantity;
  }

  public Side getSide() {
    return side;
  }

  public long getPrice() {
    return price;
  }

  public long getOrderId() {
    return orderId;
  }
  
}

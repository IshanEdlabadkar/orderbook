import java.time.Instant;
public class Trade {
  final long price;
  final long buyOrderId;
  final long sellOrderId;
  final long quantity;
  final Instant time;

  public Trade(long price, long quantity, long buyOrderId, long sellOrderId) {
    this.time = Instant.now();
    this.price = price;
    this.quantity = quantity;
    this.buyOrderId = buyOrderId;
    this.sellOrderId = sellOrderId;
  }
}

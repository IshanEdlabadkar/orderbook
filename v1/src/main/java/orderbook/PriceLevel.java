package orderbook;
import java.util.LinkedHashMap;
import java.util.Map;
public class PriceLevel {
  LinkedHashMap<Long, Order> orders = new LinkedHashMap<>();
  long price;
  public PriceLevel(long price) {
    this.price = price;
  }

  public void addOrder(Order order) {
    orders.put(order.orderId, order);
  }

  public void removeOrder(Order order) {
    orders.remove(order.orderId);
  }

  public long getPrice() {
    return price;
  }

  public long getTotalQuantity() {
    long sum = 0;
    for (Map.Entry<Long, Order> entry : orders.entrySet()) {
      sum += entry.getValue().getQuantity();
    }
    return sum;
  }
}

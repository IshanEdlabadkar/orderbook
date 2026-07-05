import java.util.LinkedHashMap;
public class PriceLevel {
  LinkedHashMap<Long, Order> orders = new LinkedHashMap<>();
  long price;
  public PriceLevel(long price) {
    this.price = price;
  }
}

package orderbook;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OrderBookTest {
    @Test
    void addOrder_samePriceTwoOrders_areStoredInSamePriceLevelInFifoOrder() {
        //two orders with the same price must be stored in FiFo order
        OrderBook book = new OrderBook();
        Order order1 = new Order(100L, Side.BUY, 1L, 10L, "AAPL");
        Order order2 = new Order(100L, Side.BUY, 2L, 40L, "AAPL");
        book.addOrder(order1);
        book.addOrder(order2);
        PriceLevel level = book.buySide.get(100L);
        
        assertNotNull(level);
        assertEquals(2, level.orders.size());
        assertEquals(1L, level.orders.keySet().iterator().next());
    }

    @Test
    void addOrder_buyAndSell_goToDifferentSides() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));
        book.addOrder(new Order(101L, Side.SELL, 2L, 10L, "AAPL"));

        assertTrue(book.buySide.containsKey(100L));
        assertTrue(book.sellSide.containsKey(101L));
        assertFalse(book.buySide.containsKey(101L));
    }

    @Test
    void getTopOfBook_multiplePricesOnBothSides_returnsBestBidAndBestAsk() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));
        book.addOrder(new Order(40L, Side.BUY, 2L, 10L, "AAPL"));
        book.addOrder(new Order(150L, Side.SELL, 3L, 10L, "AAPL"));
        book.addOrder( new Order(130L, Side.SELL, 4L, 10L, "AAPL"));

        assertEquals(100, book.getTopOfBook(Side.BUY));
        assertEquals(130, book.getTopOfBook(Side.SELL));

    }

    @Test
    void getOrderBook_buySide_returnsLevelsDescendingByPrice() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));
        book.addOrder(new Order(105L, Side.BUY, 2L, 5L, "AAPL"));
        book.addOrder(new Order(95L, Side.BUY, 3L, 20L, "AAPL"));

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(10, Side.BUY);

        assertEquals(3, snapshots.size());
        assertEquals(105L, snapshots.get(0).price());
        assertEquals(100L, snapshots.get(1).price());
        assertEquals(95L, snapshots.get(2).price());
    }

    @Test
    void getOrderBook_sellSide_returnsLevelsAscendingByPrice() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.SELL, 1L, 10L, "AAPL"));
        book.addOrder(new Order(105L, Side.SELL, 2L, 5L, "AAPL"));
        book.addOrder(new Order(95L, Side.SELL, 3L, 20L, "AAPL"));

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(10, Side.SELL);

        assertEquals(3, snapshots.size());
        assertEquals(95L, snapshots.get(0).price());
        assertEquals(100L, snapshots.get(1).price());
        assertEquals(105L, snapshots.get(2).price());
    }

    @Test
    void getOrderBook_depthLimitsResults_returnsBestLevelsOnly() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));
        book.addOrder(new Order(105L, Side.BUY, 2L, 5L, "AAPL"));
        book.addOrder(new Order(95L, Side.BUY, 3L, 20L, "AAPL"));

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(2, Side.BUY);

        assertEquals(2, snapshots.size());
        assertEquals(105L, snapshots.get(0).price());
        assertEquals(100L, snapshots.get(1).price());
    }

    @Test
    void getOrderBook_depthExceedsAvailableLevels_returnsAllLevels() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(50, Side.BUY);

        assertEquals(1, snapshots.size());
        assertEquals(100L, snapshots.get(0).price());
    }

    @Test
    void getOrderBook_depthZero_returnsEmptyList() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(0, Side.BUY);

        assertTrue(snapshots.isEmpty());
    }

    @Test
    void getOrderBook_multipleOrdersSamePriceLevel_quantityIsSummed() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));
        book.addOrder(new Order(100L, Side.BUY, 2L, 40L, "AAPL"));

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(10, Side.BUY);

        assertEquals(1, snapshots.size());
        assertEquals(100L, snapshots.get(0).price());
        assertEquals(50L, snapshots.get(0).quantity());
    }

    @Test
    void getOrderBook_emptySide_returnsEmptyList() {
        OrderBook book = new OrderBook();

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(10, Side.BUY);

        assertTrue(snapshots.isEmpty());
    }

    @Test
    void getOrderBook_ordersOnOppositeSide_areNotIncluded() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));

        List<PriceLevelSnapshot> snapshots = book.getOrderBook(10, Side.SELL);

        assertTrue(snapshots.isEmpty());
    }

    @Test
    void getOrderBook_negativeDepth_throwsIllegalArgumentException() {
        OrderBook book = new OrderBook();
        book.addOrder(new Order(100L, Side.BUY, 1L, 10L, "AAPL"));

        assertThrows(IllegalArgumentException.class, () -> book.getOrderBook(-1, Side.BUY));
    }
}

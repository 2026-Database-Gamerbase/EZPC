package dao;

import model.Order;
import java.util.List;

public interface OrderDAO {

	void insertOrder(Order order);
    int getNextOrderId();
    
    List<Order> getOrdersByOrderId(int orderId);
    List<Order> getOrdersByCafe(String pcCafeId);
}
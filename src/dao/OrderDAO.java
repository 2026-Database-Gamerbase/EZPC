package dao;

import model.Order;

import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {

	void insertOrder(Order order) throws SQLException ;
    int getNextOrderId();
    
    List<Order> getOrdersByOrderId(int orderId);
    List<Order> getOrdersByCafe(String pcCafeId);
}
package dao;

import model.Food;
import model.Order;

import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {

	void insertOrder(Order order) throws SQLException ;
    int getNextOrderId();

    List<Order> getOrdersByOrderId(int orderId);
    List<Order> getOrdersByCafe(String pcCafeId);

    //이 음식을 먹은 손님이 함께 주문한 상위 3개 상품 추천
    List<Food> getRecommendedFoods(String targetFoodName);
}
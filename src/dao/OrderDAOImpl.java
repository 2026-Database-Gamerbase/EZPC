package dao;

import model.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    private final Connection conn;

    public OrderDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insertOrder(Order order) {
        String sql = "INSERT INTO food_order (order_id, food_name, pc_cafe_id, seat_num, food_quantity, food_pay_amount) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getOrderId());
            pstmt.setString(2, order.getFoodName());
            pstmt.setString(3, order.getPcCafeId());
            pstmt.setInt(4, order.getSeatNum());
            pstmt.setInt(5, order.getFoodQuantity());
            pstmt.setInt(6, order.getFoodPayAmount());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getNextOrderId() {
        String sql = "SELECT IFNULL(MAX(order_id), 0) + 1 AS next_id FROM food_order";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public List<Order> getOrdersByOrderId(int orderId) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM food_order WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("food_name"),
                        rs.getString("pc_cafe_id"),
                        rs.getInt("seat_num"),
                        rs.getInt("food_quantity"),
                        rs.getInt("food_pay_amount"),
                        rs.getTimestamp("ordered_at")
                    );
                    orderList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    @Override
    public List<Order> getOrdersByCafe(String pcCafeId) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM food_order WHERE pc_cafe_id = ? ORDER BY ordered_at DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("food_name"),
                        rs.getString("pc_cafe_id"),
                        rs.getInt("seat_num"),
                        rs.getInt("food_quantity"),
                        rs.getInt("food_pay_amount"),
                        rs.getTimestamp("ordered_at")
                    );
                    orderList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }
}
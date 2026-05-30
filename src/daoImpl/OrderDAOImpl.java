package daoImpl;

import model.Order;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.OrderDAO;

public class OrderDAOImpl implements OrderDAO {

    private final Connection conn;

    public OrderDAOImpl(Connection conn) {
        this.conn = conn;
    }

 //throws SQLException -> DB 작업 중 오류가 발생하면 이 메서드에서 처리하지 않고 호출한 상위 메서드로 예외를 전달한다.
 // catch 에서 출력만 하고 끝내면 호출자는 실패를 알 수 없어 정상 처리된 것으로 오해할 수 있다.
    @Override
    public void insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO food_order (order_id, food_name, pc_cafe_id, seat_num, food_quantity,payment_rate, food_pay_amount) VALUES (?, ?, ?, ?, ?,?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getOrderId());
            pstmt.setString(2, order.getFoodName());
            pstmt.setString(3, order.getPcCafeId());
            pstmt.setInt(4, order.getSeatNum());
            pstmt.setInt(5, order.getFoodQuantity());
            pstmt.setDouble(6, order.getPaymentRate());
            pstmt.setInt(7, order.getFoodPayAmount());
            pstmt.executeUpdate();
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
                        rs.getDouble("payment_rate"),
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
                        rs.getDouble("payment_rate"),
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
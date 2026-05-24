package daoImpl;

import model.Food;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.FoodDAO;

public class FoodDAOImpl implements FoodDAO {
    
    private final Connection conn;

    public FoodDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insertFood(Food food) {
        String sql = "INSERT INTO food (food_name, price) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, food.getFoodName());
            pstmt.setInt(2, food.getPrice());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void updateFood(Food food) {
        String sql = "UPDATE food SET price = ? WHERE food_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, food.getPrice());
            pstmt.setString(2, food.getFoodName());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFood(String foodName) {
        String sql = "DELETE FROM food WHERE food_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, foodName);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Food getFoodByName(String foodName) {
        String sql = "SELECT * FROM food WHERE food_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, foodName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Food(
                        rs.getString("food_name"),
                        rs.getInt("price")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Food> getAllFoods() {
        List<Food> foodList = new ArrayList<>();
        String sql = "SELECT * FROM food";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Food food = new Food(
                    rs.getString("food_name"),
                    rs.getInt("price")
                );
                foodList.add(food);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foodList;
    }
}
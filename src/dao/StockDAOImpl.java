package dao;

import model.Stock;
import db.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDAOImpl implements StockDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnector.getConnection();
    }

    @Override
    public void insertStock(Stock stock) {
        String sql = "INSERT INTO stock (pc_cafe_id, food_name, stock_quantity) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, stock.getPcCafeId());
            pstmt.setString(2, stock.getFoodName());
            pstmt.setInt(3, stock.getStockQuantity());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stock getStock(String pcCafeId, String foodName) {
        String sql = "SELECT * FROM stock WHERE pc_cafe_id = ? AND food_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pcCafeId);
            pstmt.setString(2, foodName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Stock(
                        rs.getString("pc_cafe_id"),
                        rs.getString("food_name"),
                        rs.getInt("stock_quantity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Stock> getStocksByCafe(String pcCafeId) {
        List<Stock> stockList = new ArrayList<>();
        String sql = "SELECT * FROM stock WHERE pc_cafe_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pcCafeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Stock stock = new Stock(
                        rs.getString("pc_cafe_id"),
                        rs.getString("food_name"),
                        rs.getInt("stock_quantity")
                    );
                    stockList.add(stock);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stockList;
    }

    @Override
    public void updateStockQuantity(String pcCafeId, String foodName, int newQuantity) {
        String sql = "UPDATE stock SET stock_quantity = ? WHERE pc_cafe_id = ? AND food_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setString(2, pcCafeId);
            pstmt.setString(3, foodName);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStock(String pcCafeId, String foodName) {
        String sql = "DELETE FROM stock WHERE pc_cafe_id = ? AND food_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pcCafeId);
            pstmt.setString(2, foodName);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package daoImpl;



import model.PcCafe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.PcCafeDAO;

public class PcCafeDAOImpl implements PcCafeDAO {
	
	  private Connection conn;

	    public PcCafeDAOImpl(Connection conn) {
	        this.conn = conn;
	    }
	    
	    
    @Override
    public void insert(PcCafe pcCafe) throws SQLException {
        String sql = "INSERT INTO pc_cafe (pc_cafe_id, pc_cafe_name, average_star_rating, total_sales, total_seats) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, pcCafe.getPcId());
            statement.setString(2, pcCafe.getPcName());
            statement.setDouble(3, pcCafe.getAverageStarRating());
            statement.setInt(4, pcCafe.getTotalSales());
            statement.setInt(5, pcCafe.getTotalSeats());
            statement.executeUpdate();
        }
    }

    @Override
    public PcCafe findById(String pcId) throws SQLException {
        String sql = "SELECT * FROM pc_cafe WHERE pc_cafe_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, pcId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToPcCafe(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<PcCafe> findAll() throws SQLException {
        String sql = "SELECT * FROM pc_cafe";
        List<PcCafe> pcCafes = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                pcCafes.add(mapToPcCafe(resultSet));
            }
        }

        return pcCafes;
    }

    @Override
    public void update(PcCafe pcCafe) throws SQLException {
        String sql = "UPDATE pc_cafe SET pc_cafe_name = ?, average_star_rating = ?, total_sales = ?, total_seats = ? WHERE pc_cafe_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, pcCafe.getPcName());
            statement.setDouble(2, pcCafe.getAverageStarRating());
            statement.setInt(3, pcCafe.getTotalSales());
            statement.setInt(4, pcCafe.getTotalSeats());
            statement.setString(5, pcCafe.getPcId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteById(String pcId) throws SQLException {
        String sql = "DELETE FROM pc_cafe WHERE pc_cafe_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, pcId);
            statement.executeUpdate();
        }
    }
    

    
  
    private PcCafe mapToPcCafe(ResultSet resultSet) throws SQLException {
        return new PcCafe(
                resultSet.getString("pc_cafe_id"),
                resultSet.getString("pc_cafe_name"),
                resultSet.getDouble("average_star_rating"),
                resultSet.getInt("total_sales"),
                resultSet.getInt("total_seats")
        );
    }
    
    
}

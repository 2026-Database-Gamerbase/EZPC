package dao;

import db.DatabaseConnector;
import model.Charge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChargeDAOImpl implements ChargeDAO {
    @Override
    public int insert(Charge charge) throws SQLException {
        String sql = "INSERT INTO charge (pc_cafe_id, seat_num, ticket_time, member_id, charge_pay_amount) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, charge.getPcCafeId());
            statement.setInt(2, charge.getSeatNum());
            statement.setInt(3, charge.getTicketTime());
            statement.setString(4, charge.getMemberId());
            statement.setInt(5, charge.getChargePayAmount());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    charge.setChargeId(generatedId);
                    return generatedId;
                }
            }
        }

        return 0;
    }

    @Override
    public Charge findById(int chargeId) throws SQLException {
        String sql = "SELECT * FROM charge WHERE charge_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, chargeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToCharge(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Charge> findAll() throws SQLException {
        String sql = "SELECT * FROM charge";
        List<Charge> charges = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                charges.add(mapToCharge(resultSet));
            }
        }

        return charges;
    }

    @Override
    public List<Charge> findByPcCafeId(String pcCafeId) throws SQLException {
        String sql = "SELECT * FROM charge WHERE pc_cafe_id = ? ORDER BY charged_at DESC";
        List<Charge> charges = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pcCafeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    charges.add(mapToCharge(resultSet));
                }
            }
        }

        return charges;
    }

    @Override
    public List<Charge> findByMemberId(String memberId) throws SQLException {
        String sql = "SELECT * FROM charge WHERE member_id = ? ORDER BY charged_at DESC";
        List<Charge> charges = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    charges.add(mapToCharge(resultSet));
                }
            }
        }

        return charges;
    }

    @Override
    public void deleteById(int chargeId) throws SQLException {
        String sql = "DELETE FROM charge WHERE charge_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, chargeId);
            statement.executeUpdate();
        }
    }

    private Charge mapToCharge(ResultSet resultSet) throws SQLException {
        return new Charge(
                resultSet.getInt("charge_id"),
                resultSet.getString("pc_cafe_id"),
                resultSet.getInt("seat_num"),
                resultSet.getInt("ticket_time"),
                resultSet.getString("member_id"),
                resultSet.getInt("charge_pay_amount"),
                resultSet.getTimestamp("charged_at")
        );
    }
}

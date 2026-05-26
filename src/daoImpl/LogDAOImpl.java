package daoImpl;

import db.DatabaseConnector;
import dao.LogDAO;
import model.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class LogDAOImpl implements LogDAO {
    //log_id는 자동 increment 때문에 따로 insert X
    @Override
    public int insert(Log log) throws SQLException {
        String sql = "INSERT INTO use_log (pc_cafe_id, seat_num, member_id) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, log.getPcCafeId());
            statement.setInt(2, log.getSeatNum());
            statement.setString(3, log.getMemberId());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int logId = generatedKeys.getInt(1);
                    log.setLogId(logId);
                    return logId;
                }
            }
        }

        return 0;
    }

    @Override
    public Log findById(int logId) throws SQLException {
        String sql = "SELECT * FROM use_log WHERE log_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, logId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToLog(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Log> findAll() throws SQLException {
        String sql = "SELECT * FROM use_log";
        List<Log> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                logs.add(mapToLog(resultSet));
            }
        }

        return logs;
    }

    @Override
    public List<Log> findByPcCafeId(String pcCafeId) throws SQLException {
        String sql = "SELECT * FROM use_log WHERE pc_cafe_id = ?";
        List<Log> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pcCafeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapToLog(resultSet));
                }
            }
        }

        return logs;
    }

    @Override
    public List<Log> findByMemberId(String memberId) throws SQLException {
        String sql = "SELECT * FROM use_log WHERE member_id = ?";
        List<Log> logs = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapToLog(resultSet));
                }
            }
        }

        return logs;
    }
//login이 드러가있는 log_id에 logout 시간을 기록
    @Override
    public void updateLogoutTime(int logId) throws SQLException {
        String sql = "UPDATE use_log SET logout_time = CURRENT_TIMESTAMP WHERE log_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, logId);
            statement.executeUpdate();
        }
    }

    @Override
    public void update(Log log) throws SQLException {
        String sql = "UPDATE use_log SET pc_cafe_id = ?, seat_num = ?, member_id = ?, login_time = ?, logout_time = ? WHERE log_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, log.getPcCafeId());
            statement.setInt(2, log.getSeatNum());
            statement.setString(3, log.getMemberId());
            statement.setTimestamp(4, toTimestamp(log.getLoginTime()));
            statement.setTimestamp(5, toTimestamp(log.getLogoutTime()));
            statement.setInt(6, log.getLogId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteById(int logId) throws SQLException {
        String sql = "DELETE FROM use_log WHERE log_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, logId);
            statement.executeUpdate();
        }
    }

    private Log mapToLog(ResultSet resultSet) throws SQLException {
        return new Log(
                resultSet.getInt("log_id"),
                resultSet.getString("pc_cafe_id"),
                resultSet.getInt("seat_num"),
                resultSet.getString("member_id"),
                toLocalDateTime(resultSet.getTimestamp("login_time")),
                toLocalDateTime(resultSet.getTimestamp("logout_time"))
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }

    private Timestamp toTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }

        return Timestamp.valueOf(localDateTime);
    }
}

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class LogDAOImpl implements LogDAO {
	private final Connection conn;

	public LogDAOImpl(Connection conn) {
	    this.conn = conn;
	}
	
    //log_id는 자동 increment 때문에 따로 insert X
	@Override
	public int insert(Log log) throws SQLException {
	    String sql = "INSERT INTO use_log (pc_cafe_id, seat_num, member_id) VALUES (?, ?, ?)";

	    try (PreparedStatement statement =
	                 conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
    
	//손님 의 로그 기록을 얻기 위한 함수 
    @Override
    public Log findLatestLogoutLog(String pcCafeId, int seatNum) {
        String sql = """
            SELECT log_id, pc_cafe_id, seat_num, member_id, login_time, logout_time
            FROM use_log
            WHERE pc_cafe_id = ?
              AND seat_num = ?
              AND logout_time IS NOT NULL
            ORDER BY log_id DESC
            LIMIT 1
        """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);
            pstmt.setInt(2, seatNum);

            try (ResultSet rs = pstmt.executeQuery()) {
            	if (rs.next()) {
            	    return mapToLog(rs);
            	}
            }

        } catch (SQLException e) {
            throw new RuntimeException("최근 퇴실 로그 조회 실패", e);
        }

        return null;
    }

    @Override
    public Log findById(int logId) throws SQLException {
        String sql = "SELECT * FROM use_log WHERE log_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
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

        try (PreparedStatement statement = conn.prepareStatement(sql);
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

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
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

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    logs.add(mapToLog(resultSet));
                }
            }
        }

        return logs;
    }
 // login이 들어가 있고 아직 logout_time이 없는 최신 로그에 logout 시간을 기록
    @Override
    public void updateLogoutTime(String pcCafeId, int seatNum) throws SQLException {
        String sql = """
            UPDATE use_log
            SET logout_time = CURRENT_TIMESTAMP
            WHERE pc_cafe_id = ?
              AND seat_num = ?
              AND logout_time IS NULL
            ORDER BY log_id DESC
            LIMIT 1
        """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, pcCafeId);
            statement.setInt(2, seatNum);

            statement.executeUpdate();
        }
    }

    @Override
    public void update(Log log) throws SQLException {
        String sql = "UPDATE use_log SET pc_cafe_id = ?, seat_num = ?, member_id = ?, login_time = ?, logout_time = ? WHERE log_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
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

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, logId);
            statement.executeUpdate();
        }
    }
    
    // 연별, 월별, 일별 가능한 해당 PC방의  시간 대 별 입장 손님 수 
    // 예시: 일별 findCustomerEntryCounts(pcCafeId, "DAY", 2026, 6, 2);
    // 월별:findCustomerEntryCounts(pcCafeId, "MONTH", 2026, 6, 0);
    // 년별: findCustomerEntryCounts(pcCafeId, "YEAR", 2026, 0, 0);
    @Override
    public Map<String, Integer> findCustomerEntryCounts(String pcCafeId, String periodType,int year,int month,int date) {
        Map<String, Integer> result = new LinkedHashMap<>();

        String sql =
            "SELECT DATE_FORMAT(login_time, '%H') AS timeSlot, " +
            "       COUNT(*) AS customerCount " +
            "FROM use_log " +
            "WHERE pc_cafe_id = ? " +
            "  AND login_time IS NOT NULL " +
            "  AND YEAR(login_time) = ? ";

        if ("MONTH".equals(periodType) || "DAY".equals(periodType)) {
            sql += "  AND MONTH(login_time) = ? ";
        }

        if ("DAY".equals(periodType)) {
            sql += "  AND DAY(login_time) = ? ";
        }

        sql +=
            "GROUP BY DATE_FORMAT(login_time, '%H') " +
            "ORDER BY timeSlot ASC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            int index = 1;

            pstmt.setString(index++, pcCafeId);
            pstmt.setInt(index++, year);

            if ("MONTH".equals(periodType) || "DAY".equals(periodType)) {
                pstmt.setInt(index++, month);
            }

            if ("DAY".equals(periodType)) {
                pstmt.setInt(index++, date);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.put(
                        rs.getString("timeSlot"),
                        rs.getInt("customerCount")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("시간대별 손님 입장 수 조회 중 오류 발생");
        }

        return result;
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

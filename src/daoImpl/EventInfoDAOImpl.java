package daoImpl;

import model.EventInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.EventInfoDAO;

public class EventInfoDAOImpl implements EventInfoDAO {
	
	private final Connection conn;

	public EventInfoDAOImpl(Connection conn) {
	    this.conn = conn;
	}
	
	
	
    @Override
    public void insert(EventInfo eventInfo) throws SQLException {
        // 이벤트 정보 1개 추가 / Insert one event info row.
    	   String sql = """
    	            INSERT INTO event_info
    	            (event_type, event_content, event_type_num, payment_rate)
    	            VALUES (?, ?, ?, ?)
    	            """;

    	    try (PreparedStatement statement = conn.prepareStatement(sql)) {

    	        statement.setString(1, eventInfo.getEventType());
    	        statement.setString(2, eventInfo.getEventContent());
    	        statement.setInt(3, eventInfo.getEventTypeNum());
    	        statement.setDouble(4, eventInfo.getPaymentRate());

    	        statement.executeUpdate();
    	    }
    }

    @Override
    public EventInfo findByType(String eventType) throws SQLException {
        // 이벤트 종류로 이벤트 정보 조회 / Select one event info by event type.
        String sql = "SELECT * FROM event_info WHERE event_type = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)
             ) {
            statement.setString(1, eventType);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEventInfo(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<EventInfo> findAll() throws SQLException {
        // 이벤트 정보 전체 조회 / Select every event info row.
        String sql = "SELECT * FROM event_info";
        List<EventInfo> eventInfos = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                eventInfos.add(mapToEventInfo(resultSet));
            }
        }

        return eventInfos;
    }

    @Override
    public void update(EventInfo eventInfo) throws SQLException {
        String sql = """
                UPDATE event_info
                SET event_content = ?,
                    event_type_num = ?,
                    payment_rate = ?
                WHERE event_type = ?
                """;

        try (PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, eventInfo.getEventContent());
            statement.setInt(2, eventInfo.getEventTypeNum());
            statement.setDouble(3, eventInfo.getPaymentRate());
            statement.setString(4, eventInfo.getEventType());

            statement.executeUpdate();
        }
    }

    @Override
    public void deleteByType(String eventType) throws SQLException {
        // 이벤트 종류로 이벤트 정보 삭제 / Delete one event info by event type.
        String sql = "DELETE FROM event_info WHERE event_type = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, eventType);
            statement.executeUpdate();
        }
    }

 // 조회 결과 한 줄을 EventInfo 객체로 변환 / Convert one ResultSet row into an EventInfo object.
    private EventInfo mapToEventInfo(ResultSet resultSet) throws SQLException {
        return new EventInfo(
                resultSet.getString("event_type"),
                resultSet.getString("event_content"),
                resultSet.getInt("event_type_num"),
                resultSet.getDouble("payment_rate")
        );
    }
}

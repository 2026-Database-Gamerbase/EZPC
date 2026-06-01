package daoImpl;

import db.DatabaseConnector;

import model.EventSchedule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.EventScheduleDAO;

public class EventScheduleDAOImpl implements EventScheduleDAO {
	private final Connection conn;

	public EventScheduleDAOImpl(Connection conn) {
	    this.conn = conn;
	}
	
    @Override
    public void insert(EventSchedule eventSchedule) throws SQLException {
        // 이벤트 일정 1개 추가 / Insert one event schedule row.
        String sql = "INSERT INTO event_schedule (event_type, pc_cafe_id, event_start_date, event_end_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)
             ) {
            statement.setString(1, eventSchedule.getEventType());
            statement.setString(2, eventSchedule.getPcId());
            statement.setDate(3, Date.valueOf(eventSchedule.getEventStartDate()));
            statement.setDate(4, Date.valueOf(eventSchedule.getEventEndDate()));
            statement.executeUpdate();
        }
    }

    @Override
    public EventSchedule findById(String eventType, String pcId, LocalDate eventStartDate) throws SQLException {
        //복합 기본키로 이벤트 일정 조회 / Select one event schedule by composite primary key.
        String sql = "SELECT * FROM event_schedule WHERE event_type = ? AND pc_cafe_id = ? AND event_start_date = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)
             ) {
            statement.setString(1, eventType);
            statement.setString(2, pcId);
            statement.setDate(3, Date.valueOf(eventStartDate));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEventSchedule(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<EventSchedule> findAll() throws SQLException {
        //이벤트 일정 전체 조회 / Select every event schedule row.
        String sql = "SELECT * FROM event_schedule";
        List<EventSchedule> eventSchedules = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                eventSchedules.add(mapToEventSchedule(resultSet));
            }
        }

        return eventSchedules;
    }
    

@Override
public double findCurrentOrderPaymentRate(String pcCafeId) throws SQLException {
	//해당 pc 방의 이벤트 결제 비율 확인 
	//CURDATE() BETWEEN es.event_start_date AND es.event_end_date -> 현재 날짜를 이벤트 날짜와 비교 
	
    String sql = """
        SELECT ei.payment_rate
        FROM event_schedule es
        JOIN event_info ei
            ON es.event_type = ei.event_type
        WHERE es.pc_cafe_id = ?
          AND ei.event_type_num = 0
          AND CURDATE() BETWEEN es.event_start_date AND es.event_end_date
        ORDER BY ei.payment_rate ASC
        LIMIT 1
    """;

    try (PreparedStatement statement = conn.prepareStatement(sql)
             ) {

        statement.setString(1, pcCafeId);

        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("payment_rate"); //payment_rate 반환
            }
        }
    }

    return 1.00; //default는 1
}


    @Override
    public List<EventSchedule> findByPcId(String pcId) throws SQLException {
        //특정 PC방의 이벤트 일정 조회 / Select event schedules for one pc cafe.
        String sql = "SELECT * FROM event_schedule WHERE pc_cafe_id = ?";
        List<EventSchedule> eventSchedules = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pcId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    eventSchedules.add(mapToEventSchedule(resultSet));
                }
            }
        }

        return eventSchedules;
    }

    @Override
    public void update(EventSchedule eventSchedule) throws SQLException {
        // 복합 기본키로 이벤트 종료일 수정/ Update event end date by composite primary key.
        String sql = "UPDATE event_schedule SET event_end_date = ? WHERE event_type = ? AND pc_cafe_id = ? AND event_start_date = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, Date.valueOf(eventSchedule.getEventEndDate()));
            statement.setString(2, eventSchedule.getEventType());
            statement.setString(3, eventSchedule.getPcId());
            statement.setDate(4, Date.valueOf(eventSchedule.getEventStartDate()));
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteById(String eventType, String pcId, LocalDate eventStartDate) throws SQLException {
        // 복합 기본키로 이벤트 일정 삭제 / Delete one event schedule by composite primary key.
        String sql = "DELETE FROM event_schedule WHERE event_type = ? AND pc_cafe_id = ? AND event_start_date = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)
             ) {
            statement.setString(1, eventType);
            statement.setString(2, pcId);
            statement.setDate(3, Date.valueOf(eventStartDate));
            statement.executeUpdate();
        }
    }

    //EventSchedule 객체로 변환/ Convert one ResultSet row into an EventSchedule object.
    private EventSchedule mapToEventSchedule(ResultSet resultSet) throws SQLException {
        return new EventSchedule(
                resultSet.getString("event_type"),
                resultSet.getString("pc_cafe_id"),
                resultSet.getDate("event_start_date").toLocalDate(),
                resultSet.getDate("event_end_date").toLocalDate()
        );
    }
}


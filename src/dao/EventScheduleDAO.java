package dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import model.EventSchedule;

public interface EventScheduleDAO {
    void insert(EventSchedule eventSchedule) throws SQLException;

    EventSchedule findById(String eventType, String pcId, LocalDate eventStartDate) throws SQLException;

    List<EventSchedule> findAll() throws SQLException;

    List<EventSchedule> findByPcId(String pcId) throws SQLException;
    
    double findCurrentOrderPaymentRate(String pcCafeId) throws SQLException;

    void update(EventSchedule eventSchedule) throws SQLException;

    void deleteById(String eventType, String pcId, LocalDate eventStartDate) throws SQLException;
}

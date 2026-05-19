package service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import dao.EventScheduleDAO;
import dao.EventScheduleDAOImpl;
import model.EventSchedule;

public class EventScheduleService {
    private final EventScheduleDAO eventScheduleDAO;

    public EventScheduleService() {
        this(new EventScheduleDAOImpl());
    }

    public EventScheduleService(EventScheduleDAO eventScheduleDAO) {
        this.eventScheduleDAO = eventScheduleDAO;
    }

    public void insertEventSchedule(EventSchedule eventSchedule) throws SQLException {
        // DAO로 보내기 전 이벤트 기간 확인 / Check event period before sending to DAO.
        validateEventPeriod(eventSchedule.getEventStartDate(), eventSchedule.getEventEndDate());
        eventScheduleDAO.insert(eventSchedule);
    }

    public EventSchedule getEventSchedule(String eventType, String pcId, LocalDate eventStartDate) throws SQLException {
        return eventScheduleDAO.findById(eventType, pcId, eventStartDate);
    }

    public List<EventSchedule> getAllEventSchedules() throws SQLException {
        return eventScheduleDAO.findAll();
    }

    public List<EventSchedule> getEventSchedulesByPc(String pcId) throws SQLException {
        return eventScheduleDAO.findByPcId(pcId);
    }

    public void updateEventSchedule(EventSchedule eventSchedule) throws SQLException {
        validateEventPeriod(eventSchedule.getEventStartDate(), eventSchedule.getEventEndDate());
        eventScheduleDAO.update(eventSchedule);
    }

    public void deleteEventSchedule(String eventType, String pcId, LocalDate eventStartDate) throws SQLException {
        eventScheduleDAO.deleteById(eventType, pcId, eventStartDate);
    }

    private void validateEventPeriod(LocalDate startDate, LocalDate endDate) {
        // 종료일은 시작일보다 빠를 수 없음 / End date cannot be before start date.
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Event end date cannot be before start date.");
        }
    }
}

package service;

import java.sql.SQLException;
import java.util.List;

import dao.EventInfoDAO;
import dao.EventInfoDAOImpl;
import model.EventInfo;

public class EventInfoService {
    // 이벤트 정보 관련 비즈니스 로직 담당 / Handles event info business logic.
    private final EventInfoDAO eventInfoDAO;

    public EventInfoService() {
        this(new EventInfoDAOImpl());
    }

    public EventInfoService(EventInfoDAO eventInfoDAO) {
        this.eventInfoDAO = eventInfoDAO;
    }

    public void insertEventInfo(EventInfo eventInfo) throws SQLException {
        eventInfoDAO.insert(eventInfo);
    }

    public EventInfo getEventInfo(String eventType) throws SQLException {
        return eventInfoDAO.findByType(eventType);
    }

    public List<EventInfo> getAllEventInfos() throws SQLException {
        return eventInfoDAO.findAll();
    }

    public void updateEventInfo(EventInfo eventInfo) throws SQLException {
        eventInfoDAO.update(eventInfo);
    }

    public void deleteEventInfo(String eventType) throws SQLException {
        eventInfoDAO.deleteByType(eventType);
    }
}

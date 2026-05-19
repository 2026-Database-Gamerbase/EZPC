package eventinfo;

import java.sql.SQLException;
import java.util.List;

public interface EventInfoDAO {
    void insert(EventInfo eventInfo) throws SQLException;

    EventInfo findByType(String eventType) throws SQLException;

    List<EventInfo> findAll() throws SQLException;

    void update(EventInfo eventInfo) throws SQLException;

    void deleteByType(String eventType) throws SQLException;
}

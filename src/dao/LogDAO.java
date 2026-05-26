package dao;

import java.sql.SQLException;
import java.util.List;
import model.Log;

public interface LogDAO {
    int insert(Log log) throws SQLException;

    Log findById(int logId) throws SQLException;

    List<Log> findAll() throws SQLException;

    List<Log> findByPcCafeId(String pcCafeId) throws SQLException;

    List<Log> findByMemberId(String memberId) throws SQLException;

    void updateLogoutTime(int logId) throws SQLException;

    void update(Log log) throws SQLException;

    void deleteById(int logId) throws SQLException;
}

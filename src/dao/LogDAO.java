package dao;

import java.sql.SQLException;

import java.util.List;
import java.util.Map;

import daoImpl.LogDAOImpl;
import model.Log;

public interface LogDAO {
	
    int insert(Log log) throws SQLException;

    Log findById(int logId) throws SQLException;

    List<Log> findAll() throws SQLException;
    
    Log findLatestLogoutLog(String pcCafeId, int seatNum);
   
    List<Log> findByPcCafeId(String pcCafeId) throws SQLException;

    List<Log> findByMemberId(String memberId) throws SQLException;

    void updateLogoutTime(String pcCafeId, int seatNum) throws SQLException;

    void update(Log log) throws SQLException;

    void deleteById(int logId) throws SQLException;
    
    Map<String, Integer> findMonthlyCustomerCounts(String pcCafeId, int year);
    
    public Map<String, Integer> findCustomerHourlyEntryCounts(String pcCafeId,String periodType,int year,int month,int date);
}

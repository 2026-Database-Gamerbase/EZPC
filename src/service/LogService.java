package service;



import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import dao.LogDAO;
import daoImpl.LogDAOImpl;
import model.Log;




public class LogService {
    private final LogDAO logDAO;


    public LogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    public int insertLog(Log log) throws SQLException {
        return logDAO.insert(log);
    }

    public Log getLog(int logId) throws SQLException {
        return logDAO.findById(logId);
    }

    public List<Log> getAllLogs() throws SQLException {
        return logDAO.findAll();
    }

    public List<Log> getLogsByPcCafe(String pcCafeId) throws SQLException {
        return logDAO.findByPcCafeId(pcCafeId);
    }

    public List<Log> getLogsByMember(String memberId) throws SQLException {
        return logDAO.findByMemberId(memberId);
    }

    public void updateLogoutTime(String pcCafeId, int seatNum) throws SQLException {
        logDAO.updateLogoutTime(pcCafeId,seatNum);
    }

    public void updateLog(Log log) throws SQLException {
        logDAO.update(log);
    }

    public void deleteLog(int logId) throws SQLException {
        logDAO.deleteById(logId);
    }
    
    public Map<String, Integer> findMonthlyCustomerCounts(String pcCafeId, int year) {
        return logDAO.findMonthlyCustomerCounts(pcCafeId, year);
    }
    
    
    public Map<String, Integer> findCustomerEntryCounts(String pcCafeId,String periodType,int year,int month,int date) {

        return logDAO.findCustomerHourlyEntryCounts(pcCafeId, periodType, year, month, date);
    }
}

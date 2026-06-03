package daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.SalesReportDAO;
import model.SalesReport;
import model.MonthlySalesReport;
import model.PeakTimeSalesReport;
import model.EventSalesReport;

public class SalesReportDAOImpl implements SalesReportDAO {
    
    private Connection conn;

    public SalesReportDAOImpl(Connection conn) {
        this.conn = conn;
    }

    // 특정 지점의 전체 매출 내역 조회 (UNION ALL)
    @Override
    public List<SalesReport> findAllSales(String pcCafeId) {
        List<SalesReport> list = new ArrayList<>();
        
        String sql = 
            "SELECT 'FOOD' AS salesType, pc_cafe_id AS pcCafeId, seat_num AS seatNum, food_name AS itemName, food_pay_amount AS payAmount, ordered_at AS salesDate " +
            "FROM food_order WHERE pc_cafe_id = ? " +
            "UNION ALL " +
            "SELECT 'CHARGE' AS salesType, pc_cafe_id AS pcCafeId, seat_num AS seatNum, CONCAT('PC ', ticket_time, '분') AS itemName, charge_pay_amount AS payAmount, charged_at AS salesDate " +
            "FROM charge WHERE pc_cafe_id = ? " +
            "ORDER BY salesDate DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);
            pstmt.setString(2, pcCafeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SalesReport report = new SalesReport();
                    report.setSalesType(rs.getString("salesType"));
                    report.setPcCafeId(rs.getString("pcCafeId"));
                    report.setSeatNum(rs.getInt("seatNum"));
                    report.setItemName(rs.getString("itemName"));
                    report.setPayAmount(rs.getInt("payAmount"));
                    report.setSalesDate(rs.getTimestamp("salesDate"));
                    list.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("전체 매출 내역 조회 중 오류 발생");
        }
        return list;
    }

    // 특정 지점의 총 매출액
    @Override
    public long getTotalSalesAmount(String pcCafeId) {
        long totalAmount = 0;
        
        String sql = 
            "SELECT SUM(payAmount) AS total " +
            "FROM (" +
            "    SELECT food_pay_amount AS payAmount FROM food_order WHERE pc_cafe_id = ? " +
            "    UNION ALL " +
            "    SELECT charge_pay_amount AS payAmount FROM charge WHERE pc_cafe_id = ?" +
            ") AS UnifiedSales";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);
            pstmt.setString(2, pcCafeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalAmount = rs.getLong("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("총 매출액 조회 중 오류 발생");
        }
        return totalAmount;
    }

    // 월별 매출 리포트 및 부진 지점 색출 (전월 대비 매출이 10% 이상 감소한 경우를 부진으로 판단)
    @Override
    public List<MonthlySalesReport> findMonthlyReport(String pcCafeId) {
        List<MonthlySalesReport> list = new ArrayList<>();
        
        // CTE 내부에서 파라미터를 받아 지점별로 미리 필터링해 대용량 풀스캔 방지
        String sql = 
            "WITH UnifiedSales AS (" +
            "    SELECT pc_cafe_id, food_pay_amount AS payAmount, ordered_at AS salesDate " +
            "    FROM food_order WHERE pc_cafe_id = ? " +
            "    UNION ALL " +
            "    SELECT pc_cafe_id, charge_pay_amount AS payAmount, charged_at AS salesDate " +
            "    FROM charge WHERE pc_cafe_id = ? " +
            "), " +
            "MonthlySum AS (" +
            "    SELECT DATE_FORMAT(salesDate, '%Y-%m') AS yearMonth, SUM(payAmount) AS totalSales " +
            "    FROM UnifiedSales GROUP BY DATE_FORMAT(salesDate, '%Y-%m')" +
            ") " +
            "SELECT yearMonth, totalSales, " +
            "       LAG(totalSales) OVER (ORDER BY yearMonth) AS prevMonthSales, " +
            "       " +
            "       ROUND((totalSales - LAG(totalSales) OVER (ORDER BY yearMonth)) / NULLIF(LAG(totalSales) OVER (ORDER BY yearMonth), 0) * 100, 2) AS growthRate, " +
            "       " +
            "       CASE " +
            "           WHEN LAG(totalSales) OVER (ORDER BY yearMonth) IS NULL THEN '신규 입점' " +
            "           WHEN LAG(totalSales) OVER (ORDER BY yearMonth) = 0 THEN '신규 입점' " +
            "           WHEN (totalSales - LAG(totalSales) OVER (ORDER BY yearMonth)) / NULLIF(LAG(totalSales) OVER (ORDER BY yearMonth), 0) <= -0.1 THEN '부진' " +
            "           ELSE '정상' " +
            "       END AS status " +
            "FROM MonthlySum";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);
            pstmt.setString(2, pcCafeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	MonthlySalesReport report = new MonthlySalesReport();
                    report.setYearMonth(rs.getString("yearMonth"));
                    report.setTotalSales(rs.getInt("totalSales"));
                    report.setPrevMonthSales(rs.getInt("prevMonthSales"));
                    report.setGrowthRate(rs.getDouble("growthRate"));
                    report.setStatus(rs.getString("status"));
                    list.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("월별 매출 리포트 조회 중 오류 발생");
        }
        return list;
    }

    // 시간대별 피크 매출 리포트 (최근 한 달 기준, 순수 매출액 중심)
    @Override
    public List<PeakTimeSalesReport> findPeakTimeSales(String pcCafeId) {
        List<PeakTimeSalesReport> list = new ArrayList<>();
        
        // CTE 내부에서 미리 pc_cafe_id 및 날짜 조건으로 필터링하여 성능 저하 방지
        String sql = 
            "WITH UnifiedSales AS (" +
            "    SELECT pc_cafe_id, food_pay_amount AS payAmount, ordered_at AS salesDate " +
            "    FROM food_order " +
            "    WHERE pc_cafe_id = ? AND ordered_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
            "    UNION ALL " +
            "    SELECT pc_cafe_id, charge_pay_amount AS payAmount, charged_at AS salesDate " +
            "    FROM charge " +
            "    WHERE pc_cafe_id = ? AND charged_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
            ") " +
            "SELECT DATE_FORMAT(salesDate, '%H') AS timeSlot, " +
            "       SUM(payAmount) AS totalSales " +
            "FROM UnifiedSales " +
            "GROUP BY DATE_FORMAT(salesDate, '%H') " +
            "ORDER BY totalSales DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);
            pstmt.setString(2, pcCafeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	PeakTimeSalesReport report = new PeakTimeSalesReport();
                    report.setTimeSlot(rs.getString("timeSlot"));
                    report.setTotalSales(rs.getInt("totalSales"));
                    list.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("시간대별 피크 매출 조회 중 오류 발생");
        }
        return list;
    }

    // 이벤트 기간 vs 직전 동일 기간 성과 비교 (음식/요금 매출 분리 및 텍스트 그래프용 데이터 집계)
    @Override
    public List<EventSalesReport> compareEventSales(String pcCafeId, String eventStartDate, String eventEndDate) {
        List<EventSalesReport> list = new ArrayList<>();
        
        // CTE 내부에 WHERE 조건을 지정하여 테이블 전체 데이터를 가공하지 않도록 인덱스 타게 유도
        String sql = 
        		"WITH UnifiedSales AS (" +
        	    "    SELECT pc_cafe_id, 'FOOD' AS type, food_pay_amount AS payAmount, ordered_at AS salesDate " +
        	    "    FROM food_order " +
        	    "    WHERE pc_cafe_id = ? " +
        	    "    UNION ALL " +
        	    "    SELECT pc_cafe_id, 'CHARGE' AS type, charge_pay_amount AS payAmount, charged_at AS salesDate " +
        	    "    FROM charge " +
        	    "    WHERE pc_cafe_id = ? " +
        	    "), " +
        	    "DateRange AS (" +
        	    "    SELECT STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s') AS eStart, " +
        	    "           STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s') AS eEnd, " +
        	    "           DATEDIFF(STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s'), STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s')) + 1 AS duration " +
        	    ") " +
        		"SELECT " +
        		"    '이벤트기간' AS periodType, " +
        		"    SUM(CASE WHEN type = 'FOOD' THEN payAmount ELSE 0 END) AS foodSales, " +
        		"    SUM(CASE WHEN type = 'CHARGE' THEN payAmount ELSE 0 END) AS chargeSales, " +
        		"    SUM(payAmount) AS totalSales, " +
        		"    1 AS sortOrder " + 
        		"FROM UnifiedSales, DateRange " +
        		"WHERE salesDate BETWEEN eStart AND eEnd " +
        		"UNION ALL " +
        		"SELECT " +
        		"    '직전기간' AS periodType, " + 
        		"    SUM(CASE WHEN type = 'FOOD' THEN payAmount ELSE 0 END) AS foodSales, " +
        		"    SUM(CASE WHEN type = 'CHARGE' THEN payAmount ELSE 0 END) AS chargeSales, " +
        		"    SUM(payAmount) AS totalSales, " +
        		"    2 AS sortOrder " + 
        		"FROM UnifiedSales, DateRange " +
        		"WHERE salesDate BETWEEN DATE_SUB(eStart, INTERVAL duration DAY) AND DATE_SUB(eStart, INTERVAL 1 SECOND) " +
        		"ORDER BY sortOrder ASC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String startStr = eventStartDate + " 00:00:00";
            String endStr = eventEndDate + " 23:59:59";
            
            pstmt.setString(1, pcCafeId);
            pstmt.setString(2, pcCafeId);
            pstmt.setString(3, startStr);
            pstmt.setString(4, endStr);
            pstmt.setString(5, endStr);
            pstmt.setString(6, startStr);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	EventSalesReport report = new EventSalesReport();
                    report.setPeriodType(rs.getString("periodType"));
                    report.setFoodSales(rs.getInt("foodSales"));
                    report.setChargeSales(rs.getInt("chargeSales"));
                    report.setTotalSales(rs.getInt("totalSales"));
                    list.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("이벤트 성과 분석 조회 중 오류 발생");
        }
        return list;
    }
}
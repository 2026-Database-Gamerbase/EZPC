package dao;

import java.util.List;
import model.SalesReport;
import model.MonthlySalesReport;
import model.PeakTimeSalesReport;
import model.EventSalesReport;

public interface SalesReportDAO {
	
    // 특정 지점의 전체 매출 내역 조회
    List<SalesReport> findAllSales(String pcCafeId);
    
    // 특정 지점의 총 매출액
    long getTotalSalesAmount(String pcCafeId);

    
    // ---------------------------------------------------------
    // TODO: 고급 SQL 메서드를 UI 설계에서 어디에 어떻게 연결할지 결정
    // ---------------------------------------------------------
    
    // 월별 매출 리포트 및 부진 지점 색출
    List<MonthlySalesReport> findMonthlyReport(String pcCafeId);
    
    // 이벤트 전/후 매출 성과 비교
    List<EventSalesReport> compareEventSales(String pcCafeId, String eventStartDate, String eventEndDate);
    
    // 시간대별 피크 매출 리포트
    List<PeakTimeSalesReport> findPeakTimeSales(String pcCafeId);

}
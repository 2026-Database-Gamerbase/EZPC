package service;

import dao.SalesReportDAO;
import model.SalesReport;
import model.MonthlySalesReport;
import model.PeakTimeSalesReport;
import model.EventSalesReport;
import java.util.List;

public class SalesReportService {
    
    private final SalesReportDAO salesReportDAO;

    public SalesReportService(SalesReportDAO salesReportDAO) {
        this.salesReportDAO = salesReportDAO;
    }

    /**
     특정 지점의 전체 매출 내역 조회
     
     @param pcCafeId PC방 지점 ID
     @return 지점 전체 매출 리스트
     
     */
    public List<SalesReport> getBranchAllSales(String pcCafeId) {
        if (pcCafeId == null || pcCafeId.trim().isEmpty()) {
            System.err.println("[getBranchAllSales]: 조회 실패 - 지점 ID 누락");
            throw new IllegalArgumentException("유효한 PC방 지점 ID를 입력해 주세요.");
        }
        return salesReportDAO.findAllSales(pcCafeId);
    }

    /**
     특정 지점의 총 매출액 조회
     
     @param pcCafeId PC방 지점 ID
     @return 총 매출액 (long)
     
     */
    public long getBranchTotalSalesAmount(String pcCafeId) {
        if (pcCafeId == null || pcCafeId.trim().isEmpty()) {
            System.err.println("[getBranchTotalSalesAmount]: 조회 실패 - 지점 ID 누락");
            throw new IllegalArgumentException("유효한 PC방 지점 ID를 입력해 주세요.");
        }
        return salesReportDAO.getTotalSalesAmount(pcCafeId);
    }

    /**
     월별 매출 리포트 및 부진 지점 색출
     
     @param pcCafeId PC방 지점 ID
     @return 월별 매출 통계 및 상태 리스트
     
     */
    public List<MonthlySalesReport> getMonthlySalesAnalysis(String pcCafeId) {
        if (pcCafeId == null || pcCafeId.trim().isEmpty()) {
            System.err.println("[getMonthlySalesAnalysis]: 리포트 생성 실패 - 지점 ID 누락");
            throw new IllegalArgumentException("통계를 조회할 PC방 지점 ID를 입력해 주세요.");
        }
        return salesReportDAO.findMonthlyReport(pcCafeId);
    }

    /**
     시간대별 피크 매출 리포트
     
     @param pcCafeId PC방 지점 ID
     @return 시간대별 통계 리스트
     
     */
    public List<PeakTimeSalesReport> getPeakTimeAnalysis(String pcCafeId) {
        if (pcCafeId == null || pcCafeId.trim().isEmpty()) {
            throw new IllegalArgumentException("피크 타임을 분석할 PC방 지점 ID를 입력해 주세요.");
        }
        return salesReportDAO.findPeakTimeSales(pcCafeId);
    }

    /**
     이벤트 전/후 성과 분석 (매출 비교)
     
     @param pcCafeId       PC방 지점 ID
     @param eventStartDate 이벤트 시작일 (예: "YYYY-MM-DD")
     @param eventEndDate   이벤트 종료일 (예: "YYYY-MM-DD")
     @return 이벤트 기간의 요약 매출과 직전 동일 기간의 요약 매출을 비교한 리스트 (2개의 데이터 행 반환)
     
     */
    public List<EventSalesReport> analyzeEventPerformance(String pcCafeId, String eventStartDate, String eventEndDate) {
        if (pcCafeId == null || pcCafeId.trim().isEmpty()) {
            System.err.println("[analyzeEventPerformance]: 성과 분석 실패 - 지점 ID 누락");
            throw new IllegalArgumentException("성과를 분석할 PC방 지점 ID를 입력해 주세요.");
        }
        if (eventStartDate == null || eventStartDate.trim().isEmpty() || 
            eventEndDate == null || eventEndDate.trim().isEmpty()) {
            System.err.println("[analyzeEventPerformance]: 성과 분석 실패 - 날짜 정보 누락 (시작일: " + eventStartDate + ", 종료일: " + eventEndDate + ")");
            throw new IllegalArgumentException("이벤트 시작일과 종료일을 모두 입력해 주세요.");
        }
        
        // 날짜 포맷 검증
        if (eventStartDate.length() != 10 || eventEndDate.length() != 10) {
            System.err.println("[analyzeEventPerformance]: 성과 분석 실패 - 잘못된 날짜 형식");
            throw new IllegalArgumentException("날짜는 'YYYY-MM-DD' 형식으로 입력해 주세요.");
        }

        List<EventSalesReport> result = salesReportDAO.compareEventSales(pcCafeId, eventStartDate, eventEndDate);

        // 성장률 계산: (이벤트기간 - 직전기간) / 직전기간 * 100
        // result[0] = 이벤트기간, result[1] = 직전기간 (DAO SQL ORDER BY sortOrder ASC 기준)
        if (result.size() == 2) {
            int eventTotal = result.get(0).getTotalSales();
            int prevTotal  = result.get(1).getTotalSales();
            if (prevTotal != 0) {
                double growthRate = Math.round(((double)(eventTotal - prevTotal) / prevTotal * 100) * 100.0) / 100.0;
                result.get(0).setGrowthRate(growthRate);
            }
            // 직전기간 행의 성장률은 기준이 없으므로 0.0 (기본값) 유지
        }

        return result;
    }
}
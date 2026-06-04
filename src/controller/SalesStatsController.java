package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

// 모델
import model.MonthlySalesReport;
import model.FoodRankingReport;
import model.PeakTimeSalesReport;
import model.EventSalesReport;
import model.EventSchedule;
import model.Food;
import model.PcCafe;

// 뷰 및 팝업 다이얼로그
import view.owner.OwnerSalesStatsView;
import view.owner.OwnerPeakTimeDialog;
import view.owner.OwnerUserTrendDialog;
import view.owner.OwnerEventAnalysisDialog;

// 서비스
import service.SalesReportService;
import service.OrderService;
import service.LogService;
import service.PcCafeService;
import service.EventScheduleService;

public class SalesStatsController {
    private final OwnerSalesStatsView view;
    private final JFrame parentFrame; 
    private final SalesReportService salesReportService;
    private final OrderService orderService;
    private final LogService logService;
    private final PcCafeService pcCafeService;
    private final EventScheduleService eventScheduleService;
    
    private String currentBranchId;

    public SalesStatsController(OwnerSalesStatsView view, JFrame parentFrame, 
                                SalesReportService salesReportService, OrderService orderService, 
                                LogService logService, PcCafeService pcCafeService, 
                                EventScheduleService eventScheduleService) {
        this.view = view;
        this.parentFrame = parentFrame;
        this.salesReportService = salesReportService;
        this.orderService = orderService;
        this.logService = logService;
        this.pcCafeService = pcCafeService;
        this.eventScheduleService = eventScheduleService;

        initEventBindings();
    }

    private void initEventBindings() {
        view.setFoodSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedFood = view.getSelectedPopularFood();
                if (selectedFood != null && !selectedFood.trim().isEmpty()) {
                    loadFoodRecommendations(selectedFood);
                }
            }
        });

        view.setPeakTimeButtonListener(e -> openPeakTimeAnalysisDialog());
        view.setUserTrendButtonListener(e -> openUserTrendAnalysisDialog());
        view.setEventAnalysisButtonListener(e -> openEventAnalysisDialog());
    }

    public void refreshSalesStats(String branchId) {
        this.currentBranchId = branchId;
        try {
            long totalSales = salesReportService.getBranchTotalSalesAmount(branchId);
            List<MonthlySalesReport> monthlyReport = salesReportService.getMonthlySalesAnalysis(branchId);
            
            Map<String, Integer> entryLogs = logService.findCustomerCountsByYearMonth(branchId);
            
            PcCafe pcCafe = pcCafeService.getPcCafe(branchId);
            double avgRating = (pcCafe != null) ? pcCafe.getAverageStarRating() : 0.0;
            
            view.updateStats(totalSales, avgRating);

            Object[][] salesData = new Object[monthlyReport.size()][5];
            for (int i = 0; i < monthlyReport.size(); i++) {
                MonthlySalesReport report = monthlyReport.get(i);
                salesData[i][0] = report.getYearMonth(); 
                salesData[i][1] = String.format("%,d원", report.getTotalSales());
                salesData[i][2] = entryLogs.getOrDefault(report.getYearMonth(), 0) + "명";
                salesData[i][3] = String.format("%.1f%%", report.getGrowthRate());
                salesData[i][4] = report.getStatus();
            }
            view.setSalesTableData(salesData);

            List<FoodRankingReport> foodRanking = orderService.getTop5FoodRankingByPcCafe(branchId);
            Object[][] foodData = new Object[foodRanking.size()][4];
            for (int i = 0; i < foodRanking.size(); i++) {
                FoodRankingReport rank = foodRanking.get(i);
                foodData[i][0] = rank.getFoodName();
                foodData[i][1] = rank.getTotalQuantity() + "개";
                foodData[i][2] = String.format("%,d원", rank.getTotalSales());
                foodData[i][3] = (i + 1) + "위";
            }
            view.setPopularFoodTableData(foodData);

        } catch (Exception e) {
            System.err.println("[SalesStatsController] 매출 통계 로드 실패");
            e.printStackTrace();
        }
    }

    private void loadFoodRecommendations(String foodName) {
        List<Food> recommendations = orderService.getFoodRecommendations(foodName);
        if (recommendations == null || recommendations.isEmpty()) {
            view.setRecommendationText("💡 [" + foodName + "] 상품은 아직 함께 주문된 연관 데이터가 부족합니다.");
            return;
        }

        StringBuilder sb = new StringBuilder(foodName + "의 인기 연관 메뉴: ");
        for (int i = 0; i < recommendations.size(); i++) {
            sb.append((i + 1)).append("위 ").append(recommendations.get(i).getFoodName());
            if (i < recommendations.size() - 1) sb.append(", ");
        }
        view.setRecommendationText(sb.toString());
    }

    private void openPeakTimeAnalysisDialog() {
        if (currentBranchId == null) {
            JOptionPane.showMessageDialog(parentFrame, "우측 상단에서 관리 지점을 먼저 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OwnerPeakTimeDialog dialog = new OwnerPeakTimeDialog(parentFrame);
        List<PeakTimeSalesReport> peakList = salesReportService.getPeakTimeAnalysis(currentBranchId);

        // 시간대별 손님 수 (올해 전체 기준 - 매출 집계 기간과 근사)
        LocalDate now = LocalDate.now();
        Map<String, Integer> visitorMap = logService.findCustomerEntryCounts(
                currentBranchId, "YEAR", now.getYear(), 0, 0);

        Object[][] dialogData = new Object[peakList.size()][3];
        for (int i = 0; i < peakList.size(); i++) {
            PeakTimeSalesReport p = peakList.get(i);
            String hour = p.getTimeSlot(); // "14"
            int visitors = visitorMap.getOrDefault(hour, 0);
            dialogData[i][0] = hour + "시 ~ " + (Integer.parseInt(hour) + 1) + "시";
            dialogData[i][1] = String.format("%,d원", p.getTotalSales());
            dialogData[i][2] = visitors > 0 ? visitors + "명" : "-";
        }
        dialog.setPeakTimeData(dialogData);
        dialog.setVisible(true);
    }

    private void openUserTrendAnalysisDialog() {
        if (currentBranchId == null) {
            JOptionPane.showMessageDialog(parentFrame, "우측 상단에서 관리 지점을 먼저 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        OwnerUserTrendDialog dialog = new OwnerUserTrendDialog(parentFrame);

        // 초기 조회 (현재 연도)
        dialog.setTrendData(buildTrendChart(currentBranchId, LocalDate.now().getYear()));

        // 조회 버튼 리스너
        dialog.setSearchButtonListener(e -> {
            int selectedYear = dialog.getSelectedYear();
            dialog.setTrendData(buildTrendChart(currentBranchId, selectedYear));
        });

        dialog.setVisible(true);
    }

    private String buildTrendChart(String branchId, int year) {
        Map<String, Integer> trendMap = logService.findMonthlyCustomerCounts(branchId, year);
        int maxCount = trendMap.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int barMax   = 30;

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append(String.format("  %d년 월별 이용자 수 추이\n", year));
        sb.append("==========================================\n");

        if (trendMap.isEmpty()) {
            sb.append("\n  조회된 방문자 데이터 로그가 존재하지 않습니다.\n");
        } else {
            for (Map.Entry<String, Integer> entry : trendMap.entrySet()) {
                int count     = entry.getValue();
                int barLength = (int)((double) count / maxCount * barMax);
                String bar    = "█".repeat(barLength);
                sb.append(String.format("  %s월 | %-30s %d명\n", entry.getKey(), bar, count));
            }
        }
        sb.append("==========================================\n");
        return sb.toString();
    }

    // 콤보박스에서 이벤트 선택 후 분석
    private void openEventAnalysisDialog() {
        if (currentBranchId == null) {
            JOptionPane.showMessageDialog(parentFrame, "우측 상단에서 관리 지점을 먼저 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // DB에서 해당 지점의 전체 이벤트 목록을 가져옴
            List<EventSchedule> eventList = eventScheduleService.getEventSchedulesByPc(currentBranchId);
            
            if (eventList == null || eventList.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "해당 지점에 진행되었거나 진행 중인 이벤트가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            OwnerEventAnalysisDialog dialog = new OwnerEventAnalysisDialog(parentFrame);
            
            // 콤보박스에 보여줄 텍스트 배열 생성 (예: "신규_가입_혜택 (2025-01-01 ~ 2025-01-31)")
            String[] comboItems = eventList.stream()
                .map(e -> e.getEventType() + " (" + e.getEventStartDate() + " ~ " + e.getEventEndDate() + ")")
                .toArray(String[]::new);
            
            dialog.setEventList(comboItems);
            
            // "분석하기" 버튼 리스너
            dialog.setAnalyzeButtonListener(evt -> {
                int selectedIdx = dialog.getSelectedEventIndex();
                if (selectedIdx < 0) return;
                
                EventSchedule selectedEvent = eventList.get(selectedIdx);
                String start = selectedEvent.getEventStartDate().toString();
                String end = selectedEvent.getEventEndDate().toString();
                
                try {
                    List<EventSalesReport> performanceList = salesReportService.analyzeEventPerformance(currentBranchId, start, end);

                    // performanceList[0] = 이벤트기간, [1] = 직전기간 (DAO sortOrder ASC 기준)
                    EventSalesReport eventReport = performanceList.size() > 0 ? performanceList.get(0) : new EventSalesReport();
                    EventSalesReport prevReport  = performanceList.size() > 1 ? performanceList.get(1) : new EventSalesReport();
                    dialog.showAnalysisResult(eventReport, prevReport, start, end);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "데이터 분석 도중 시스템 에러가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            });
            
            dialog.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, "이벤트 목록을 불러오는 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
package controller.owner;

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
            
            Map<String, Integer> entryLogs = logService.findCustomerEntryCounts(branchId, "ALL_MONTHS", 0, 0, 0);
            int totalUserCount = entryLogs.values().stream().mapToInt(Integer::intValue).sum();
            
            PcCafe pcCafe = pcCafeService.getPcCafe(branchId);
            double avgRating = (pcCafe != null) ? pcCafe.getAverageStarRating() : 0.0;
            
            view.updateStats(totalSales, totalUserCount, avgRating);

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
        
        Object[][] dialogData = new Object[peakList.size()][3];
        for (int i = 0; i < peakList.size(); i++) {
            PeakTimeSalesReport p = peakList.get(i);
            dialogData[i][0] = p.getTimeSlot() + "시 ~ " + (Integer.parseInt(p.getTimeSlot()) + 1) + "시";
            dialogData[i][1] = String.format("%,d원", p.getTotalSales());
            dialogData[i][2] = "-"; 
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
        LocalDate now = LocalDate.now();
        Map<String, Integer> trendMap = logService.findCustomerEntryCounts(currentBranchId, "MONTH", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        
        StringBuilder chartBuilder = new StringBuilder();
        chartBuilder.append("=========================================\n");
        chartBuilder.append(" 연월       방문객 수    시각 시각화 그래프\n");
        chartBuilder.append("=========================================\n");
        
        if (trendMap == null || trendMap.isEmpty()) {
            chartBuilder.append("\n  조회된 최근 방문자 데이터 로그가 존재하지 않습니다.\n");
        } else {
            for (Map.Entry<String, Integer> entry : trendMap.entrySet()) {
                String monthKey = entry.getKey();
                int count = entry.getValue();
                
                chartBuilder.append(String.format(" %-10s : %3d명    ", monthKey, count));
                int barLength = Math.min(count / 10, 20); 
                for (int i = 0; i < barLength; i++) {
                    chartBuilder.append("■");
                }
                if (barLength == 20) chartBuilder.append("+");
                chartBuilder.append("\n");
            }
        }
        chartBuilder.append("=========================================\n");
        
        dialog.setTrendData(chartBuilder.toString());
        dialog.setVisible(true);
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
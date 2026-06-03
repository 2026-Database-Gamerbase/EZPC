package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 매출 통계 탭 Panel
// 지점별 매출(리뷰 등급 포함, 부진 지점 하이라이트), 음식 판매 TOP 5, 
// 음식 연관 추천 기능을 제공하며, 고급 통계 분석 모달을 호출할 수 있도록 설계.
// ==========================================
public class OwnerSalesStatsView extends JPanel {
    private JLabel totalSalesLabel;
    private JLabel averageRate;
    
    private JTable salesTable;
    private JTable popularFoodTable;
    private DefaultTableModel salesTableModel;
    private DefaultTableModel foodTableModel;
    
    private JLabel recommendationLabel;
    private JButton btnPeakTime;
    private JButton btnUserTrend;
    private JButton btnEventAnalysis;

    public OwnerSalesStatsView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 전체 통계
        // ==========================================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 전체 통계 패널
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        statsPanel.setBackground(new Color(240, 240, 240));

        totalSalesLabel = createStatPanel(statsPanel, "총 매출", new Color(100, 150, 255));
        averageRate = createStatPanel(statsPanel, "평균 별점", new Color(100, 200, 100));

        topPanel.add(statsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 좌측(월별 매출 & 부진 지점) 및 우측(인기 음식 & 추천)
        // ==========================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(240, 240, 240));

        // 1. 좌측: 지점별 매출 테이블
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("지점별 매출 및 평가 리포트"));
        leftPanel.setBackground(new Color(240, 240, 240));

        String[] salesColumnNames = {"기준월", "당월 매출", "사용자 수", "전월비(%)", "지점 상태"};
        salesTableModel = new DefaultTableModel(salesColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        salesTable = new JTable(salesTableModel);
        salesTable.setFont(FontUtil.getKoreanFontPlain(12));
        salesTable.setRowHeight(25);
        salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String gradeInfo = (String) table.getModel().getValueAt(row, 4); // 리뷰 등급 컬럼 확인
                if (!isSelected) {
                    if (gradeInfo != null && (gradeInfo.contains("4등급") || gradeInfo.contains("부진"))) {
                        c.setBackground(new Color(255, 200, 200)); // 옅은 빨간색
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        
        leftPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // 2. 우측: 인기 음식 테이블 및 연관 추천
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("인기 음식 TOP 5 및 연관 추천"));
        rightPanel.setBackground(new Color(240, 240, 240));

        String[] foodColumnNames = {"음식명", "판매량", "매출액", "순위"};
        foodTableModel = new DefaultTableModel(foodColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        popularFoodTable = new JTable(foodTableModel);
        popularFoodTable.setFont(FontUtil.getKoreanFontPlain(12));
        popularFoodTable.setRowHeight(25);
        popularFoodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        rightPanel.add(new JScrollPane(popularFoodTable), BorderLayout.CENTER);

        // 하단 연관 추천 라벨 추가
        JPanel recommendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recommendPanel.setBackground(new Color(255, 250, 205)); // 옅은 노란색 배경
        recommendPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        recommendationLabel = new JLabel("표에서 음식을 선택하면 연관 추천 메뉴가 분석됩니다.");
        recommendationLabel.setFont(FontUtil.getKoreanFontBold(12));
        recommendPanel.add(recommendationLabel);
        rightPanel.add(recommendPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(550);

        add(splitPane, BorderLayout.CENTER);

        // ==========================================
        // 하단: 고급 통계 버튼 구역
        // ==========================================
        JPanel bottomStatsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomStatsPanel.setBackground(new Color(220, 220, 220));
        bottomStatsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        btnPeakTime = new JButton("시간대별 피크 분석");
        btnPeakTime.setFont(FontUtil.getKoreanFontPlain(12));
        btnUserTrend = new JButton("월별 이용자 추이");
        btnUserTrend.setFont(FontUtil.getKoreanFontPlain(12));
        btnEventAnalysis = new JButton("이벤트 성과 분석");
        btnEventAnalysis.setFont(FontUtil.getKoreanFontPlain(12));
        
        Dimension btnSize = new Dimension(150, 35);
        btnPeakTime.setPreferredSize(btnSize);
        btnUserTrend.setPreferredSize(btnSize);
        btnEventAnalysis.setPreferredSize(btnSize);

        bottomStatsPanel.add(btnPeakTime);
        bottomStatsPanel.add(btnUserTrend);
        bottomStatsPanel.add(btnEventAnalysis);

        add(bottomStatsPanel, BorderLayout.SOUTH);
    }

    // 통계 패널 UI 생성을 돕는 헬퍼 메서드
    private JLabel createStatPanel(JPanel parent, String title, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FontUtil.getKoreanFontBold(12));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel valueLabel = new JLabel("-");
        valueLabel.setFont(FontUtil.getKoreanFontBold(20));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        parent.add(panel);
        
        return valueLabel;
    }

    // ==========================================
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================

    // 테이블 내 선택된 음식명 가져오기 (연관 분석용)
    public String getSelectedPopularFood() {
        int row = popularFoodTable.getSelectedRow();
        if (row >= 0) {
            return (String) foodTableModel.getValueAt(row, 0);
        }
        return null;
    }

    // 연관 추천 결과 텍스트 업데이트
    public void setRecommendationText(String text) {
        recommendationLabel.setText(text);
    }

    public void setFoodSelectionListener(ListSelectionListener listener) {
        popularFoodTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void setPeakTimeButtonListener(ActionListener listener) {
        btnPeakTime.addActionListener(listener);
    }

    public void setUserTrendButtonListener(ActionListener listener) {
        btnUserTrend.addActionListener(listener);
    }

    public void setEventAnalysisButtonListener(ActionListener listener) {
        btnEventAnalysis.addActionListener(listener);
    }

    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    /**
     * 상단 전체 통계 정보를 갱신
     * @param totalSales 총 매출액
     * @param userCount 총 방문자 수
     * @param averageRating 지점 평균 별점
     */
    public void updateStats(long totalSales, double averageRating) {
        totalSalesLabel.setText(String.format("%,d원", totalSales));
        averageRate.setText(String.format("%.1f점", averageRating));
    }

    // 지점별 매출 테이블 데이터 주입
    public void setSalesTableData(Object[][] data) {
        salesTableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                salesTableModel.addRow(row);
            }
        }
    }

    // 인기 음식 테이블 데이터 주입
    public void setPopularFoodTableData(Object[][] data) {
        foodTableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                foodTableModel.addRow(row);
            }
        }
    }
}
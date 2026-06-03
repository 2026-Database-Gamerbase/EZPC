// 매출 통계 탭 (지점별 매출 조회, 월별 인기 음식 조회 등등)
package view.owner;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

public class OwnerSalesStatsView extends JPanel {
    private JComboBox<String> periodCombo;
    private JLabel totalSalesLabel;
    private JLabel userCountLabel;
    private JLabel averagePriceLabel;
    private JTable salesTable;
    private JTable popularFoodTable;
    private DefaultTableModel salesTableModel;
    private DefaultTableModel foodTableModel;

    public OwnerSalesStatsView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 필터 및 전체 통계
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 필터 패널
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(240, 240, 240));
        JLabel periodLabel = new JLabel("기간:");
        periodLabel.setFont(FontUtil.getKoreanFontPlain(12));
        // DB 연결 필요: 조회 기간 선택
        String[] periods = {"오늘", "이번 주", "이번 달", "3개월", "6개월", "1년"};
        periodCombo = new JComboBox<>(periods);
        filterPanel.add(periodLabel);
        filterPanel.add(periodCombo);
        topPanel.add(filterPanel, BorderLayout.WEST);

        // 전체 통계 패널
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        statsPanel.setBackground(new Color(240, 240, 240));

        // 총 매출
        JPanel totalSalesPanel = new JPanel(new BorderLayout());
        totalSalesPanel.setBackground(new Color(100, 150, 255));
        totalSalesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel totalSalesTextLabel = new JLabel("총 매출");
        totalSalesTextLabel.setFont(FontUtil.getKoreanFontBold(12));
        totalSalesTextLabel.setForeground(Color.WHITE);
        totalSalesLabel = new JLabel("1,234,560원"); // DB 연결 필요
        totalSalesLabel.setFont(FontUtil.getKoreanFontBold(20));
        totalSalesLabel.setForeground(Color.WHITE);
        totalSalesLabel.setHorizontalAlignment(JLabel.CENTER);
        totalSalesPanel.add(totalSalesTextLabel, BorderLayout.NORTH);
        totalSalesPanel.add(totalSalesLabel, BorderLayout.CENTER);
        statsPanel.add(totalSalesPanel);

        // 사용자 수
        JPanel userCountPanel = new JPanel(new BorderLayout());
        userCountPanel.setBackground(new Color(255, 140, 0));
        userCountPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel userCountTextLabel = new JLabel("사용자 수");
        userCountTextLabel.setFont(FontUtil.getKoreanFontBold(12));
        userCountTextLabel.setForeground(Color.WHITE);
        userCountLabel = new JLabel("456명"); // DB 연결 필요
        userCountLabel.setFont(FontUtil.getKoreanFontBold(20));
        userCountLabel.setForeground(Color.WHITE);
        userCountLabel.setHorizontalAlignment(JLabel.CENTER);
        userCountPanel.add(userCountTextLabel, BorderLayout.NORTH);
        userCountPanel.add(userCountLabel, BorderLayout.CENTER);
        statsPanel.add(userCountPanel);

        // 평균 결제액
        JPanel averagePanel = new JPanel(new BorderLayout());
        averagePanel.setBackground(new Color(100, 200, 100));
        averagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel averageTextLabel = new JLabel("평균 결제액");
        averageTextLabel.setFont(FontUtil.getKoreanFontBold(12));
        averageTextLabel.setForeground(Color.WHITE);
        averagePriceLabel = new JLabel("2,700원"); // DB 연결 필요
        averagePriceLabel.setFont(FontUtil.getKoreanFontBold(20));
        averagePriceLabel.setForeground(Color.WHITE);
        averagePriceLabel.setHorizontalAlignment(JLabel.CENTER);
        averagePanel.add(averageTextLabel, BorderLayout.NORTH);
        averagePanel.add(averagePriceLabel, BorderLayout.CENTER);
        statsPanel.add(averagePanel);

        topPanel.add(statsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 중앙: 좌측(지점별 매출) 및 우측(인기 음식)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(240, 240, 240));

        // 좌측: 지점별 매출 테이블
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("지점별 매출"));
        leftPanel.setBackground(new Color(240, 240, 240));

        // DB 연결 필요: 지점별 매출 데이터 로드
        String[] salesColumnNames = {"지점", "매출액", "사용자 수", "평균 가격"};
        salesTableModel = new DefaultTableModel(salesColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 샘플 데이터
        salesTableModel.addRow(new Object[]{"강남점", "500,000원", "120명", "4,167원"});
        salesTableModel.addRow(new Object[]{"홍대점", "400,000원", "100명", "4,000원"});
        salesTableModel.addRow(new Object[]{"명동점", "350,000원", "90명", "3,889원"});
        salesTableModel.addRow(new Object[]{"서초점", "320,000원", "85명", "3,765원"});
        salesTableModel.addRow(new Object[]{"노량진점", "300,000원", "80명", "3,750원"});

        JTable salesTable = new JTable(salesTableModel);
        salesTable.setFont(FontUtil.getKoreanFontPlain(11));
        salesTable.setRowHeight(25);
        leftPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // 우측: 인기 음식 테이블
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("인기 음식 (월별)"));
        rightPanel.setBackground(new Color(240, 240, 240));

        // DB 연결 필요: 월별 인기 음식 데이터 로드
        String[] foodColumnNames = {"음식명", "판매량", "매출액", "순위"};
        foodTableModel = new DefaultTableModel(foodColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 샘플 데이터
        foodTableModel.addRow(new Object[]{"라면", "150개", "600,000원", "1위"});
        foodTableModel.addRow(new Object[]{"우동", "120개", "600,000원", "2위"});
        foodTableModel.addRow(new Object[]{"떡볶이", "100개", "450,000원", "3위"});
        foodTableModel.addRow(new Object[]{"김밥", "90개", "270,000원", "4위"});
        foodTableModel.addRow(new Object[]{"핫도그", "80개", "280,000원", "5위"});

        JTable popularFoodTable = new JTable(foodTableModel);
        popularFoodTable.setFont(FontUtil.getKoreanFontPlain(11));
        popularFoodTable.setRowHeight(25);
        rightPanel.add(new JScrollPane(popularFoodTable), BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(500);

        add(splitPane, BorderLayout.CENTER);
    }

    // DB 연결 필요: 선택된 기간 반환
    public String getSelectedPeriod() {
        return (String) periodCombo.getSelectedItem();
    }

    // DB 연결 필요: 통계 데이터 업데이트
    public void updateStats(long totalSales, int userCount, int averagePrice) {
        totalSalesLabel.setText(String.format("%,d원", totalSales));
        userCountLabel.setText(userCount + "명");
        averagePriceLabel.setText(averagePrice + "원");
    }

    // DB 연결 필요: 지점별 매출 테이블 새로고침
    public void refreshSalesTable() {
        salesTableModel.setRowCount(0);
        // DB 연결 필요: 지점별 매출 데이터 다시 로드
        salesTableModel.addRow(new Object[]{"강남점", "500,000원", "120명", "4,167원"});
        salesTableModel.addRow(new Object[]{"홍대점", "400,000원", "100명", "4,000원"});
    }

    // DB 연결 필요: 인기 음식 테이블 새로고침
    public void refreshPopularFoodTable() {
        foodTableModel.setRowCount(0);
        // DB 연결 필요: 인기 음식 데이터 다시 로드
        foodTableModel.addRow(new Object[]{"라면", "150개", "600,000원", "1위"});
        foodTableModel.addRow(new Object[]{"우동", "120개", "600,000원", "2위"});
    }
}

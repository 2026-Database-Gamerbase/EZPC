package view.owner;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 시간대별 피크 매출 분석 팝업 (JDialog)
// ==========================================
public class OwnerPeakTimeDialog extends JDialog {
    private JTable peakTable;
    private DefaultTableModel tableModel;
    private JLabel peakTimeLabel;
    private JLabel peakSalesLabel;

    public OwnerPeakTimeDialog(JFrame parentFrame) {
        super(parentFrame, "시간대별 피크 매출 분석", true);
        setSize(480, 520);
        setLocationRelativeTo(parentFrame);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 타이틀 + 기간
        // ==========================================
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JLabel titleLabel = new JLabel("시간대별 피크(Peak) 리포트");
        titleLabel.setFont(FontUtil.getKoreanFontBold(16));

        JLabel periodLabel = new JLabel("※ 최근 1개월 기준");
        periodLabel.setFont(FontUtil.getKoreanFontPlain(11));
        periodLabel.setForeground(Color.GRAY);
        periodLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        titlePanel.add(titleLabel,  BorderLayout.WEST);
        titlePanel.add(periodLabel, BorderLayout.EAST);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // ==========================================
        // 피크 시간대 배너
        // ==========================================
        JPanel banner = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        banner.setBackground(Color.WHITE);
        banner.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        JLabel bannerTitle = new JLabel("최고 매출 시간대");
        bannerTitle.setFont(FontUtil.getKoreanFontPlain(12));
        bannerTitle.setForeground(Color.GRAY);

        peakTimeLabel = new JLabel("-");
        peakTimeLabel.setFont(FontUtil.getKoreanFontBold(20));
        peakTimeLabel.setForeground(new Color(100, 150, 255));

        JLabel separator = new JLabel("|");
        separator.setForeground(Color.LIGHT_GRAY);
        separator.setFont(FontUtil.getKoreanFontPlain(18));

        peakSalesLabel = new JLabel("-");
        peakSalesLabel.setFont(FontUtil.getKoreanFontBold(18));
        peakSalesLabel.setForeground(new Color(40, 45, 65));

        banner.add(bannerTitle);
        banner.add(peakTimeLabel);
        banner.add(separator);
        banner.add(peakSalesLabel);

        // ==========================================
        // 중앙: 전체 순위 테이블
        // ==========================================
        String[] columnNames = {"순위", "시간대", "총 매출액"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        peakTable = new JTable(tableModel);
        peakTable.setFont(FontUtil.getKoreanFontPlain(12));
        peakTable.setRowHeight(25);
        peakTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        peakTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        peakTable.getColumnModel().getColumn(2).setPreferredWidth(200);

        peakTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row == 0 ? new Color(255, 243, 200) : Color.WHITE);
                    c.setForeground(row == 0 ? new Color(150, 100, 0) : Color.BLACK);
                }
                return c;
            }
        });

        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.add(banner, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(peakTable), BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ==========================================
        // 하단: 닫기 버튼
        // ==========================================
        JButton closeButton = new JButton("확인");
        closeButton.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // Controller에서 피크타임 리포트 데이터를 주입
    // data: [시간대, 총 매출액] 형태, 0번 행이 1위
    public void setPeakTimeData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data == null) return;

        for (int i = 0; i < data.length; i++) {
            tableModel.addRow(new Object[]{(i + 1) + "위", data[i][0], data[i][1]});
        }

        // 배너에 1위 데이터 표시
        if (data.length > 0) {
            peakTimeLabel.setText(data[0][0].toString());
            peakSalesLabel.setText(data[0][1].toString());
        }
    }
}

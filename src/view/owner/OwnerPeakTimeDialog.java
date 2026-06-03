package view.owner;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 시간대별 피크 분석 팝업 (JDialog)
// ==========================================
public class OwnerPeakTimeDialog extends JDialog {
    private JTable peakTable;
    private DefaultTableModel tableModel;

    public OwnerPeakTimeDialog(JFrame parentFrame) {
        super(parentFrame, "시간대별 피크 매출 및 방문객 분석", true);
        setSize(600, 450);
        setLocationRelativeTo(parentFrame);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 타이틀
        // ==========================================
        JLabel titleLabel = new JLabel("시간대별 피크(Peak) 리포트");
        titleLabel.setFont(FontUtil.getKoreanFontBold(16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 피크타임 분석 테이블
        // ==========================================
        String[] columnNames = {"시간대", "평균 매출액", "최대 방문객 수"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        peakTable = new JTable(tableModel);
        peakTable.setFont(FontUtil.getKoreanFontPlain(12));
        peakTable.setRowHeight(25);
        
        mainPanel.add(new JScrollPane(peakTable), BorderLayout.CENTER);

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

    // ==========================================
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================

    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    // Controller에서 피크타임 리포트 데이터를 주입
    public void setPeakTimeData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
}
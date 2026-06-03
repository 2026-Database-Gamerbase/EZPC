package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 이벤트 성과 분석 팝업 (JDialog)
// 이벤트 전/후의 매출량 및 객단가 변화를 비교 출력
// ==========================================
public class OwnerEventAnalysisDialog extends JDialog {
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton analyzeButton;
    private JTable analysisTable;
    private DefaultTableModel tableModel;

    public OwnerEventAnalysisDialog(JFrame parentFrame) {
        super(parentFrame, "이벤트 전/후 성과 분석 리포트", true);
        setSize(700, 400);
        setLocationRelativeTo(parentFrame);
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 날짜 검색 조건 입력 패널
        // ==========================================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(240, 240, 240));
        searchPanel.setBorder(BorderFactory.createTitledBorder("이벤트 기간 설정 (YYYY-MM-DD)"));

        searchPanel.add(new JLabel("시작일:"));
        startDateField = new JTextField(10);
        searchPanel.add(startDateField);

        searchPanel.add(new JLabel("  종료일:"));
        endDateField = new JTextField(10);
        searchPanel.add(endDateField);

        analyzeButton = new JButton("성과 분석하기");
        analyzeButton.setBackground(new Color(100, 150, 255));
        analyzeButton.setForeground(Color.WHITE);
        searchPanel.add(analyzeButton);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 성과 비교 테이블
        // ==========================================
        String[] columnNames = {"분석 구간", "대상 기간", "총 매출", "일평균 매출", "객단가"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        analysisTable = new JTable(tableModel);
        analysisTable.setFont(FontUtil.getKoreanFontPlain(12));
        analysisTable.setRowHeight(30);
        
        mainPanel.add(new JScrollPane(analysisTable), BorderLayout.CENTER);

        // ==========================================
        // 하단: 닫기 버튼
        // ==========================================
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ==========================================
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================
    
    // ==========================================
    // Getter
    // ==========================================
    public String getStartDate() {
        return startDateField.getText().trim();
    }

    public String getEndDate() {
        return endDateField.getText().trim();
    }

    // ==========================================
    // Listener 등록
    // ==========================================
    public void setAnalyzeButtonListener(ActionListener listener) {
        analyzeButton.addActionListener(listener);
    }
    
    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    /**
     * Controller에서 이벤트 전/후 비교 데이터를 주입합니다.
     * @param data Object[][] 형태의 배열 (분석 구간, 대상 기간, 총 매출, 일평균 매출, 객단가)
     */
    public void setAnalysisTableData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
}
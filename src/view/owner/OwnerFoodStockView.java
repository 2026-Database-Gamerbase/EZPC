package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 음식 재고 관리 탭 화면 구성 Panel
// 최상위 메인 프레임에서 선택된 지점의 재고 현황만 필터링되어 표시
// ==========================================
public class OwnerFoodStockView extends JPanel {
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JSpinner quantitySpinner;
    private JButton updateButton;
    private JLabel statusLabel;

    public OwnerFoodStockView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // ==========================================
        // 상단: 제목
        // ==========================================
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("음식 재고 관리");
        titleLabel.setFont(FontUtil.getKoreanFontBold(20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 재고 테이블
        // ==========================================
        String[] columnNames = {"음식명", "현재 재고"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stockTable = new JTable(tableModel);
        stockTable.setFont(FontUtil.getKoreanFontPlain(12));
        stockTable.setRowHeight(25);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(stockTable), BorderLayout.CENTER);

        // ==========================================
        // 하단: 재고 수정 패널
        // ==========================================
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 수정 옵션 패널
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        editPanel.setBackground(new Color(240, 240, 240));
        editPanel.setBorder(BorderFactory.createTitledBorder("재고 업데이트"));

        editPanel.add(new JLabel("선택된 음식의 수량:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        quantitySpinner.setPreferredSize(new Dimension(100, 25));
        editPanel.add(quantitySpinner);

        updateButton = new JButton("업데이트");
        updateButton.setPreferredSize(new Dimension(100, 25));
        editPanel.add(updateButton);

        bottomPanel.add(editPanel);

        // 상태 메시지 패널
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FontUtil.getKoreanFontPlain(12));
        statusLabel.setForeground(Color.RED);
        statusPanel.add(statusLabel);
        bottomPanel.add(statusPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================

    // ==========================================
    // Getter
    // ==========================================
    public String getSelectedFoodName() {
        int row = stockTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 0);
        }
        return null;
    }

    public int getSelectedCurrentStock() {
        int row = stockTable.getSelectedRow();
        if (row >= 0) {
            return (int) tableModel.getValueAt(row, 1);
        }
        return 0;
    }

    public int getQuantity() {
        return (int) quantitySpinner.getValue();
    }

    // ==========================================
    // Setter
    // ==========================================
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // ==========================================
    // Listener 등록
    // ==========================================
    public void setUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    /**
     * Controller에서 DB 연동 후, 선택된 지점의 최신 재고 데이터를 테이블에 덮어씌웁니다.
     * @param data Object[][] 형태의 재고 배열 (음식명, 현재 재고)
     */
    public void setStockTableData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
    
    // 입력 폼 초기화 (업데이트 성공 후 호출)
    public void clearInputForm() {
        quantitySpinner.setValue(0);
        stockTable.clearSelection();
    }
}
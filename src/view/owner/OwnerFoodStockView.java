// 음식 재고 탭
package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

public class OwnerFoodStockView extends JPanel {
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JSpinner quantitySpinner;
    private JButton updateButton;
    private JButton refreshButton;
    private JLabel statusLabel;

    public OwnerFoodStockView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 제목
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("음식 재고 관리");
        titleLabel.setFont(FontUtil.getKoreanFontBold(20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 중앙: 재고 테이블
        // DB 연결 필요: 해당 지점의 음식 재고 정보 로드
        String[] columnNames = {"음식명", "현재 재고", "최소 재고", "상태"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 샘플 데이터
        tableModel.addRow(new Object[]{"라면", 12, 20, "부족"});
        tableModel.addRow(new Object[]{"우동", 35, 20, "정상"});
        tableModel.addRow(new Object[]{"김밥", 50, 30, "정상"});
        tableModel.addRow(new Object[]{"떡볶이", 5, 15, "긴급"});
        tableModel.addRow(new Object[]{"핫도그", 28, 20, "정상"});
        tableModel.addRow(new Object[]{"계란", 100, 50, "정상"});
        tableModel.addRow(new Object[]{"소시지", 15, 25, "부족"});

        stockTable = new JTable(tableModel);
        stockTable.setFont(FontUtil.getKoreanFontPlain(12));
        stockTable.setRowHeight(25);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 상태에 따라 색상 표시 (커스텀 렌더러)
        stockTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("긴급".equals(status)) {
                    c.setBackground(new Color(255, 99, 71)); // 빨강색
                    c.setForeground(Color.WHITE);
                } else if ("부족".equals(status)) {
                    c.setBackground(new Color(255, 200, 100)); // 주황색
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(144, 238, 144)); // 초록색
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        add(new JScrollPane(stockTable), BorderLayout.CENTER);

        // 하단: 재고 수정 패널
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

        refreshButton = new JButton("새로고침");
        refreshButton.setPreferredSize(new Dimension(100, 25));
        editPanel.add(refreshButton);

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

    // 선택된 행의 음식명 반환
    public String getSelectedFoodName() {
        int row = stockTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 0);
        }
        return null;
    }

    // 선택된 행의 현재 재고 반환
    public int getSelectedCurrentStock() {
        int row = stockTable.getSelectedRow();
        if (row >= 0) {
            return (int) tableModel.getValueAt(row, 1);
        }
        return 0;
    }

    // 수량 스피너의 값 반환
    public int getQuantity() {
        return (int) quantitySpinner.getValue();
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 업데이트 버튼 리스너 설정
    public void setUpdateButtonListener(ActionListener listener) {
        updateButton.addActionListener(listener);
    }

    // 새로고침 버튼 리스너 설정
    public void setRefreshButtonListener(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    // DB 연결 필요: 재고 테이블 새로고침
    public void refreshStockTable() {
        tableModel.setRowCount(0);
        // DB 연결 필요: 현재 지점의 음식 재고 정보 다시 로드
        tableModel.addRow(new Object[]{"라면", 12, 20, "부족"});
        tableModel.addRow(new Object[]{"우동", 35, 20, "정상"});
        tableModel.addRow(new Object[]{"김밥", 50, 30, "정상"});
    }

    // DB 연결 필요: 특정 음식의 재고 업데이트
    public void updateStockRow(int row, int newQuantity) {
        if (row >= 0 && row < tableModel.getRowCount()) {
            tableModel.setValueAt(newQuantity, row, 1);
            // 상태 업데이트 로직 (최소 재고 기준)
            int minStock = (int) tableModel.getValueAt(row, 2);
            String status;
            if (newQuantity == 0) {
                status = "긴급";
            } else if (newQuantity < minStock) {
                status = "부족";
            } else {
                status = "정상";
            }
            tableModel.setValueAt(status, row, 3);
        }
    }
}

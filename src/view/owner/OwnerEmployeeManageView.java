package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * OwnerEmployeeManageView - 직원 관리 탭
 * 알바생 및 매니저들의 목록을 조회하고, 시급 조정, 고용/해고,
 * 현재 출근 여부를 관리하는 화면입니다.
 */
public class OwnerEmployeeManageView extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JSpinner wageSpinner;
    private JCheckBox attendanceCheckBox;
    private JButton saveButton;
    private JButton hireButton;
    private JButton fireButton;
    private JLabel statusLabel;

    public OwnerEmployeeManageView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 제목
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("직원 관리");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 중앙: 직원 테이블
        // DB 연결 필요: 직원 목록 로드
        String[] columnNames = {"이름", "직급", "시급", "출근 상태", "고용일"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 샘플 데이터
        tableModel.addRow(new Object[]{"김철수", "매니저", "12,000원", "출근중", "2023-01-15"});
        tableModel.addRow(new Object[]{"이영희", "알바", "10,000원", "퇴근", "2023-06-20"});
        tableModel.addRow(new Object[]{"박민준", "알바", "10,000원", "출근중", "2024-01-10"});
        tableModel.addRow(new Object[]{"최수진", "매니저", "12,000원", "퇴근", "2023-03-25"});
        tableModel.addRow(new Object[]{"정진호", "알바", "10,000원", "출근중", "2024-02-01"});

        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 12));
        employeeTable.setRowHeight(25);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // 출근 상태에 따라 색상 표시 (커스텀 렌더러)
        employeeTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if ("출근중".equals(status)) {
                    c.setBackground(new Color(144, 238, 144)); // 초록색
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(200, 200, 200)); // 회색
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // 하단: 직원 정보 수정 패널
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2, 1));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 수정 옵션 패널
        JPanel editPanel = new JPanel();
        editPanel.setLayout(new GridBagLayout());
        editPanel.setBackground(new Color(240, 240, 240));
        editPanel.setBorder(BorderFactory.createTitledBorder("직원 정보 수정"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 시급
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        editPanel.add(new JLabel("시급:"), gbc);
        gbc.gridx = 1;
        wageSpinner = new JSpinner(new SpinnerNumberModel(10000, 0, 50000, 1000));
        wageSpinner.setPreferredSize(new Dimension(100, 25));
        editPanel.add(wageSpinner, gbc);

        editPanel.add(new JLabel("원"), gbc);

        // 출근 상태
        gbc.gridx = 3;
        editPanel.add(new JLabel("출근 상태:"), gbc);
        gbc.gridx = 4;
        attendanceCheckBox = new JCheckBox("출근중");
        editPanel.add(attendanceCheckBox, gbc);

        // 버튼들
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));

        saveButton = new JButton("수정 적용");
        saveButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(saveButton);

        hireButton = new JButton("신규 채용");
        hireButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(hireButton);

        fireButton = new JButton("해고");
        fireButton.setPreferredSize(new Dimension(100, 30));
        fireButton.setBackground(new Color(255, 99, 71));
        fireButton.setForeground(Color.WHITE);
        buttonPanel.add(fireButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        editPanel.add(buttonPanel, gbc);

        bottomPanel.add(editPanel);

        // 상태 메시지 패널
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusPanel.add(statusLabel);
        bottomPanel.add(statusPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 선택된 행의 직원명 반환
    public String getSelectedEmployeeName() {
        int row = employeeTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 0);
        }
        return null;
    }

    // 선택된 행의 직급 반환
    public String getSelectedEmployeePosition() {
        int row = employeeTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 1);
        }
        return null;
    }

    // 수정할 시급 값 반환
    public int getWage() {
        return (int) wageSpinner.getValue();
    }

    // 출근 상태 반환
    public boolean isAttendanceChecked() {
        return attendanceCheckBox.isSelected();
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 수정 적용 버튼 리스너 설정
    public void setSaveButtonListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    // 신규 채용 버튼 리스너 설정
    public void setHireButtonListener(ActionListener listener) {
        hireButton.addActionListener(listener);
    }

    // 해고 버튼 리스너 설정
    public void setFireButtonListener(ActionListener listener) {
        fireButton.addActionListener(listener);
    }

    // DB 연결 필요: 직원 테이블 새로고침
    public void refreshEmployeeTable() {
        tableModel.setRowCount(0);
        // DB 연결 필요: 직원 목록 다시 로드
        tableModel.addRow(new Object[]{"김철수", "매니저", "12,000원", "출근중", "2023-01-15"});
        tableModel.addRow(new Object[]{"이영희", "알바", "10,000원", "퇴근", "2023-06-20"});
    }

    // DB 연결 필요: 직원 정보 업데이트
    public void updateEmployeeRow(int row, String name, String position, int wage, String attendance) {
        if (row >= 0 && row < tableModel.getRowCount()) {
            tableModel.setValueAt(wage + "원", row, 2);
            tableModel.setValueAt(attendance, row, 3);
        }
    }
}

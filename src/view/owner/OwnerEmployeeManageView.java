package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 직원 관리 탭 Panel
// 해당 지점 직원의 시급 조정, 출근 상태 변경, 신규 채용 및 해고(CRUD) 기능 제공
// ==========================================
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

        // ==========================================
        // 상단: 제목
        // ==========================================
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("직원 관리");
        titleLabel.setFont(FontUtil.getKoreanFontPlain(20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 직원 테이블
        // ==========================================
        String[] columnNames = {"직원번호", "이름", "직급", "시급", "출근 상태", "고용일"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setFont(FontUtil.getKoreanFontPlain(12));
        employeeTable.setRowHeight(25);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        employeeTable.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                
                if (status != null) {
                    if ("출근중".equals(status)) {
                        c.setBackground(new Color(144, 238, 144)); // 초록색
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(new Color(200, 200, 200)); // 회색
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // ==========================================
        // 하단: 직원 정보 수정 패널
        // ==========================================
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
        hireButton.setBackground(new Color(40, 167, 69));
        hireButton.setForeground(Color.WHITE);
        hireButton.setOpaque(true); 
        hireButton.setBorderPainted(false); 
        buttonPanel.add(hireButton);

        fireButton = new JButton("해고");
        fireButton.setPreferredSize(new Dimension(100, 30));
        fireButton.setBackground(new Color(255, 99, 71));
        fireButton.setForeground(Color.WHITE);
        fireButton.setOpaque(true); 
        fireButton.setBorderPainted(false); 
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
    public String getSelectedEmployeeId() {
        int row = employeeTable.getSelectedRow();
        if (row >= 0) {
            return String.valueOf(tableModel.getValueAt(row, 0)); 
        }
        return null;
    }

    public String getSelectedEmployeeName() {
        int row = employeeTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 1);
        }
        return null;
    }

    public String getSelectedEmployeePosition() {
        int row = employeeTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 2);
        }
        return null;
    }

    public String getSelectedWageString() {
        int row = employeeTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 3);
        }
        return null;
    }

    public String getSelectedAttendanceString() {
        int row = employeeTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 4);
        }
        return null;
    }

    public int getWage() {
        return (int) wageSpinner.getValue();
    }

    public boolean isAttendanceChecked() {
        return attendanceCheckBox.isSelected();
    }

    // ==========================================
    // Setter
    // ==========================================
    public void setWage(int wage) {
        wageSpinner.setValue(wage);
    }

    public void setAttendanceChecked(boolean checked) {
        attendanceCheckBox.setSelected(checked);
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // ==========================================
    // Listner 등록
    // ==========================================
    public void setSaveButtonListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public void setHireButtonListener(ActionListener listener) {
        hireButton.addActionListener(listener);
    }

    public void setFireButtonListener(ActionListener listener) {
        fireButton.addActionListener(listener);
    }

    public void setTableSelectionListener(ListSelectionListener listener) {
        employeeTable.getSelectionModel().addListSelectionListener(listener);
    }
    
    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    /**
     * 선택된 지점의 직원 목록을 테이블에 덮어씌웁니다.
     * @param data Object[][] 형태의 배열 (직원번호, 이름, 직급, 시급, 출근상태, 고용일)
     */
    public void setEmployeeTableData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }

    // 작업(수정/채용/해고) 완료 후 입력 폼을 기본값으로 초기화
    public void clearInputForm() {
        wageSpinner.setValue(10000);
        attendanceCheckBox.setSelected(false);
        employeeTable.clearSelection();
    }
}
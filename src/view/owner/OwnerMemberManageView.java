package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 회원 관리 View
// 가입된 회원들의 등급 기준과 등급별 혜택을 조정
// 휴면 회원 필터링을 통해 타겟 마케팅 데이터를 조회
// ==========================================
public class OwnerMemberManageView extends JPanel {
    private JTable memberTable;
    private JTable gradeTable;
    private DefaultTableModel memberTableModel;
    private DefaultTableModel gradeTableModel;
    private JSpinner discountRateSpinner;
    private JSpinner standardAmountSpinner;
    private JButton saveGradeButton;
    private JLabel statusLabel;
    private JCheckBox dormantMemberCheckBox;

    public OwnerMemberManageView() {
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
        JLabel titleLabel = new JLabel("회원 관리");
        titleLabel.setFont(FontUtil.getKoreanFontBold(20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 중앙: 좌측(회원 목록) 및 우측(등급 기준 설정)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(240, 240, 240));

        // ==========================================
        // 좌측: 회원 목록 패널 구성
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("회원 목록"));
        leftPanel.setBackground(new Color(240, 240, 240));

        // 좌측 상단 휴면 회원 필터 체크박스 구역
        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftTopPanel.setBackground(new Color(240, 240, 240));
        dormantMemberCheckBox = new JCheckBox("30일 이상 미방문 휴면 회원만 보기");
        dormantMemberCheckBox.setBackground(new Color(240, 240, 240));
        dormantMemberCheckBox.setFont(FontUtil.getKoreanFontBold(12));
        dormantMemberCheckBox.setForeground(new Color(50, 50, 50));
        leftTopPanel.add(dormantMemberCheckBox);
        
        leftPanel.add(leftTopPanel, BorderLayout.NORTH);

        String[] memberColumnNames = {"ID", "이름", "현재등급", "누적금액", "가입일"};
        memberTableModel = new DefaultTableModel(memberColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        memberTable = new JTable(memberTableModel);
        memberTable.setFont(FontUtil.getKoreanFontPlain(12));
        memberTable.setRowHeight(25);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        leftPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // ==========================================
        // 우측: 등급 기준 설정 패널 구성
        // ==========================================
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("등급 기준 및 혜택 설정"));
        rightPanel.setBackground(new Color(240, 240, 240));

        // 등급 테이블
        String[] gradeColumnNames = {"등급명", "기준금액", "할인율 (%)", "혜택설명"};
        gradeTableModel = new DefaultTableModel(gradeColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 등급 초기 데이터
        gradeTableModel.addRow(new Object[]{"Bronze", "0원", "0%", "기본 등급"});
        gradeTableModel.addRow(new Object[]{"Silver", "100,000원", "5%", "5% 할인"});
        gradeTableModel.addRow(new Object[]{"Gold", "200,000원", "10%", "10% 할인"});
        gradeTableModel.addRow(new Object[]{"Platinum", "500,000원", "15%", "15% 할인"});

        gradeTable = new JTable(gradeTableModel);
        gradeTable.setFont(FontUtil.getKoreanFontPlain(12));
        gradeTable.setRowHeight(25);
        gradeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        rightPanel.add(new JScrollPane(gradeTable), BorderLayout.CENTER);

        // 등급 수정 패널
        JPanel editGradePanel = new JPanel();
        editGradePanel.setLayout(new GridBagLayout());
        editGradePanel.setBackground(new Color(240, 240, 240));
        editGradePanel.setBorder(BorderFactory.createTitledBorder("선택 등급 수정"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 기준 금액
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        editGradePanel.add(new JLabel("기준 금액:"), gbc);
        gbc.gridx = 1;
        standardAmountSpinner = new JSpinner(new SpinnerNumberModel(100000, 0, 10000000, 10000));
        standardAmountSpinner.setPreferredSize(new Dimension(120, 25));
        editGradePanel.add(standardAmountSpinner, gbc);
        editGradePanel.add(new JLabel("원"), gbc);

        // 할인율
        gbc.gridx = 0;
        gbc.gridy = 1;
        editGradePanel.add(new JLabel("할인율:"), gbc);
        gbc.gridx = 1;
        discountRateSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 50, 1));
        discountRateSpinner.setPreferredSize(new Dimension(120, 25));
        editGradePanel.add(discountRateSpinner, gbc);
        editGradePanel.add(new JLabel("%"), gbc);

        // 저장 버튼
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        saveGradeButton = new JButton("등급 기준 저장");
        saveGradeButton.setPreferredSize(new Dimension(200, 30));
        editGradePanel.add(saveGradeButton, gbc);

        rightPanel.add(editGradePanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(400);

        add(splitPane, BorderLayout.CENTER);

        // ==========================================
        // 하단: 상태 메시지
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FontUtil.getKoreanFontPlain(12));
        statusLabel.setForeground(Color.RED);
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================

    // ==========================================
    // Getter
    // ==========================================
    public boolean isDormantFilterSelected() {
        return dormantMemberCheckBox.isSelected();
    }

    public String getSelectedMemberId() {
        int row = memberTable.getSelectedRow();
        if (row >= 0) {
            return (String) memberTableModel.getValueAt(row, 0);
        }
        return null;
    }

    public String getSelectedGrade() {
        int row = gradeTable.getSelectedRow();
        if (row >= 0) {
            return (String) gradeTableModel.getValueAt(row, 0);
        }
        return null;
    }

    public int getStandardAmount() {
        return (int) standardAmountSpinner.getValue();
    }

    public int getDiscountRate() {
        return (int) discountRateSpinner.getValue();
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
    public void setDormantFilterListener(ActionListener listener) {
        dormantMemberCheckBox.addActionListener(listener);
    }

    public void setSaveGradeButtonListener(ActionListener listener) {
        saveGradeButton.addActionListener(listener);
    }

    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    /**
     * DB 데이터로 회원 목록 표를 덮어씌웁니다.
     * @param data Object[][] 형태의 배열 (ID, 이름, 현재등급, 누적금액, 가입일)
     */
    public void setMemberTableData(Object[][] data) {
        memberTableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                memberTableModel.addRow(row);
            }
        }
    }

    public void refreshGradeTable() {
        gradeTableModel.setRowCount(0);
        gradeTableModel.addRow(new Object[]{"Bronze", "0원", "0%", "기본 등급"});
        gradeTableModel.addRow(new Object[]{"Silver", "100,000원", "5%", "5% 할인"});
        gradeTableModel.addRow(new Object[]{"Gold", "200,000원", "10%", "10% 할인"});
        gradeTableModel.addRow(new Object[]{"Platinum", "500,000원", "15%", "15% 할인"});
    }

    public void updateGradeRow(int row, int standardAmount, int discountRate) {
        if (row >= 0 && row < gradeTableModel.getRowCount()) {
            gradeTableModel.setValueAt(String.format("%,d원", standardAmount), row, 1);
            gradeTableModel.setValueAt(discountRate + "%", row, 2);
        }
    }
}
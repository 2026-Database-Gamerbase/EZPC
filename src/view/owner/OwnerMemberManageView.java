// 회원 관리 탭 (등급, 할인율 등)
package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

public class OwnerMemberManageView extends JPanel {
    private JTable memberTable;
    private JTable gradeTable;
    private DefaultTableModel memberTableModel;
    private DefaultTableModel gradeTableModel;
    private JSpinner discountRateSpinner;
    private JSpinner standardAmountSpinner;
    private JButton saveGradeButton;
    private JLabel statusLabel;

    public OwnerMemberManageView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 제목
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

        // 좌측: 회원 목록
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("회원 목록"));
        leftPanel.setBackground(new Color(240, 240, 240));

        // DB 연결 필요: 회원 목록 로드
        String[] memberColumnNames = {"ID", "이름", "현재등급", "누적금액", "가입일"};
        memberTableModel = new DefaultTableModel(memberColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 샘플 데이터
        memberTableModel.addRow(new Object[]{"user001", "김철수", "Gold", "250,000원", "2023-01-15"});
        memberTableModel.addRow(new Object[]{"user002", "이영희", "Silver", "120,000원", "2023-06-20"});
        memberTableModel.addRow(new Object[]{"user003", "박민준", "Bronze", "50,000원", "2024-01-10"});
        memberTableModel.addRow(new Object[]{"user004", "최수진", "Gold", "280,000원", "2023-03-25"});
        memberTableModel.addRow(new Object[]{"user005", "정진호", "Silver", "100,000원", "2024-02-01"});

        memberTable = new JTable(memberTableModel);
        memberTable.setFont(FontUtil.getKoreanFontPlain(11));
        memberTable.setRowHeight(25);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        leftPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // 우측: 등급 기준 설정
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("등급 기준 및 혜택 설정"));
        rightPanel.setBackground(new Color(240, 240, 240));

        // 등급 테이블
        // DB 연결 필요: 등급 기준 및 할인율 정보 로드
        String[] gradeColumnNames = {"등급명", "기준금액", "할인율 (%)", "혜택설명"};
        gradeTableModel = new DefaultTableModel(gradeColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 샘플 데이터
        gradeTableModel.addRow(new Object[]{"Bronze", "0원", "0%", "기본 등급"});
        gradeTableModel.addRow(new Object[]{"Silver", "100,000원", "5%", "5% 할인"});
        gradeTableModel.addRow(new Object[]{"Gold", "200,000원", "10%", "10% 할인"});
        gradeTableModel.addRow(new Object[]{"Platinum", "500,000원", "15%", "15% 할인"});

        gradeTable = new JTable(gradeTableModel);
        gradeTable.setFont(FontUtil.getKoreanFontPlain(11));
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

        // 하단: 상태 메시지
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FontUtil.getKoreanFontPlain(12));
        statusLabel.setForeground(Color.RED);
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 선택된 회원 ID 반환
    public String getSelectedMemberId() {
        int row = memberTable.getSelectedRow();
        if (row >= 0) {
            return (String) memberTableModel.getValueAt(row, 0);
        }
        return null;
    }

    // 선택된 등급 반환
    public String getSelectedGrade() {
        int row = gradeTable.getSelectedRow();
        if (row >= 0) {
            return (String) gradeTableModel.getValueAt(row, 0);
        }
        return null;
    }

    // 기준 금액 값 반환
    public int getStandardAmount() {
        return (int) standardAmountSpinner.getValue();
    }

    // 할인율 값 반환
    public int getDiscountRate() {
        return (int) discountRateSpinner.getValue();
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 등급 기준 저장 버튼 리스너 설정
    public void setSaveGradeButtonListener(ActionListener listener) {
        saveGradeButton.addActionListener(listener);
    }

    // DB 연결 필요: 회원 테이블 새로고침
    public void refreshMemberTable() {
        memberTableModel.setRowCount(0);
        // DB 연결 필요: 회원 목록 다시 로드
        memberTableModel.addRow(new Object[]{"user001", "김철수", "Gold", "250,000원", "2023-01-15"});
        memberTableModel.addRow(new Object[]{"user002", "이영희", "Silver", "120,000원", "2023-06-20"});
    }

    // DB 연결 필요: 등급 테이블 새로고침
    public void refreshGradeTable() {
        gradeTableModel.setRowCount(0);
        // DB 연결 필요: 등급 기준 정보 다시 로드
        gradeTableModel.addRow(new Object[]{"Bronze", "0원", "0%", "기본 등급"});
        gradeTableModel.addRow(new Object[]{"Silver", "100,000원", "5%", "5% 할인"});
        gradeTableModel.addRow(new Object[]{"Gold", "200,000원", "10%", "10% 할인"});
    }

    // DB 연결 필요: 등급 정보 업데이트
    public void updateGradeRow(int row, int standardAmount, int discountRate) {
        if (row >= 0 && row < gradeTableModel.getRowCount()) {
            gradeTableModel.setValueAt(String.format("%,d원", standardAmount), row, 1);
            gradeTableModel.setValueAt(discountRate + "%", row, 2);
        }
    }
}

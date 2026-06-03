package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import view.FontUtil;

// ==========================================
// 시스템 설정 및 기준 정보 관리 탭 화면을 구성하는 Panel
// 전 지점에 적용될 데이터 CRUD (EventInfo, Food, Ticket, PcCafe)
// ==========================================
public class OwnerSystemSetupView extends JPanel {
    
    // 카테고리 선택 트리/리스트
    private JList<String> categoryList;
    
    // 동적으로 바뀔 중앙 패널 구역
    private JPanel rightPanel;
    private JLabel currentCategoryTitle;
    
    // 마스터 데이터 테이블
    private JTable dataTable;
    private DefaultTableModel tableModel;
    
    // 하단 공통 입력 폼 요소들
    private JLabel field1Label; private JTextField field1Input;
    private JLabel field2Label; private JTextField field2Input;
    private JLabel field3Label; private JTextField field3Input;
    private JLabel field4Label; private JTextField field4Input;
    
    private JButton saveButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JLabel statusLabel;

    public OwnerSystemSetupView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // ==========================================
        // 좌측: 설정 카테고리 메뉴
        // ==========================================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("설정 카테고리"));
        leftPanel.setBackground(new Color(240, 240, 240));

        String[] categories = {
            "지점(PC방) 관리",
            "공통 음식 메뉴 관리",
            "공통 요금제 관리",
            "이벤트 템플릿 관리"
        };
        categoryList = new JList<>(categories);
        categoryList.setFont(FontUtil.getKoreanFontBold(14));
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setSelectedIndex(0); // 기본 선택
        
        // 메뉴 선택 시 스타일
        categoryList.setSelectionBackground(new Color(100, 150, 255));
        categoryList.setSelectionForeground(Color.WHITE);
        categoryList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        leftPanel.add(new JScrollPane(categoryList), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        // ==========================================
        // 우측: 선택된 카테고리의 마스터 데이터 화면
        // ==========================================
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(240, 240, 240));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 우측 상단 타이틀
        currentCategoryTitle = new JLabel("지점(PC방) 관리");
        currentCategoryTitle.setFont(FontUtil.getKoreanFontBold(20));
        currentCategoryTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        rightPanel.add(currentCategoryTitle, BorderLayout.NORTH);

        // 중앙 테이블 (초기에는 지점 관리용 컬럼 세팅)
        String[] columnNames = {"지점 코드", "지점명", "총 좌석수"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        dataTable = new JTable(tableModel);
        dataTable.setFont(FontUtil.getKoreanFontPlain(12));
        dataTable.setRowHeight(25);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        rightPanel.add(new JScrollPane(dataTable), BorderLayout.CENTER);

        // ==========================================
        // 우측 하단: 데이터 생성/수정(CRUD) 폼
        // ==========================================
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(new Color(240, 240, 240));
        
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); 
        formPanel.setBorder(BorderFactory.createTitledBorder("기준 정보 추가 / 수정"));
        formPanel.setBackground(new Color(240, 240, 240));
        
        field1Label = new JLabel("지점 코드:"); field1Label.setFont(FontUtil.getKoreanFontPlain(12));
        field1Input = new JTextField(8);      field1Input.setFont(FontUtil.getKoreanFontPlain(12));
        
        field2Label = new JLabel("지점명:");   field2Label.setFont(FontUtil.getKoreanFontPlain(12));
        field2Input = new JTextField(10);     field2Input.setFont(FontUtil.getKoreanFontPlain(12));
        
        field3Label = new JLabel("총 좌석수:"); field3Label.setFont(FontUtil.getKoreanFontPlain(12));
        field3Input = new JTextField(8);      field3Input.setFont(FontUtil.getKoreanFontPlain(12));
        
        field4Label = new JLabel("여분 필드:"); field4Label.setFont(FontUtil.getKoreanFontPlain(12));
        field4Input = new JTextField(8);      field4Input.setFont(FontUtil.getKoreanFontPlain(12));
        field4Label.setVisible(false);
        field4Input.setVisible(false);
        
        formPanel.add(field1Label); formPanel.add(field1Input);
        formPanel.add(field2Label); formPanel.add(field2Input);
        formPanel.add(field3Label); formPanel.add(field3Input);
        formPanel.add(field4Label); formPanel.add(field4Input);
        
        formWrapper.add(formPanel, BorderLayout.CENTER);

        // 버튼 구역
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FontUtil.getKoreanFontPlain(12));
        statusLabel.setForeground(Color.RED);
        buttonPanel.add(statusLabel);
        
        clearButton = new JButton("입력창 비우기");
        clearButton.setFont(FontUtil.getKoreanFontPlain(12));
        
        saveButton = new JButton("저장 (추가/수정)");
        saveButton.setFont(FontUtil.getKoreanFontPlain(12));
        saveButton.setBackground(new Color(100, 150, 255));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        
        deleteButton = new JButton("삭제");
        deleteButton.setFont(FontUtil.getKoreanFontPlain(12));
        deleteButton.setBackground(new Color(255, 99, 71));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);
        rightPanel.add(formWrapper, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================

    // ==========================================
    // Getter
    // ==========================================

    // 현재 선택된 카테고리 인덱스를 반환 (0: 지점, 1: 음식, 2: 요금제, 3: 이벤트)
    public int getSelectedCategoryIndex() {
        return categoryList.getSelectedIndex();
    }
    
    // 테이블에서 선택된 행의 특정 컬럼 값 get (PK 가져올 때 사용)
    public String getSelectedRowId() {
        int row = dataTable.getSelectedRow();
        if (row >= 0) return String.valueOf(tableModel.getValueAt(row, 0));
        return null;
    }

    // 폼 입력값 get (배열로 반환하여 Controller가 인덱스로 파싱)
    public String[] getFormInputs() {
        return new String[] {
            field1Input.getText().trim(),
            field2Input.getText().trim(),
            field3Input.getText().trim(),
            field4Input.getText().trim()
        };
    }

    // ==========================================
    // Setter
    // ==========================================
    public void setStatusMessage(String msg) {
        statusLabel.setText(msg);
    }

    // ==========================================
    // Listener 등록
    // ==========================================
    
    // 좌측 카테고리(메뉴) 선택 변경 이벤트를 Controller에 연결
    public void setCategorySelectionListener(ListSelectionListener listener) {
        categoryList.addListSelectionListener(listener);
    }
    
    // 마스터 테이블의 행 클릭 이벤트를 Controller에 연결
    public void setTableSelectionListener(ListSelectionListener listener) {
        dataTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void setSaveButtonListener(ActionListener listener) { saveButton.addActionListener(listener); }
    public void setDeleteButtonListener(ActionListener listener) { deleteButton.addActionListener(listener); }
    public void setClearButtonListener(ActionListener listener) { clearButton.addActionListener(listener); }

    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
 
    // 선택된 카테고리에 맞춰 테이블 컬럼명과 폼 라벨을 동적으로 변경
    public void setViewMode(String title, String[] columns, String[] labels) {
        currentCategoryTitle.setText(title);
        
        // 테이블 컬럼 재설정
        tableModel.setColumnIdentifiers(columns);
        
        // 폼 라벨 재설정 (사용하지 않는 필드는 숨김 처리)
        JTextField[] inputs = {field1Input, field2Input, field3Input, field4Input};
        JLabel[] viewLabels = {field1Label, field2Label, field3Label, field4Label};
        
        for (int i = 0; i < 4; i++) {
            if (i < labels.length) {
                viewLabels[i].setText(labels[i] + ":");
                viewLabels[i].setVisible(true);
                inputs[i].setVisible(true);
            } else {
                viewLabels[i].setVisible(false);
                inputs[i].setVisible(false);
            }
        }
    }

    // Controller에서 DB 연동 후 마스터 데이터 주입
    public void setTableData(Object[][] data) {
        tableModel.setRowCount(0);
        if (data != null) {
            for (Object[] row : data) {
                tableModel.addRow(row);
            }
        }
    }
    
 
    // 폼에 특정 데이터 주입 (테이블 행 클릭 시 호출)
    public void fillFormInputs(String[] values) {
        JTextField[] inputs = {field1Input, field2Input, field3Input, field4Input};
        for (int i = 0; i < values.length && i < 4; i++) {
            inputs[i].setText(values[i]);
        }
        // 테이블 데이터를 불러왔을 때 = 수정 모드이므로 PK(field1) 수정 불가 처리
        field1Input.setEditable(false);
        field1Input.setBackground(Color.LIGHT_GRAY);
    }

    public void clearForm() {
        field1Input.setText(""); field2Input.setText("");
        field3Input.setText(""); field4Input.setText("");
        dataTable.clearSelection();

        // 신규 추가 모드이므로 PK 입력 가능하게 롤백
        field1Input.setEditable(true);
        field1Input.setBackground(Color.WHITE);
    }
}
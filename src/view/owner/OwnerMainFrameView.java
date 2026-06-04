package view.owner;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import view.FontUtil;

// ==========================================
// Owner View의 기본 Frame
// 최상단에 전역 지점 선택 드롭다운 기능 있음
// ==========================================
public class OwnerMainFrameView extends JFrame {
    private JTabbedPane tabbedPane;
    private JComboBox<String> branchComboBox;
    private JButton logoutButton;
    
    // 하위 탭 뷰들
    private OwnerSeatMonitorView seatMonitorView;
    private OwnerSalesStatsView salesStatsView;
    private OwnerFoodStockView foodStockView;
    private OwnerEmployeeManageView employeeManageView;
    private OwnerMemberManageView memberManageView;
    private OwnerSystemSetupView systemSetupView;

    public OwnerMainFrameView() {
        setTitle("PC방 통합 관리자 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ==========================================
        // 상단: 타이틀 및 전역 지점 선택 영역
        // ==========================================
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(new Color(40, 40, 40));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        // 좌측 타이틀
        JLabel titleLabel = new JLabel("PC방 통합 관리 시스템");
        titleLabel.setFont(FontUtil.getKoreanFontBold(24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // 우측 지점 선택 컨트롤
        JPanel branchSelectPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        branchSelectPanel.setOpaque(false);
        
        JLabel branchLabel = new JLabel("관리 지점 선택: ");
        branchLabel.setFont(FontUtil.getKoreanFontBold(14));
        branchLabel.setForeground(Color.WHITE);
        
        // 콤보박스 (Controller가 DB에서 가져와 세팅)
        branchComboBox = new JComboBox<>();
        branchComboBox.setPreferredSize(new Dimension(150, 25));
        
        branchSelectPanel.add(branchLabel);
        branchSelectPanel.add(branchComboBox);
        topPanel.add(branchSelectPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 중앙: 탭 패널
        // ==========================================
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(new Color(240, 240, 240));

        seatMonitorView = new OwnerSeatMonitorView();
        tabbedPane.addTab("좌석 모니터링", seatMonitorView);

        salesStatsView = new OwnerSalesStatsView();
        tabbedPane.addTab("매출 통계", salesStatsView);

        foodStockView = new OwnerFoodStockView();
        tabbedPane.addTab("음식 재고", foodStockView);

        employeeManageView = new OwnerEmployeeManageView();
        tabbedPane.addTab("직원 관리", employeeManageView);

        memberManageView = new OwnerMemberManageView();
        tabbedPane.addTab("회원 관리", memberManageView);
        
        systemSetupView = new OwnerSystemSetupView();
        tabbedPane.addTab("시스템 설정", systemSetupView);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // ==========================================
        // 하단: 로그아웃 버튼
        // ==========================================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        bottomPanel.setBackground(new Color(40, 40, 40));

        logoutButton = new JButton("로그아웃");
        logoutButton.setFont(FontUtil.getKoreanFontBold(12));
        logoutButton.setBackground(new Color(200, 60, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(90, 28));
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // ==========================================
    // Controller 연동을 위한 Getter 및 Listener 메서드 모음
    // ==========================================

    // ==========================================
    // Getter
    // ==========================================
    
    // 현재 콤보박스에서 선택된 지점 이름을 반환
    public String getSelectedBranch() {
        return (String) branchComboBox.getSelectedItem();
    }

    // 현재 선택된 탭의 인덱스를 반환 (지점 변경 시 어떤 탭을 새로고침할지 판단하는 용도)
    public int getSelectedTabIndex() {
        return tabbedPane.getSelectedIndex();
    }

    // 하위 뷰 반환 Getter
    public OwnerSeatMonitorView getSeatMonitorView() { return seatMonitorView; }
    public OwnerSalesStatsView getSalesStatsView() { return salesStatsView; }
    public OwnerFoodStockView getFoodStockView() { return foodStockView; }
    public OwnerEmployeeManageView getEmployeeManageView() { return employeeManageView; }
    public OwnerMemberManageView getMemberManageView() { return memberManageView; }
    public OwnerSystemSetupView getSystemSetupView() { return systemSetupView; }

    // ==========================================
    // Listener 등록
    // ==========================================
    
    // 지점 콤보박스 선택이 변경되었을 때 발생하는 이벤트를 컨트롤러에 연결
    public void setBranchChangeListener(ActionListener listener) {
        branchComboBox.addActionListener(listener);
    }

    // 사용자가 다른 탭을 클릭했을 때 발생하는 이벤트를 컨트롤러에 연결
    public void addTabChangeListener(ChangeListener listener) {
        tabbedPane.addChangeListener(listener);
    }

    public void setLogoutButtonListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    // ==========================================
    // 상태 갱신용 메서드
    // ==========================================
    
    // DB에서 가져온 지점 목록을 콤보박스에 세팅
    public void setBranchList(String[] branches) {
        branchComboBox.setModel(new DefaultComboBoxModel<>(branches));
    }
}
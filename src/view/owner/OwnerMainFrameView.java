package view.owner;

import java.awt.*;
import javax.swing.*;

/**
 * OwnerMainFrameView - 사장님 관리자 창의 메인 프레임
 * 사장님 관리자 창의 메인 전체 틀(Frame)입니다.
 * 좌측이나 상단에 메뉴 네비게이션을 포함합니다.
 */
public class OwnerMainFrameView extends JFrame {
    private JTabbedPane tabbedPane;
    private OwnerSeatMonitorView seatMonitorView;
    private OwnerSalesStatsView salesStatsView;
    private OwnerFoodStockView foodStockView;
    private OwnerEmployeeManageView employeeManageView;
    private OwnerMemberManageView memberManageView;

    public OwnerMainFrameView() {
        setTitle("PC방 관리자 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 상단: 타이틀
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(40, 40, 40));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("PC방 관리 시스템");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 중앙: 탭 패널
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(new Color(240, 240, 240));

        // DB 연결 필요: 각 탭 내용 로드
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

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // 하단: 상태 바
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(200, 200, 200));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("준비 완료");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // 좌석 모니터링 뷰 반환
    public OwnerSeatMonitorView getSeatMonitorView() {
        return seatMonitorView;
    }

    // 매출 통계 뷰 반환
    public OwnerSalesStatsView getSalesStatsView() {
        return salesStatsView;
    }

    // 음식 재고 뷰 반환
    public OwnerFoodStockView getFoodStockView() {
        return foodStockView;
    }

    // 직원 관리 뷰 반환
    public OwnerEmployeeManageView getEmployeeManageView() {
        return employeeManageView;
    }

    // 회원 관리 뷰 반환
    public OwnerMemberManageView getMemberManageView() {
        return memberManageView;
    }
}

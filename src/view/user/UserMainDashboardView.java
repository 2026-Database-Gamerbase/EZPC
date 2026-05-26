package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * UserMainDashboardView - 메인 대시보드 (위젯형)
 * PC 사용 중 우측 상단 등에 항상 떠있는 메인 대시보드 창입니다.
 * 실시간 잔여 시간을 차감하여 보여주며, 음식 주문 및 리뷰 버튼이 포함됩니다.
 */
public class UserMainDashboardView extends JFrame {
    private JLabel remainingTimeLabel;
    private JLabel seatNumberLabel;
    private JLabel branchNameLabel;
    private JButton foodOrderButton;
    private JButton reviewButton;
    private JButton logoutButton;
    private JLabel timerLabel;

    public UserMainDashboardView() {
        setTitle("PC방 대시보드");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(300, 250);
        setAlwaysOnTop(true);
        setResizable(false);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(50, 50, 50));

        // 상단: 사용 정보
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 1));
        infoPanel.setBackground(new Color(50, 50, 50));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        branchNameLabel = new JLabel("지점: 강남점"); // DB 연결 필요
        branchNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        branchNameLabel.setForeground(Color.WHITE);
        infoPanel.add(branchNameLabel);

        seatNumberLabel = new JLabel("좌석: 1번"); // DB 연결 필요
        seatNumberLabel.setFont(new Font("Arial", Font.BOLD, 12));
        seatNumberLabel.setForeground(Color.WHITE);
        infoPanel.add(seatNumberLabel);

        // 남은 시간 (실시간 차감)
        // DB 연결 필요: 세션 시작 시간과 구매한 시간으로부터 실시간 계산
        timerLabel = new JLabel("남은 시간: 01:00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        timerLabel.setForeground(new Color(100, 200, 100));
        infoPanel.add(timerLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // 중앙: 잔여 시간 (큰 글씨)
        remainingTimeLabel = new JLabel("1시간 0분");
        remainingTimeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        remainingTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        remainingTimeLabel.setForeground(new Color(100, 200, 100));
        remainingTimeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(remainingTimeLabel, BorderLayout.CENTER);

        // 하단: 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 5, 5));
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        foodOrderButton = new JButton("음식주문");
        foodOrderButton.setBackground(new Color(255, 140, 0));
        foodOrderButton.setForeground(Color.WHITE);
        foodOrderButton.setFocusPainted(false);
        foodOrderButton.setFont(new Font("Arial", Font.BOLD, 11));
        buttonPanel.add(foodOrderButton);

        reviewButton = new JButton("리뷰");
        reviewButton.setBackground(new Color(100, 150, 255));
        reviewButton.setForeground(Color.WHITE);
        reviewButton.setFocusPainted(false);
        reviewButton.setFont(new Font("Arial", Font.BOLD, 11));
        buttonPanel.add(reviewButton);

        logoutButton = new JButton("로그아웃");
        logoutButton.setBackground(new Color(200, 50, 50));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 11));
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    // DB 연결 필요: 사용자 세션 정보 설정
    public void setSessionInfo(String branchName, int seatNumber) {
        branchNameLabel.setText("지점: " + branchName);
        seatNumberLabel.setText("좌석: " + seatNumber + "번");
    }

    // 남은 시간 업데이트 (실시간)
    // DB 연결 필요: 타이머 구현 필요
    public void updateRemainingTime(int hours, int minutes, int seconds) {
        remainingTimeLabel.setText(hours + "시간 " + minutes + "분");
        timerLabel.setText(String.format("남은 시간: %02d:%02d:%02d", hours, minutes, seconds));
    }

    // 음식 주문 버튼 리스너 설정
    public void setFoodOrderButtonListener(ActionListener listener) {
        foodOrderButton.addActionListener(listener);
    }

    // 리뷰 버튼 리스너 설정
    public void setReviewButtonListener(ActionListener listener) {
        reviewButton.addActionListener(listener);
    }

    // 로그아웃 버튼 리스너 설정
    public void setLogoutButtonListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
}

// 메인 대시보드 (위젯)
package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

import view.FontUtil;

public class UserMainDashboardView extends JFrame {
	private JLabel nameLabel;
    private JLabel remainingTimeLabel;
    private JLabel seatNumberLabel;
    private JLabel branchNameLabel;
    private JLabel gradeLabel;
    private JButton foodOrderButton;
    private JButton reviewButton;
    private JButton logoutButton;
    private JButton timeChargeButton;
    private JLabel timerLabel;
    
    private int remainMinutes; 
    private int usedMinutes = 0; 
    private Timer timer;

    public UserMainDashboardView() {
        setTitle("PC방 대시보드");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 350);
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
        infoPanel.setLayout(new GridLayout(5, 1));
        infoPanel.setBackground(new Color(50, 50, 50));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        branchNameLabel = new JLabel("지점: 강남점");
        branchNameLabel.setFont(FontUtil.getKoreanFontBold(12));
        branchNameLabel.setForeground(Color.WHITE);
        infoPanel.add(branchNameLabel);
        
        nameLabel = new JLabel("이름: 로딩중...");
        nameLabel.setFont(FontUtil.getKoreanFontBold(12));
        nameLabel.setForeground(Color.WHITE);
        infoPanel.add(nameLabel);
        
        // 3. 등급 라벨 UI 초기화 및 추가
        gradeLabel = new JLabel("등급: (알 수 없음)"); 
        gradeLabel.setFont(FontUtil.getKoreanFontBold(12));
        gradeLabel.setForeground(new Color(255, 215, 0)); 
        infoPanel.add(gradeLabel);

        seatNumberLabel = new JLabel("좌석: 1번"); // DB 연결 필요
        seatNumberLabel.setFont(FontUtil.getKoreanFontBold(12));
        seatNumberLabel.setForeground(Color.WHITE);
        infoPanel.add(seatNumberLabel);

        // 남은 시간 (실시간 차감)
        // DB 연결 필요: 세션 시작 시간과 구매한 시간으로부터 실시간 계산
        timerLabel = new JLabel("남은 시간: 01:00:00");
        timerLabel.setFont(FontUtil.getKoreanFontBold(14));
        timerLabel.setForeground(new Color(100, 200, 100));
        infoPanel.add(timerLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // 중앙: 잔여 시간 (큰 글씨)
        remainingTimeLabel = new JLabel("1시간 0분");
        remainingTimeLabel.setFont(FontUtil.getKoreanFontBold(36));
        remainingTimeLabel.setHorizontalAlignment(JLabel.CENTER);
        remainingTimeLabel.setForeground(new Color(100, 200, 100));
        remainingTimeLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(remainingTimeLabel, BorderLayout.CENTER);

        // 하단: 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 5, 5));
        buttonPanel.setBackground(new Color(50, 50, 50));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 이용권 충전 버튼 
        timeChargeButton = new JButton("시간충전");
        timeChargeButton.setBackground(new Color(50, 205, 50));
        timeChargeButton.setForeground(Color.WHITE);
        timeChargeButton.setFocusPainted(false);
        timeChargeButton.setFont(FontUtil.getKoreanFontBold(11));
        buttonPanel.add(timeChargeButton);
        
        foodOrderButton = new JButton("음식주문");
        foodOrderButton.setBackground(new Color(255, 140, 0));
        foodOrderButton.setForeground(Color.WHITE);
        foodOrderButton.setFocusPainted(false);
        foodOrderButton.setFont(FontUtil.getKoreanFontBold(11));
        buttonPanel.add(foodOrderButton);

        reviewButton = new JButton("리뷰");
        reviewButton.setBackground(new Color(100, 150, 255));
        reviewButton.setForeground(Color.WHITE);
        reviewButton.setFocusPainted(false);
        reviewButton.setFont(FontUtil.getKoreanFontBold(11));
        buttonPanel.add(reviewButton);

        logoutButton = new JButton("로그아웃");
        logoutButton.setBackground(new Color(200, 50, 50));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(FontUtil.getKoreanFontBold(11));
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
    
    // 등급 표시
    public void setSessionInfo(String branchName, int seatNumber, String memberGrade, String memberName) {
        branchNameLabel.setText("지점: " + branchName);
        seatNumberLabel.setText("좌석: " + seatNumber + "번");
        
        // 이름 표시 분기 처리
        if (memberName == null || memberName.isEmpty()) {
            nameLabel.setText("이름: 비회원");
        } else {
            nameLabel.setText("이름: " + memberName + "님");
        }
        
        // 등급 표시 분기 처리
        if (memberGrade == null || memberGrade.isEmpty()) {
            gradeLabel.setText("등급: 비회원");
            gradeLabel.setForeground(Color.LIGHT_GRAY);
        } else {
            gradeLabel.setText("등급: " + memberGrade.toUpperCase());
            gradeLabel.setForeground(new Color(255, 215, 0)); // 회원일 땐 다시 황금색으로 복구
        }
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
        timerLabel.setText(String.format("남은 시간: %02d:%02d", hours, minutes));
    }

    public void setFoodOrderButtonListener(ActionListener listener) {
        foodOrderButton.addActionListener(listener);
    }

    public void setReviewButtonListener(ActionListener listener) {
        reviewButton.addActionListener(listener);
    }

    public void setLogoutButtonListener(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }
    
    public void setTimeChargeButtonListener(ActionListener listener) {
        timeChargeButton.addActionListener(listener);
    }
    
    public void startTimer(int initialRemainMinutes) {
        this.remainMinutes = initialRemainMinutes;
        this.usedMinutes = 0; // 사용 시간 초기화
        
        updateRemainingTime(remainMinutes / 60, remainMinutes % 60, 0); // 최초 1회 표시

        // 1분(60,000 밀리초)마다 실행되는 타이머
        timer = new Timer(60000, e -> {
            remainMinutes--; // 남은 시간 1분 감소
            usedMinutes++;   // 사용한 시간 1분 증가
            
            updateRemainingTime(remainMinutes / 60, remainMinutes % 60, 0); // 화면 갱신
            
            if (remainMinutes <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, "시간이 다 되었습니다. 자동으로 로그아웃됩니다.");
                logoutButton.doClick(); // 강제 로그아웃 버튼 클릭 효과
            }
        });
        timer.start();
    }

    public void addTime(int minutes) {
        this.remainMinutes += minutes;
        updateRemainingTime(remainMinutes / 60, remainMinutes % 60, 0);
    }

    public int getUsedMinutes() {
        return this.usedMinutes;
    }

    public void stopTimer() {
        if (timer != null) timer.stop();
    }
}

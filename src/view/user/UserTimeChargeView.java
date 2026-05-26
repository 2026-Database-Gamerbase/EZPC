package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * UserTimeChargeView - 시간 충전 결제 화면
 * 선불 이용권을 결제하는 화면입니다.
 * 회원의 경우 누적 금액 등급(Silver, Gold 등)에 따른 할인율이
 * 실시간 반영되어 금액이 표시됩니다.
 */
public class UserTimeChargeView extends JPanel {
    private JLabel userNameLabel;
    private JLabel userGradeLabel;
    private JLabel discountRateLabel;
    private JComboBox<String> timeOptionCombo;
    private JLabel basePriceLabel;
    private JLabel discountAmountLabel;
    private JLabel finalPriceLabel;
    private JButton paymentButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    public UserTimeChargeView() {
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // 상단: 제목
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 240, 240));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel titleLabel = new JLabel("시간권 구매");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 중앙: 메인 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // 사용자 정보
        JLabel userLabel = new JLabel("사용자:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(userLabel, gbc);
        userNameLabel = new JLabel("사용자명"); // DB 연결 필요
        userNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        centerPanel.add(userNameLabel, gbc);

        // 사용자 등급 (DB 연결 필요: 회원등급에 따른 할인율 반영)
        JLabel gradeLabel = new JLabel("회원 등급:");
        gradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(gradeLabel, gbc);
        userGradeLabel = new JLabel("Bronze"); // DB 연결 필요
        userGradeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        centerPanel.add(userGradeLabel, gbc);

        // 할인율
        JLabel discountLabel = new JLabel("할인율:");
        discountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(discountLabel, gbc);
        discountRateLabel = new JLabel("0%"); // DB 연결 필요
        discountRateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        discountRateLabel.setForeground(new Color(255, 100, 100));
        gbc.gridx = 1;
        centerPanel.add(discountRateLabel, gbc);

        // 시간 옵션 선택
        JLabel timeOptionLabel = new JLabel("시간 선택:");
        timeOptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(timeOptionLabel, gbc);
        // DB 연결 필요: 시간 옵션 및 가격 로드
        String[] timeOptions = {"1시간 - 2,000원", "3시간 - 5,500원", "5시간 - 9,000원", "10시간 - 17,000원"};
        timeOptionCombo = new JComboBox<>(timeOptions);
        gbc.gridx = 1;
        centerPanel.add(timeOptionCombo, gbc);

        // 기본 가격
        JLabel basePriceTextLabel = new JLabel("기본 가격:");
        basePriceTextLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(basePriceTextLabel, gbc);
        basePriceLabel = new JLabel("2,000원"); // DB 연결 필요
        basePriceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        centerPanel.add(basePriceLabel, gbc);

        // 할인액
        JLabel discountAmountTextLabel = new JLabel("할인액:");
        discountAmountTextLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(discountAmountTextLabel, gbc);
        discountAmountLabel = new JLabel("0원"); // DB 연결 필요
        discountAmountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        discountAmountLabel.setForeground(new Color(100, 200, 100));
        gbc.gridx = 1;
        centerPanel.add(discountAmountLabel, gbc);

        // 최종 결제액
        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel finalPriceTextLabel = new JLabel("최종 결제액:");
        finalPriceTextLabel.setFont(new Font("Arial", Font.BOLD, 16));
        centerPanel.add(finalPriceTextLabel, gbc);
        finalPriceLabel = new JLabel("2,000원"); // DB 연결 필요
        finalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        finalPriceLabel.setForeground(new Color(0, 100, 200));
        gbc.gridx = 1;
        centerPanel.add(finalPriceLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // 하단: 상태 메시지
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);

        // 하단 우측: 버튼
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        paymentButton = new JButton("결제");
        paymentButton.setPreferredSize(new Dimension(100, 35));
        cancelButton = new JButton("취소");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(paymentButton);
        buttonPanel.add(cancelButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(240, 240, 240));
        southPanel.add(statusPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    // DB 연결 필요: 사용자 정보 설정
    public void setUserInfo(String userName, String grade, int discountRate) {
        userNameLabel.setText(userName);
        userGradeLabel.setText(grade);
        discountRateLabel.setText(discountRate + "%");
    }

    // 가격 정보 설정 (DB 연결 필요)
    public void setPriceInfo(int basePrice, int discountAmount, int finalPrice) {
        basePriceLabel.setText(basePrice + "원");
        discountAmountLabel.setText(discountAmount + "원");
        finalPriceLabel.setText(finalPrice + "원");
    }

    // 선택된 시간 옵션 반환
    public String getSelectedTimeOption() {
        return (String) timeOptionCombo.getSelectedItem();
    }

    // 결제 버튼 리스너 설정
    public void setPaymentButtonListener(ActionListener listener) {
        paymentButton.addActionListener(listener);
    }

    // 취소 버튼 리스너 설정
    public void setCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
}

// 시간 충전 결제 화면 (등급별 할인 적용)
// UserTimeChargeView.java
package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import model.Ticket;
import view.FontUtil;

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

    private int currentDiscountRate = 0;
    private List<Ticket> tickets;

    public UserTimeChargeView(List<Ticket> tickets) {
        this.tickets = tickets;
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
        titleLabel.setFont(FontUtil.getKoreanFontBold(24));
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
        userLabel.setFont(FontUtil.getKoreanFontBold(14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(userLabel, gbc);
        userNameLabel = new JLabel("사용자명");
        userNameLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gbc.gridx = 1;
        centerPanel.add(userNameLabel, gbc);

        // 사용자 등급
        JLabel gradeLabel = new JLabel("회원 등급:");
        gradeLabel.setFont(FontUtil.getKoreanFontBold(14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(gradeLabel, gbc);
        userGradeLabel = new JLabel("Bronze");
        userGradeLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gbc.gridx = 1;
        centerPanel.add(userGradeLabel, gbc);

        // 할인율
        JLabel discountLabel = new JLabel("할인율:");
        discountLabel.setFont(FontUtil.getKoreanFontBold(14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(discountLabel, gbc);
        discountRateLabel = new JLabel("0%");
        discountRateLabel.setFont(FontUtil.getKoreanFontPlain(14));
        discountRateLabel.setForeground(new Color(255, 100, 100));
        gbc.gridx = 1;
        centerPanel.add(discountRateLabel, gbc);

        // 시간 옵션 선택
        JLabel timeOptionLabel = new JLabel("시간 선택:");
        timeOptionLabel.setFont(FontUtil.getKoreanFontBold(14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(timeOptionLabel, gbc);
        
        timeOptionCombo = new JComboBox<>();
        for (Ticket t : tickets) {
            int hours = t.getTicketTime() / 60;
            int mins  = t.getTicketTime() % 60;
            String label = (hours > 0 ? hours + "시간" : "") + (mins > 0 ? " " + mins + "분" : "");
            timeOptionCombo.addItem(label.trim() + " - " + String.format("%,d", t.getPrice()) + "원");
        }
        
        // 💡 콤보박스 값이 바뀔 때마다 가격을 다시 계산하도록 이벤트 추가
        timeOptionCombo.addActionListener(e -> updatePriceDisplay());
        
        gbc.gridx = 1;
        centerPanel.add(timeOptionCombo, gbc);

        // 기본 가격
        JLabel basePriceTextLabel = new JLabel("기본 가격:");
        basePriceTextLabel.setFont(FontUtil.getKoreanFontBold(14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(basePriceTextLabel, gbc);
        basePriceLabel = new JLabel("2,000원");
        basePriceLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gbc.gridx = 1;
        centerPanel.add(basePriceLabel, gbc);

        // 할인액
        JLabel discountAmountTextLabel = new JLabel("할인액:");
        discountAmountTextLabel.setFont(FontUtil.getKoreanFontBold(14));
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(discountAmountTextLabel, gbc);
        discountAmountLabel = new JLabel("0원");
        discountAmountLabel.setFont(FontUtil.getKoreanFontPlain(14));
        discountAmountLabel.setForeground(new Color(100, 200, 100));
        gbc.gridx = 1;
        centerPanel.add(discountAmountLabel, gbc);

        // 최종 결제액
        gbc.gridy = 6;
        gbc.gridx = 0;
        JLabel finalPriceTextLabel = new JLabel("최종 결제액:");
        finalPriceTextLabel.setFont(FontUtil.getKoreanFontBold(16));
        centerPanel.add(finalPriceTextLabel, gbc);
        finalPriceLabel = new JLabel("2,000원");
        finalPriceLabel.setFont(FontUtil.getKoreanFontBold(16));
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

    // 가격을 다시 계산하여 화면에 표시하는 메서드
    private void updatePriceDisplay() {
        Ticket t = getSelectedTicket();
        if (t == null) return;
        int basePrice     = t.getPrice();
        int discountAmount = basePrice * currentDiscountRate / 100;
        setPriceInfo(basePrice, discountAmount, basePrice - discountAmount);
    }

    // 현재 선택된 Ticket 객체 반환
    public Ticket getSelectedTicket() {
        int idx = timeOptionCombo.getSelectedIndex();
        return (idx >= 0 && idx < tickets.size()) ? tickets.get(idx) : null;
    }

    // 사용자 정보 설정할 때 초기 가격 표시도 갱신
    public void setUserInfo(String userName, String grade, int discountRate) {
        userNameLabel.setText(userName);
        userGradeLabel.setText(grade);
        discountRateLabel.setText(discountRate + "%");
        
        this.currentDiscountRate = discountRate;
        updatePriceDisplay();
    }

    // 가격 정보 설정
    public void setPriceInfo(int basePrice, int discountAmount, int finalPrice) {
        basePriceLabel.setText(basePrice + "원");
        discountAmountLabel.setText(discountAmount + "원");
        finalPriceLabel.setText(finalPrice + "원");
    }

    public String getSelectedTimeOption() {
        return (String) timeOptionCombo.getSelectedItem();
    }

    public void setPaymentButtonListener(ActionListener listener) {
        paymentButton.addActionListener(listener);
    }

    public void setCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }
}

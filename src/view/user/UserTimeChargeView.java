// 시간 충전 결제 화면 (등급별 + 이벤트 할인 적용)
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
    private JLabel gradeDiscountLabel;
    private JLabel eventDiscountLabel;     // 이벤트 할인율 표시
    private JComboBox<String> timeOptionCombo;
    private JLabel basePriceLabel;
    private JLabel discountAmountLabel;
    private JLabel finalPriceLabel;
    private JButton paymentButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    private int currentGradeDiscountRate = 0;   // 등급 할인율 (%)
    private double currentEventPaymentRate = 1.00; // 이벤트 결제비율 (1.0 = 할인없음)
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
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // 사용자
        addLabel(centerPanel, gbc, row, "사용자:");
        userNameLabel = new JLabel("사용자명");
        userNameLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(userNameLabel, gbc);

        // 회원 등급
        addLabel(centerPanel, gbc, row, "회원 등급:");
        userGradeLabel = new JLabel("브론즈");
        userGradeLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(userGradeLabel, gbc);

        // 등급 할인율
        addLabel(centerPanel, gbc, row, "등급 할인:");
        gradeDiscountLabel = new JLabel("0%");
        gradeDiscountLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gradeDiscountLabel.setForeground(new Color(255, 100, 100));
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(gradeDiscountLabel, gbc);

        // 이벤트 할인율
        addLabel(centerPanel, gbc, row, "이벤트 할인:");
        eventDiscountLabel = new JLabel("없음");
        eventDiscountLabel.setFont(FontUtil.getKoreanFontPlain(14));
        eventDiscountLabel.setForeground(new Color(255, 100, 100));
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(eventDiscountLabel, gbc);

        // 구분선
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        centerPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;

        // 시간 선택
        addLabel(centerPanel, gbc, row, "시간 선택:");
        timeOptionCombo = new JComboBox<>();
        for (Ticket t : tickets) {
            int hours = t.getTicketTime() / 60;
            int mins  = t.getTicketTime() % 60;
            String label = (hours > 0 ? hours + "시간" : "") + (mins > 0 ? " " + mins + "분" : "");
            timeOptionCombo.addItem(label.trim() + " - " + String.format("%,d", t.getPrice()) + "원");
        }
        timeOptionCombo.addActionListener(e -> updatePriceDisplay());
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(timeOptionCombo, gbc);

        // 기본 가격
        addLabel(centerPanel, gbc, row, "기본 가격:");
        basePriceLabel = new JLabel("0원");
        basePriceLabel.setFont(FontUtil.getKoreanFontPlain(14));
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(basePriceLabel, gbc);

        // 할인액
        addLabel(centerPanel, gbc, row, "할인액:");
        discountAmountLabel = new JLabel("0원");
        discountAmountLabel.setFont(FontUtil.getKoreanFontPlain(14));
        discountAmountLabel.setForeground(new Color(100, 200, 100));
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(discountAmountLabel, gbc);

        // 최종 결제액
        addLabel(centerPanel, gbc, row, "최종 결제액:");
        ((JLabel) centerPanel.getComponent(centerPanel.getComponentCount() - 1))
                .setFont(FontUtil.getKoreanFontBold(16));
        finalPriceLabel = new JLabel("0원");
        finalPriceLabel.setFont(FontUtil.getKoreanFontBold(16));
        finalPriceLabel.setForeground(new Color(0, 100, 200));
        gbc.gridx = 1; gbc.gridy = row++;
        centerPanel.add(finalPriceLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // 하단
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(240, 240, 240));
        statusPanel.add(statusLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        paymentButton = new JButton("결제");
        paymentButton.setPreferredSize(new Dimension(100, 35));
        cancelButton  = new JButton("취소");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        buttonPanel.add(paymentButton);
        buttonPanel.add(cancelButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(new Color(240, 240, 240));
        southPanel.add(statusPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, int row, String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontUtil.getKoreanFontBold(14));
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(label, gbc);
    }

    // 가격 계산: 기본가 * 이벤트비율 * (1 - 등급할인율)
    private void updatePriceDisplay() {
        Ticket t = getSelectedTicket();
        if (t == null) return;

        int basePrice  = t.getPrice();
        // 프로시저와 동일한 공식
        int finalPrice = (int) Math.round(basePrice * currentEventPaymentRate * (1.0 - currentGradeDiscountRate / 100.0));
        int discountAmount = basePrice - finalPrice;

        basePriceLabel.setText(String.format("%,d원", basePrice));
        discountAmountLabel.setText(String.format("%,d원", discountAmount));
        finalPriceLabel.setText(String.format("%,d원", finalPrice));
    }

    // 현재 선택된 Ticket 객체 반환
    public Ticket getSelectedTicket() {
        int idx = timeOptionCombo.getSelectedIndex();
        return (idx >= 0 && idx < tickets.size()) ? tickets.get(idx) : null;
    }

    /**
     * 사용자 정보 및 할인율 세팅
     * @param eventPaymentRate  1.0 = 이벤트 없음, 0.9 = 10% 이벤트 할인
     */
    public void setUserInfo(String userName, String grade, int gradeDiscountRate, double eventPaymentRate) {
        userNameLabel.setText(userName);
        userGradeLabel.setText(grade);

        // 등급 할인 표시
        gradeDiscountLabel.setText(gradeDiscountRate + "%");

        // 이벤트 할인 표시
        if (eventPaymentRate < 1.00) {
            int eventDiscountPercent = (int) Math.round((1.0 - eventPaymentRate) * 100);
            eventDiscountLabel.setText(eventDiscountPercent + "% (이벤트 진행 중)");
            eventDiscountLabel.setForeground(new Color(200, 80, 0)); // 주황색으로 강조
        } else {
            eventDiscountLabel.setText("없음");
            eventDiscountLabel.setForeground(Color.GRAY);
        }

        this.currentGradeDiscountRate  = gradeDiscountRate;
        this.currentEventPaymentRate   = eventPaymentRate;
        updatePriceDisplay();
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

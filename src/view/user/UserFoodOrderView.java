package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.Food;
import view.FontUtil;

/**
 * UserFoodOrderView - 음식 주문 팝업 창
 * 해당 지점의 음식 재고(stock) 상황을 파악해 메뉴를 보여주며,
 * 진행 중인 이벤트가 있다면 할인율(payment_rate)을 적용해 주문을 생성합니다.
 */
public class UserFoodOrderView extends JDialog {
    private JTable foodMenuTable;
    private JLabel eventDiscountLabel;
    private JSpinner quantitySpinner;
    private JLabel totalPriceLabel;
    private JButton orderButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private DefaultTableModel tableModel;
    private double paymentRate = 1.0;

    public UserFoodOrderView(JFrame parent) {
        super(parent, "음식 주문", true);
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // 상단: 제목 및 이벤트 할인 정보
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("음식 주문");
        titleLabel.setFont(FontUtil.getKoreanFontBold(20));
        topPanel.add(titleLabel);

        // DB 연결 필요: 진행 중인 이벤트 확인 및 할인율 반영
        eventDiscountLabel = new JLabel("현재 이벤트: 없음 | 할인율: 0%");
        eventDiscountLabel.setFont(FontUtil.getKoreanFontPlain(12));
        eventDiscountLabel.setForeground(new Color(255, 100, 50));
        topPanel.add(eventDiscountLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 중앙: 메뉴 테이블
        // DB 연결 필요: 해당 지점의 재고가 있는 음식 메뉴 로드
        String[] columnNames = {"음식명", "기본가격", "현재가격", "재고"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        foodMenuTable = new JTable(tableModel);
        foodMenuTable.setFont(FontUtil.getKoreanFontPlain(12));
        foodMenuTable.setRowHeight(25);
        foodMenuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(foodMenuTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 하단: 주문 정보 및 버튼
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(3, 1));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 수량 선택
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setBackground(new Color(240, 240, 240));
        quantityPanel.add(new JLabel("수량:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantityPanel.add(quantitySpinner);
        bottomPanel.add(quantityPanel);

        // 총 가격
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setBackground(new Color(240, 240, 240));
        JLabel totalLabel = new JLabel("총 가격:");
        totalLabel.setFont(FontUtil.getKoreanFontBold(14));
        totalPriceLabel = new JLabel("0원");
        totalPriceLabel.setFont(FontUtil.getKoreanFontBold(16));
        totalPriceLabel.setForeground(new Color(0, 100, 200));
        pricePanel.add(totalLabel);
        pricePanel.add(totalPriceLabel);
        bottomPanel.add(pricePanel);

        // 상태 메시지 및 버튼
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        actionPanel.setBackground(new Color(240, 240, 240));
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        actionPanel.add(statusLabel);

        JPanel buttonSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonSubPanel.setBackground(new Color(240, 240, 240));
        orderButton = new JButton("주문");
        orderButton.setPreferredSize(new Dimension(100, 35));
        cancelButton = new JButton("취소");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        buttonSubPanel.add(orderButton);
        buttonSubPanel.add(cancelButton);
        actionPanel.add(buttonSubPanel);

        bottomPanel.add(actionPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    // DB 연결 필요: 이벤트 할인율 설정
    public void setEventDiscount(String eventName, int discountRate) {
        eventDiscountLabel.setText("현재 이벤트: " + eventName + " | 할인율: " + discountRate + "%");
    }

    // 선택된 음식 정보 반환
    public int getSelectedFoodRow() {
        return foodMenuTable.getSelectedRow();
    }

    public String getSelectedFoodName() {
        int row = foodMenuTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 0);
        }
        return null;
    }

    public int getSelectedFoodPrice() {
        int row = foodMenuTable.getSelectedRow();
        if (row >= 0) {
            return (int) tableModel.getValueAt(row, 2); // 할인 적용된 가격
        }
        return 0;
    }

    // 선택된 수량 반환
    public int getSelectedQuantity() {
        return (int) quantitySpinner.getValue();
    }

    // 총 가격 설정
    public void setTotalPrice(int totalPrice) {
        totalPriceLabel.setText(totalPrice + "원");
    }

    // 상태 메시지 설정
    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 주문 버튼 리스너 설정
    public void setOrderButtonListener(ActionListener listener) {
        orderButton.addActionListener(listener);
    }

    // 취소 버튼 리스너 설정
    public void setCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }

    public void setMenuData(List<Food> foods, Map<String, Integer> stockMap, double paymentRate) {
        this.paymentRate = paymentRate;
        tableModel.setRowCount(0);
        for (Food food : foods) {
            int basePrice = food.getPrice();
            int currentPrice = (int) Math.floor(basePrice * paymentRate);
            int stockQty = stockMap.getOrDefault(food.getFoodName(), 0);
            tableModel.addRow(new Object[]{food.getFoodName(), basePrice, currentPrice, stockQty + "개"});
        }
        if (paymentRate >= 1.0) {
            setEventDiscount("없음", 0);
        } else {
            int discountRate = (int) Math.round((1.0 - paymentRate) * 100);
            setEventDiscount("할인 중", discountRate);
        }
        setTotalPrice(getSelectedFoodPrice() * getSelectedQuantity());
    }

    public void setFoodTableSelectionListener(ListSelectionListener listener) {
        foodMenuTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void setQuantityChangeListener(ChangeListener listener) {
        quantitySpinner.addChangeListener(listener);
    }
}

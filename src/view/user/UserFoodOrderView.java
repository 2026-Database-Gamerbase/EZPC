// 음식 주문창 (전체화면 및 추천 문구 추가 버전)
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

public class UserFoodOrderView extends JDialog {
    private JTable foodMenuTable;
    private JLabel eventDiscountLabel;
    private JSpinner quantitySpinner;
    private JLabel totalPriceLabel;
    private JLabel recommendLabel; // 👈 추가: 추천 문구 라벨
    private JButton orderButton;
    private JButton cancelButton;
    private JLabel statusLabel;
    private DefaultTableModel tableModel;
    private double paymentRate = 1.0;

    public UserFoodOrderView(JFrame parent) {
        super(parent, "음식 주문", true);
        
        // 1. [수정] 사용자의 모니터 화면 전체 크기를 가져와서 설정
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height); 
        setLocationRelativeTo(null); // 화면 정중앙 배치
        setResizable(true);           // 사용자가 창 크기를 조절할 수 있도록 허용

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
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("음식 주문");
        titleLabel.setFont(FontUtil.getKoreanFontBold(24)); // 전체화면이므로 폰트 키움
        topPanel.add(titleLabel);

        eventDiscountLabel = new JLabel("현재 이벤트: 없음 | 할인율: 0%");
        eventDiscountLabel.setFont(FontUtil.getKoreanFontPlain(14));
        eventDiscountLabel.setForeground(new Color(255, 100, 50));
        topPanel.add(eventDiscountLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 중앙: 메뉴 테이블 (BorderLayout.CENTER에 있으므로 창 크기에 따라 자동으로 최대화됨)
        String[] columnNames = {"음식명", "기본가격", "현재가격", "재고"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        foodMenuTable = new JTable(tableModel);
        foodMenuTable.setFont(FontUtil.getKoreanFontPlain(14)); // 표 글씨 크기 키움
        foodMenuTable.setRowHeight(30); // 행 높이도 보기 편하게 키움
        foodMenuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(foodMenuTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 하단: 주문 정보 및 버튼들 (전체화면 시 세로로 길어지는 버그 방지를 위해 BoxLayout 사용)
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); 
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 수량 선택 패널
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quantityPanel.setBackground(new Color(240, 240, 240));
        JLabel qLabel = new JLabel("수량:");
        qLabel.setFont(FontUtil.getKoreanFontBold(14));
        quantityPanel.add(qLabel);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setPreferredSize(new Dimension(80, 25));
        quantityPanel.add(quantitySpinner);
        bottomPanel.add(quantityPanel);

        // 총 가격 패널
        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pricePanel.setBackground(new Color(240, 240, 240));
        JLabel totalLabel = new JLabel("총 가격:");
        totalLabel.setFont(FontUtil.getKoreanFontBold(16));
        totalPriceLabel = new JLabel("0원");
        totalPriceLabel.setFont(FontUtil.getKoreanFontBold(20));
        totalPriceLabel.setForeground(new Color(0, 100, 200));
        pricePanel.add(totalLabel);
        pricePanel.add(totalPriceLabel);
        bottomPanel.add(pricePanel);

        // 2. [추가] 추천 상품 메시지 패널
        JPanel recommendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recommendPanel.setBackground(new Color(240, 240, 240));
        recommendLabel = new JLabel("💡 메뉴를 선택하시면 함께 많이 주문한 음식을 추천해 드려요!");
        recommendLabel.setFont(FontUtil.getKoreanFontPlain(14));
        recommendLabel.setForeground(new Color(30, 144, 255)); // 산뜻한 파란색
        recommendPanel.add(recommendLabel);
        bottomPanel.add(recommendPanel);

        // 상태 메시지 및 버튼 패널
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(new Color(240, 240, 240));
        
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(FontUtil.getKoreanFontPlain(13));
        actionPanel.add(statusLabel, BorderLayout.WEST);

        JPanel buttonSubPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonSubPanel.setBackground(new Color(240, 240, 240));
        orderButton = new JButton("주문");
        orderButton.setPreferredSize(new Dimension(120, 40));
        orderButton.setFont(FontUtil.getKoreanFontBold(14));
        
        cancelButton = new JButton("취소");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setFont(FontUtil.getKoreanFontBold(14));
        
        buttonSubPanel.add(orderButton);
        buttonSubPanel.add(cancelButton);
        actionPanel.add(buttonSubPanel, BorderLayout.CENTER);

        bottomPanel.add(actionPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    // 3. [추가] 컨트롤러가 추천 문구를 동적으로 변경할 수 있도록 하는 메서드
    public void setRecommendMessage(String message) {
        recommendLabel.setText(message);
    }

    public void setEventDiscount(String eventName, int discountRate) {
        eventDiscountLabel.setText("현재 이벤트: " + eventName + " | 할인율: " + discountRate + "%");
    }

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
            Object value = tableModel.getValueAt(row, 2);
            return (value instanceof Number) ? ((Number) value).intValue() : 0;
        }
        return 0;
    }

    public int getSelectedQuantity() {
        return (int) quantitySpinner.getValue();
    }

    public void setTotalPrice(int totalPrice) {
        totalPriceLabel.setText(totalPrice + "원");
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    public void setOrderButtonListener(ActionListener listener) {
        orderButton.addActionListener(listener);
    }

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
        // 4. [수정] 처음 데이터를 가져왔을 때는 선택된 게 없으므로 안전하게 0원 처리
        setTotalPrice(0);
    }

    public void setFoodTableSelectionListener(ListSelectionListener listener) {
        foodMenuTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void setQuantityChangeListener(ChangeListener listener) {
        quantitySpinner.addChangeListener(listener);
    }

    
}
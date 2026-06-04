// 음식 주문창 (전체화면 및 추천 문구 추가 버전)
package view.user;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import model.Food;
import view.FontUtil;

public class UserFoodOrderView extends JDialog {

    // ── 메뉴판 ──
    private JTable foodMenuTable;
    private DefaultTableModel menuTableModel;
    private JLabel eventDiscountLabel;
    private JSpinner quantitySpinner;
    private JLabel recommendLabel; // 추천 음식 라벨
    private JButton addToCartButton;   // 담기

    // ── 장바구니 ──
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JButton removeCartButton;   // 선택 삭제
    private JLabel cartTotalLabel;      // 합계 금액

    // ── 하단 ──
    private JButton orderButton;        // 주문하기
    private JButton cancelButton;
    private JLabel statusLabel;

    // ── 내부 상태 ──
    private double paymentRate = 1.0;
    // foodName → [quantity, unitDiscountedPrice]
    private final LinkedHashMap<String, int[]> cartItems = new LinkedHashMap<>();

    public UserFoodOrderView(JFrame parent) {
        super(parent, "음식 주문", true);
        // 1. [수정] 사용자의 모니터 화면 전체 크기를 가져와서 설정
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null); //화면 정중앙 배치
        setResizable(true); //사용자가 창 크기 조절 허용
        initializeUI();
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // ── 상단: 제목 + 이벤트 할인 ──────────────────────
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(new Color(240, 240, 240));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JLabel titleLabel = new JLabel("음식 주문");
        titleLabel.setFont(FontUtil.getKoreanFontBold(24)); //전체 화면이므로 폰트 키움
        topPanel.add(titleLabel);

        eventDiscountLabel = new JLabel("현재 이벤트: 없음 | 할인율: 0%");
        eventDiscountLabel.setFont(FontUtil.getKoreanFontPlain(14));
        eventDiscountLabel.setForeground(new Color(255, 100, 50));
        topPanel.add(eventDiscountLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // ── 중앙: 메뉴 테이블(좌) + 장바구니(우) ──────────
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setDividerSize(6);

        // 좌: 메뉴 테이블
        String[] menuCols = {"음식명", "기본가격", "현재가격", "재고"};
        menuTableModel = new DefaultTableModel(menuCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        foodMenuTable = new JTable(menuTableModel);
        foodMenuTable.setFont(FontUtil.getKoreanFontPlain(14));
        foodMenuTable.setRowHeight(30);
        foodMenuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("메뉴판"));
        menuPanel.add(new JScrollPane(foodMenuTable), BorderLayout.CENTER);

        // 담기 영역
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        addPanel.setBackground(new Color(240, 240, 240));
        JLabel qLabel = new JLabel("수량:");
        qLabel.setFont(FontUtil.getKoreanFontBold(14));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setPreferredSize(new Dimension(70, 28));
        addToCartButton = new JButton("🛒 담기");
        addToCartButton.setFont(FontUtil.getKoreanFontBold(13));
        addToCartButton.setBackground(new Color(50, 180, 100));
        addToCartButton.setForeground(Color.WHITE);
        addToCartButton.setOpaque(true);
        addToCartButton.setBorderPainted(false);
        addToCartButton.setPreferredSize(new Dimension(100, 30));
        addPanel.add(qLabel);
        addPanel.add(quantitySpinner);
        addPanel.add(addToCartButton);
        menuPanel.add(addPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(menuPanel);

        // 우: 장바구니
        String[] cartCols = {"음식명", "수량", "금액"};
        cartTableModel = new DefaultTableModel(cartCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setFont(FontUtil.getKoreanFontPlain(14));
        cartTable.setRowHeight(30);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cartTotalLabel = new JLabel("합계: 0원");
        cartTotalLabel.setFont(FontUtil.getKoreanFontBold(16));
        cartTotalLabel.setForeground(new Color(0, 100, 200));
        cartTotalLabel.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));

        removeCartButton = new JButton("선택 삭제");
        removeCartButton.setFont(FontUtil.getKoreanFontPlain(13));
        removeCartButton.setBackground(new Color(220, 80, 60));
        removeCartButton.setForeground(Color.WHITE);
        removeCartButton.setOpaque(true);
        removeCartButton.setBorderPainted(false);

        JPanel cartBottom = new JPanel(new BorderLayout());
        cartBottom.setBackground(new Color(240, 240, 240));
        cartBottom.add(cartTotalLabel, BorderLayout.WEST);
        cartBottom.add(removeCartButton, BorderLayout.EAST);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(BorderFactory.createTitledBorder("🛒 장바구니"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(cartBottom, BorderLayout.SOUTH);

        splitPane.setRightComponent(cartPanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // ── 하단: 추천 + 상태 + 버튼 ─────────────────────
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(240, 240, 240));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 15, 10, 15));

        JPanel recommendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        recommendPanel.setBackground(new Color(240, 240, 240));
        recommendLabel = new JLabel("💡 메뉴를 선택하시면 함께 많이 주문한 음식을 추천해 드려요!");
        recommendLabel.setFont(FontUtil.getKoreanFontPlain(13));
        recommendLabel.setForeground(new Color(30, 144, 255));
        recommendPanel.add(recommendLabel);
        bottomPanel.add(recommendPanel);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setBackground(new Color(240, 240, 240));

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(FontUtil.getKoreanFontPlain(13));
        actionPanel.add(statusLabel, BorderLayout.WEST);

        JPanel buttonSubPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonSubPanel.setBackground(new Color(240, 240, 240));

        orderButton = new JButton("주문하기");
        orderButton.setPreferredSize(new Dimension(120, 40));
        orderButton.setFont(FontUtil.getKoreanFontBold(14));
        orderButton.setBackground(new Color(100, 150, 255));
        orderButton.setForeground(Color.WHITE);
        orderButton.setOpaque(true);
        orderButton.setBorderPainted(false);

        cancelButton = new JButton("취소");
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setFont(FontUtil.getKoreanFontBold(14));

        buttonSubPanel.add(cancelButton);
        buttonSubPanel.add(orderButton);
        actionPanel.add(buttonSubPanel, BorderLayout.EAST);
        bottomPanel.add(actionPanel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // 선택 삭제 내부 처리 (뷰 자체에서 담당)
        removeCartButton.addActionListener(e -> removeSelectedCartItem());
    }

    // ══════════════════════════════════════════════
    //  장바구니 내부 관리
    // ══════════════════════════════════════════════

    // 현재 선택된 메뉴를 장바구니에 추가 (컨트롤러가 호출)
    public boolean addToCart(String foodName, int quantity, int unitDiscountedPrice) {
        if (foodName == null) return false;
        if (cartItems.containsKey(foodName)) {
            // 이미 담긴 경우 수량 합산
            cartItems.get(foodName)[0] += quantity;
        } else {
            cartItems.put(foodName, new int[]{quantity, unitDiscountedPrice});
        }
        refreshCartTable();
        return true;
    }

    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            setStatusMessage("삭제할 항목을 장바구니에서 선택해 주세요.");
            return;
        }
        String foodName = (String) cartTableModel.getValueAt(row, 0);
        cartItems.remove(foodName);
        refreshCartTable();
        setStatusMessage(foodName + "이(가) 장바구니에서 삭제되었습니다.");
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        int total = 0;
        for (Map.Entry<String, int[]> entry : cartItems.entrySet()) {
            int qty       = entry.getValue()[0];
            int unitPrice = entry.getValue()[1];
            int amount    = qty * unitPrice;
            total += amount;
            cartTableModel.addRow(new Object[]{entry.getKey(), qty + "개", amount + "원"});
        }
        cartTotalLabel.setText("합계: " + total + "원");
    }

    // ══════════════════════════════════════════════
    //  컨트롤러 연동 메서드
    // ══════════════════════════════════════════════

    public void setMenuData(List<Food> foods, Map<String, Integer> stockMap, double paymentRate) {
        this.paymentRate = paymentRate;
        menuTableModel.setRowCount(0);
        for (Food food : foods) {
            int base    = food.getPrice();
            int current = (int) Math.floor(base * paymentRate);
            int stock   = stockMap.getOrDefault(food.getFoodName(), 0);
            menuTableModel.addRow(new Object[]{food.getFoodName(), base, current, stock + "개"});
        }
        if (paymentRate >= 1.0) {
            setEventDiscount("없음", 0);
        } else {
            int rate = (int) Math.round((1.0 - paymentRate) * 100);
            setEventDiscount("할인 중", rate);
        }
    }

    public void setEventDiscount(String eventName, int discountRate) {
        eventDiscountLabel.setText("현재 이벤트: " + eventName + " | 할인율: " + discountRate + "%");
    }

    public void setRecommendMessage(String message) {
        recommendLabel.setText(message);
    }

    public void setStatusMessage(String message) {
        statusLabel.setText(message);
    }

    // 선택된 메뉴 정보
    public String getSelectedFoodName() {
        int row = foodMenuTable.getSelectedRow();
        return (row >= 0) ? (String) menuTableModel.getValueAt(row, 0) : null;
    }

    public int getSelectedFoodCurrentPrice() {
        int row = foodMenuTable.getSelectedRow();
        if (row < 0) return 0;
        Object val = menuTableModel.getValueAt(row, 2);
        return (val instanceof Number) ? ((Number) val).intValue() : 0;
    }

    public int getSelectedQuantity() {
        return (int) quantitySpinner.getValue();
    }

    // 장바구니 조회 (컨트롤러가 주문 시 사용)
    public Map<String, Integer> getCartQuantities() {
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, int[]> e : cartItems.entrySet()) {
            result.put(e.getKey(), e.getValue()[0]);
        }
        return result;
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public void clearCart() {
        cartItems.clear();
        refreshCartTable();
    }

    // 메뉴 테이블 재고 갱신 (주문 완료 후 호출)
    public void refreshMenuStock(Map<String, Integer> stockMap) {
        for (int i = 0; i < menuTableModel.getRowCount(); i++) {
            String name  = (String) menuTableModel.getValueAt(i, 0);
            int    stock = stockMap.getOrDefault(name, 0);
            menuTableModel.setValueAt(stock + "개", i, 3);
        }
    }

    // 리스너 등록
    public void setFoodTableSelectionListener(ListSelectionListener listener) {
        foodMenuTable.getSelectionModel().addListSelectionListener(listener);
    }

    public void setQuantityChangeListener(ChangeListener listener) {
        quantitySpinner.addChangeListener(listener);
    }

    public void setAddToCartButtonListener(ActionListener listener) {
        addToCartButton.addActionListener(listener);
    }

    public void setOrderButtonListener(ActionListener listener) {
        orderButton.addActionListener(listener);
    }

    public void setCancelButtonListener(ActionListener listener) {
        cancelButton.addActionListener(listener);
    }
}

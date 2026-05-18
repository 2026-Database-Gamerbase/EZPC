package service;

import dao.OrderDAO;
import dao.OrderDAOImpl;
import model.Order;

import java.util.List;


public class OrderService {
    private final OrderDAO orderDAO;
    private final StockService stockService;

    public OrderService() {
        this.orderDAO = new OrderDAOImpl();
        this.stockService = new StockService();
    }

    /**
     손님 음식 주문 처리 및 재고 차감 연동
     
     @param pcCafeId   PC방 지점 ID
     @param seatNum    좌석 번호
     @param foodName   음식 이름
     @param quantity   주문 수량
     @param singlePrice 음식 단가
     @return 주문 및 재고 차감 성공 시 true
     
    **/
    public boolean placeOrder(String pcCafeId, int seatNum, String foodName, int quantity, int singlePrice) {
        if (quantity <= 0) {
            System.err.println("[placeOrder]: 주문 실패 - 잘못된 주문 수량 (" + quantity + "개)");
            throw new IllegalArgumentException("음식은 최소 1개 이상 주문해야 합니다.");
        }

        stockService.reduceStock(pcCafeId, foodName, quantity);

        int newOrderId = orderDAO.getNextOrderId();
        int totalPayAmount = singlePrice * quantity;

        Order newOrder = new Order();
        newOrder.setOrderId(newOrderId);
        newOrder.setFoodName(foodName);
        newOrder.setPcCafeId(pcCafeId);
        newOrder.setSeatNum(seatNum);
        newOrder.setFoodQuantity(quantity);
        newOrder.setFoodPayAmount(totalPayAmount);

        orderDAO.insertOrder(newOrder);
        return true;
    }

    /**
     주문 번호 기준 영수증 상세 조회
     
     @param orderId 주문 번호
     @return 주문 상세 내역 리스트
     
    **/
    public List<Order> getReceipt(int orderId) {
        List<Order> receipt = orderDAO.getOrdersByOrderId(orderId);
        if (receipt == null || receipt.isEmpty()) {
            System.err.println("[getReceipt]: 주문 번호 " + orderId + "번의 내역을 찾을 수 없습니다.");
            throw new IllegalArgumentException("존재하지 않는 주문 번호입니다.");
        }
        return receipt;
    }

    /**
     지점별 전체 누적 주문 내역 조회
     
     @param pcCafeId PC방 지점 ID
     @return 지점 주문 내역 리스트
     
    **/
    public List<Order> getCafeOrderHistory(String pcCafeId) {
        List<Order> history = orderDAO.getOrdersByCafe(pcCafeId);
        if (history == null) {
            System.err.println("[getCafeOrderHistory]: " + pcCafeId + " 지점 조회 오류");
            throw new IllegalArgumentException("지점 주문 내역을 불러오는 데 실패했습니다.");
        }
        return history;
    }
}
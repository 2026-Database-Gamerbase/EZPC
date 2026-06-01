package service;

import dao.OrderDAO;
import java.sql.Connection;
import dao.FoodDAO;
import dao.EventScheduleDAO;
import daoImpl.OrderDAOImpl;
import model.Order;

import java.util.List;

import java.sql.SQLException;


public class OrderService {
	private final OrderDAO orderDAO;
    private final StockService stockService;
    private final FoodDAO foodDAO;
    private final EventScheduleDAO eventScheduleDAO;
    private final Connection conn;
    
    
    public OrderService(Connection conn, OrderDAO orderDAO, StockService stockService, FoodDAO foodDAO, EventScheduleDAO eventScheduleDAO) {
      //OrderService에 conn을 둔 이유는 placeOrder()에서 재고 차감과 주문 저장을 한 묶음으로 트랜젝션 처리하기 위함 
    	this.conn = conn;
    	this.orderDAO = orderDAO;
        this.stockService = stockService;
        this.foodDAO = foodDAO;
        this.eventScheduleDAO = eventScheduleDAO;
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
//    public boolean placeOrder(String pcCafeId, int seatNum, String foodName, int quantity, int singlePrice) {
//        if (quantity <= 0) {
//            System.err.println("[placeOrder]: 주문 실패 - 잘못된 주문 수량 (" + quantity + "개)");
//            throw new IllegalArgumentException("음식은 최소 1개 이상 주문해야 합니다.");
//        }
//
//        stockService.reduceStock(pcCafeId, foodName, quantity);
//
//        int newOrderId = orderDAO.getNextOrderId();
//        int totalPayAmount = singlePrice * quantity;
//
//        Order newOrder = new Order();
//        newOrder.setOrderId(newOrderId);
//        newOrder.setFoodName(foodName);
//        newOrder.setPcCafeId(pcCafeId);
//        newOrder.setSeatNum(seatNum);
//        newOrder.setFoodQuantity(quantity);
//        newOrder.setFoodPayAmount(totalPayAmount);
//
//        orderDAO.insertOrder(newOrder);
//        return true;
//    }
    
    public boolean placeOrder(String pcCafeId, int seatNum, String foodName, int quantity) throws SQLException {
        if (quantity <= 0) {
            System.err.println("[placeOrder]: 주문 실패 - 잘못된 주문 수량 (" + quantity + "개)");
            throw new IllegalArgumentException("음식은 최소 1개 이상 주문해야 합니다.");
        }

        // 기존 autoCommit 상태를 저장해 둔다.
        // placeOrder가 끝난 뒤 다른 DB 작업에 영향을 주지 않기 위해 finally에서 원래 상태로 복구한다.
        boolean oldAutoCommit = conn.getAutoCommit();

        try {
            // 트랜잭션 시작.
            // 기본값 autoCommit=true이면 SQL 한 줄마다 즉시 commit되므로,
            // 여러 DB 작업을 하나의 작업 단위로 묶기 위해 false로 변경한다.
            conn.setAutoCommit(false);

            // 1. 재고 차감
            stockService.reduceStock(pcCafeId, foodName, quantity);

            // 2. 새 주문 ID 생성
            int newOrderId = orderDAO.getNextOrderId();

            // 3. 음식 기본 가격 조회
            int singlePrice = foodDAO.findPriceByFoodName(foodName);

            // 4. 현재 PC방의 주문 이벤트 결제비율 조회(default: 1)
            double paymentRate = eventScheduleDAO.findCurrentOrderPaymentRate(pcCafeId);

            // 5. 최종 결제 금액 계산
            int totalPayAmount = (int) Math.floor(singlePrice * quantity * paymentRate);

            // 6. Order 객체 생성
            Order newOrder = new Order();
            newOrder.setOrderId(newOrderId);
            newOrder.setFoodName(foodName);
            newOrder.setPcCafeId(pcCafeId);
            newOrder.setSeatNum(seatNum);
            newOrder.setFoodQuantity(quantity);
            newOrder.setPaymentRate(paymentRate);
            newOrder.setFoodPayAmount(totalPayAmount);

            // 7. 주문 저장
            // 여기서 SQLException이 발생하면 catch로 이동하고 rollback된다.
            orderDAO.insertOrder(newOrder);

            // 모든 DB 작업이 성공했으므로 트랜잭션을 확정
            // 이 시점에 재고 차감과 주문 저장이 함께 DB에 반영
            conn.commit();

            return true;

        } catch (SQLException | RuntimeException e) {
            // 트랜잭션 도중 하나라도 실패하면 이전 작업들을 모두 취소한다.
            conn.rollback();

            // 실패 사실을 상위 메서드에 다시 전달한다.
            // 실패 사실만 전달한다. 
            throw e;

        } finally {
            // placeOrder가 끝난 뒤 Connection 상태를 원래대로 복구한다.
            // 같은 Connection을 다른 기능에서 계속 쓸 수 있으므로 복구
            conn.setAutoCommit(oldAutoCommit);
        }
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
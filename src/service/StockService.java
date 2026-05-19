package service;

import dao.StockDAO;
import dao.StockDAOImpl;
import model.Stock;

import java.util.List;


public class StockService {
	private final StockDAO stockDAO;

    public StockService(StockDAO stockDAO) {
        this.stockDAO = stockDAO;
    }

    /**
     특정 지점에 신규 음식 재고 최초 등록
     
     @param pcCafeId   PC방 지점 ID
     @param foodName   음식 이름
     @param initialQty 초기 재고 수량
     @return 등록 성공 시 true
     
    **/
    public boolean addInitialStock(String pcCafeId, String foodName, int initialQty) {
        if (initialQty < 0) {
            System.err.println("[addInitialStock]: 등록 실패 - 잘못된 초기 수량 (" + initialQty + "개)");
            throw new IllegalArgumentException("초기 재고는 0개 이상이어야 합니다.");
        }
        
        if (stockDAO.getStock(pcCafeId, foodName) != null) {
            System.err.println("[addInitialStock]: " + foodName + "은 이미 재고 목록에 존재합니다.");
            throw new IllegalArgumentException(foodName + "은(는) 이미 해당 지점에 등록된 재고 항목입니다.");
        }
        
        stockDAO.insertStock(new Stock(pcCafeId, foodName, initialQty));
        return true;
    }

    /**
     지점별 기존 재고 추가 입고
     
     @param pcCafeId PC방 지점 ID
     @param foodName 음식 이름
     @param amount   추가 입고 수량
     @return 입고 성공 시 true
     
    **/
    public boolean fillStock(String pcCafeId, String foodName, int amount) {
        if (amount <= 0) {
            System.err.println("[fillStock]: 입고 실패 - 잘못된 입고 수량 (" + amount + "개)");
            throw new IllegalArgumentException("입고 수량은 최소 1개 이상이어야 합니다.");
        }

        Stock stock = stockDAO.getStock(pcCafeId, foodName);
        if (stock != null) {
            int updatedQty = stock.getStockQuantity() + amount;
            stockDAO.updateStockQuantity(pcCafeId, foodName, updatedQty);
            return true;
        } else {
            System.err.println("[fillStock]: " + foodName + "은 " + pcCafeId + "에 등록되지 않은 재고 항목입니다.");
            throw new IllegalArgumentException("등록되지 않은 재고 항목입니다. 먼저 초기 재고 등록을 진행해 주세요.");
        }
    }

    /**
     주문 발생 시 지점별 재고 차감
     
     @param pcCafeId PC방 지점 ID
     @param foodName 음식 이름
     @param amount   차감할 주문 수량
     @return 차감 성공 시 true
     
    **/
    public boolean reduceStock(String pcCafeId, String foodName, int amount) {
        if (amount <= 0) {
            System.err.println("[reduceStock]: 차감 실패 - 잘못된 주문 수량 (" + amount + "개)");
            throw new IllegalArgumentException("최소 1개 이상 주문해야 합니다.");
        }

        Stock stock = stockDAO.getStock(pcCafeId, foodName);
        if (stock != null) {
            int currentQty = stock.getStockQuantity();
            if (currentQty >= amount) {
                stockDAO.updateStockQuantity(pcCafeId, foodName, currentQty - amount);
                return true;
            } else {
                System.err.println("[reduceStock]: " + foodName + " 재고 부족 (현재: " + currentQty + "개, 요청: " + amount + "개)");
                throw new IllegalArgumentException(foodName + "의 재고가 부족합니다. (현재 재고: " + currentQty + "개)");
            }
        }
        System.err.println("[reduceStock]: " + pcCafeId + "에 " + foodName + "의 재고 정보가 존재하지 않습니다.");
        throw new IllegalArgumentException("해당 지점에 상품 재고 정보가 존재하지 않습니다.");
    }

    /**
     특정 지점의 전체 재고 현황 조회
     
     @param pcCafeId PC방 지점 ID
     @return 지점 재고 리스트
     
    **/
    public List<Stock> getCafeStockList(String pcCafeId) {
        return stockDAO.getStocksByCafe(pcCafeId);
    }
}
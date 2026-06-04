package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.Stock;
import service.StockService;
import view.owner.OwnerFoodStockView;

public class FoodStockController {
    private final OwnerFoodStockView view;
    private final StockService stockService;
    private String currentBranchId;

    public FoodStockController(OwnerFoodStockView view, StockService stockService) {
        this.view = view;
        this.stockService = stockService;
        initEventBindings();
    }

    private void initEventBindings() {
        // 재고 업데이트 버튼
        view.setUpdateButtonListener(e -> handleUpdateStock());
        
        // 새로고침 버튼
        view.setRefreshButtonListener(e -> {
            if (currentBranchId != null) {
                refreshStockList(currentBranchId);
                view.setStatusMessage("재고 현황이 새로고침되었습니다.");
            }
        });
    }

    // 선택한 지점 재고 목록 불러와서 테이블 세팅
    public void refreshStockList(String branchId) {
        this.currentBranchId = branchId;
        List<Stock> stockList = stockService.getCafeStockList(branchId);
        
        Object[][] tableData = new Object[stockList.size()][2];
        for (int i = 0; i < stockList.size(); i++) {
            Stock stock = stockList.get(i);
            
            tableData[i][0] = stock.getFoodName();
            tableData[i][1] = stock.getStockQuantity();
        }
        view.setStockTableData(tableData);
    }

    // 선택한 음식 재고 추가 입고
    private void handleUpdateStock() {
        String selectedFood = view.getSelectedFoodName();
        if (selectedFood == null) {
            JOptionPane.showMessageDialog(view, "재고를 업데이트할 음식을 테이블에서 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int amount = view.getQuantity();
        if (amount <= 0) {
            JOptionPane.showMessageDialog(view, "추가 입고할 수량은 1개 이상이어야 합니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = stockService.fillStock(currentBranchId, selectedFood, amount);
            if (success) {
                view.setStatusMessage("[" + selectedFood + "] 재고 " + amount + "개 추가 입고 완료");
                view.clearInputForm();
                refreshStockList(currentBranchId); // 테이블 갱신
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "경고", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            view.setStatusMessage("재고 업데이트 중 시스템 오류가 발생했습니다.");
            ex.printStackTrace();
        }
    }
}
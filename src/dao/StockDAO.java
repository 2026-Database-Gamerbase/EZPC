package dao;

import model.Stock;
import java.util.List;

public interface StockDAO {

	void insertStock(Stock stock);
    void updateStockQuantity(String pcCafeId, String foodName, int newQuantity);
    void deleteStock(String pcCafeId, String foodName);
    
    Stock getStock(String pcCafeId, String foodName);

    List<Stock> getStocksByCafe(String pcCafeId);
    
}
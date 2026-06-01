package dao;

import model.Food;
import java.util.List;

public interface FoodDAO {
	
    void insertFood(Food food);
    void updateFood(Food food);
    void deleteFood(String foodName);
    
    Food getFoodByName(String foodName);
    int findPriceByFoodName(String foodName);
    List<Food> getAllFoods();
}
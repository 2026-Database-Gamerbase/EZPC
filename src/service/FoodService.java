package service;

import dao.FoodDAO;
import dao.FoodDAOImpl;
import model.Food;

import java.util.List;


public class FoodService {
    private final FoodDAO foodDAO;

    public FoodService(FoodDAO foodDAO) {
        this.foodDAO = foodDAO;
    }

    /**
     신규 음식 메뉴 검증 및 등록

     @param name  음식 이름
     @param price 음식 가격
     @return 등록 성공 시 true
     
    **/
    public boolean registerFood(String name, int price) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("[registerFood]: 등록 실패 - 음식 이름 누락");
            throw new IllegalArgumentException("음식 이름을 입력해 주세요.");
        }
        if (price < 0) {
            System.err.println("[registerFood]: 등록 실패 - 잘못된 가격 (" + price + "원)");
            throw new IllegalArgumentException("음식 가격은 0원 이상이어야 합니다.");
        }
        
        if (foodDAO.getFoodByName(name) != null) {
            System.err.println("[registerFood]: " + name + "은 이미 존재하는 음식 메뉴입니다.");
            throw new IllegalArgumentException(name + "은(는) 이미 등록된 메뉴입니다.");
        }
        
        foodDAO.insertFood(new Food(name, price));
        return true;
    }

    /**
     음식 이름으로 단건 상세 조회
     
     @param name 조회할 음식 이름
     @return Food 객체
     
    **/
    public Food findFood(String name) {
        return foodDAO.getFoodByName(name);
    }

    /**
     전체 음식 메뉴판 조회

     @return 전체 음식 리스트
     
    **/
    public List<Food> getMenuBoard() {
        return foodDAO.getAllFoods();
    }

    /**
     기존 음식의 가격 수정
     
     @param name      가격을 수정할 음식 이름
     @param newPrice  새로 변경할 가격
     @return 수정 성공 시 true
     
    **/
    public boolean modifyFoodPrice(String name, int newPrice) {
        if (newPrice < 0) {
            System.err.println("[modifyFoodPrice]: 수정 실패 - 잘못된 가격 (" + newPrice + "원)");
            throw new IllegalArgumentException("변경할 가격은 0원 이상이어야 합니다.");
        }

        Food food = foodDAO.getFoodByName(name);
        if (food == null) {
            System.err.println("[modifyFoodPrice]: " + name + "을(를) 찾을 수 없습니다.");
            throw new IllegalArgumentException(name + "은(는) 존재하지 않는 음식 메뉴입니다.");
        }

        food.setPrice(newPrice);
        foodDAO.updateFood(food);
        return true;
    }

    /**
     특정 음식 메뉴 삭제
     
     @param name 삭제할 음식 이름
     @return 삭제 성공 시 true
     
    **/
    public boolean removeFood(String name) {
        if (foodDAO.getFoodByName(name) == null) {
            System.err.println("[removeFood]: " + name + "을(를) 찾을 수 없습니다.");
            throw new IllegalArgumentException(name + "은(는) 존재하지 않는 음식 메뉴입니다.");
        }

        foodDAO.deleteFood(name);
        return true;
    }
}
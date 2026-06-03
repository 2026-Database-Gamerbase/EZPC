package daoImpl;

import model.Food;
import model.Order;
import model.FoodRankingReport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.OrderDAO;


public class OrderDAOImpl implements OrderDAO {

    private final Connection conn;

    public OrderDAOImpl(Connection conn) {
        this.conn = conn;
    }

 //throws SQLException -> DB 작업 중 오류가 발생하면 이 메서드에서 처리하지 않고 호출한 상위 메서드로 예외를 전달한다.
 // catch 에서 출력만 하고 끝내면 호출자는 실패를 알 수 없어 정상 처리된 것으로 오해할 수 있다.
    @Override
    public void insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO food_order (order_id, food_name, pc_cafe_id, seat_num, food_quantity,payment_rate, food_pay_amount) VALUES (?, ?, ?, ?, ?,?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, order.getOrderId());
            pstmt.setString(2, order.getFoodName());
            pstmt.setString(3, order.getPcCafeId());
            pstmt.setInt(4, order.getSeatNum());
            pstmt.setInt(5, order.getFoodQuantity());
            pstmt.setDouble(6, order.getPaymentRate());
            pstmt.setInt(7, order.getFoodPayAmount());
            pstmt.executeUpdate();
        } 
    }

    @Override
    public int getNextOrderId() {
        String sql = "SELECT IFNULL(MAX(order_id), 0) + 1 AS next_id FROM food_order";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("next_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public List<Order> getOrdersByOrderId(int orderId) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM food_order WHERE order_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("food_name"),
                        rs.getString("pc_cafe_id"),
                        rs.getInt("seat_num"),
                        rs.getInt("food_quantity"),
                        rs.getInt("food_pay_amount"),
                        rs.getDouble("payment_rate"),
                        rs.getTimestamp("ordered_at")
                    );
                    orderList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    @Override
    public List<Order> getOrdersByCafe(String pcCafeId) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM food_order WHERE pc_cafe_id = ? ORDER BY ordered_at DESC";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getString("food_name"),
                        rs.getString("pc_cafe_id"),
                        rs.getInt("seat_num"),
                        rs.getInt("food_quantity"),
                        rs.getInt("food_pay_amount"),
                        rs.getDouble("payment_rate"),
                        rs.getTimestamp("ordered_at")
                    );
                    orderList.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    
     //이 음식을 먹은 손님이 함께 주문한 상위 3개 상품 추천
     //3단계 CTE - BasketWithTarget → CoOccurrence → Ranked
     //BasketWithTarget = 타켓 음식이 담긴 모든 주문의 order_id 추출
     //CoOccurrence = 타겟 음식이 담긴 모든 주문의 order_id로 food_order와 join해서 타켓 음식과 함께 주문된 음식들을 추출
     //Ranked = 같이 주문된 음식들의 순위를 윈도우 함수로 매김
    @Override
    public List<Food> getRecommendedFoods(String targetFoodName) {
    	//추천될 음식 3개를 저장할 리스트
        List<Food> result = new ArrayList<>();

        String sql =
            "WITH " +
            //1단계 - 타켓 음식이 담긴 모든 주문의 order_id를 추출함
            "BasketWithTarget AS ( " +
            "    SELECT DISTINCT order_id " +
            "    FROM food_order " +
            "    WHERE food_name = ? " + //food_name = targetFoodName
            "), " +
            //2단계 - 타켓 음식이 담긴 주문에 함께 담긴 다른 음식 집계
            "CoOccurrence AS ( " +
            "    SELECT " +
            "        fo.food_name                AS food_name, " + //타켓 음식과 같이 주문한 음식의 이름
            "        COUNT(DISTINCT fo.order_id) AS co_count, " +
            "        SUM(fo.food_quantity)        AS total_qty " +
            "    FROM food_order fo " +
            "    JOIN BasketWithTarget b ON fo.order_id = b.order_id " + //food_order를 food_order와 타켓 음식이 담긴 주문의 order_id로 join
            "    WHERE fo.food_name <> ? " + //타켓 음식이 아닌 음식을 고름, 즉 타켓 음식과 함께 주문한 다른 음식의 이름을 구함
            "    GROUP BY fo.food_name " +
            "), " +
            //3단계 - 윈도우 함수로 순위 부여
            "Ranked AS ( " +
            "    SELECT " +
            "        c.food_name, " +
            "        c.co_count, " +
            "        RANK() OVER (ORDER BY c.co_count DESC, c.total_qty DESC) AS rnk " +
            "    FROM CoOccurrence c " +
            ") " +
            "SELECT r.rnk, f.food_name, f.price, r.co_count " + //순위, 음식 이름, 가격, 같이 주문한 횟수
            "FROM Ranked r " +
            "JOIN food f ON r.food_name = f.food_name " +
            "WHERE r.rnk <= 3 " + //상위 3개 추천 음식만 보여줌
            "ORDER BY r.rnk, f.food_name";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, targetFoodName);
            pstmt.setString(2, targetFoodName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Food( //같이 추천할 음식의 이름과 가격을 리스트에 저장함. 저장한 순서대로 1, 2, 3위임
                        rs.getString("food_name"),
                        rs.getInt("price")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    // 해당 PC방의 가장 잘 팔린 음식 TOP5 
    // (음식, 음식 가격, 음식 총 판매 수량, 음식 총 결제 금액 출력 가능)
    @Override
    public List<FoodRankingReport> getTop5FoodRankingByPcCafe(String pcCafeId) {
        List<FoodRankingReport> list = new ArrayList<>();

        String sql =
        		//food_order와 food를 조인해서 food의 price를 select하고 
        		//pcCafeId, foodName, foodPrice, 
        		//SUM(fo.food_quantity) -> totalQuantity, SUM(fo.food_pay_amount)-> totalSales를 select
            "WITH FoodSales AS ( " +
            "    SELECT " +
            "        fo.food_name AS foodName, " +
            "        f.price AS foodPrice, " +
            "        SUM(fo.food_quantity) AS totalQuantity, " +
            "        SUM(fo.food_pay_amount) AS totalSales, " +
           //DENSE_RANK: 총 수량(2순위:총매출) 을 기준으로 랭크를 매김 
           //pc방이 고정되어있기 때문에 PARTITION BY로 PC방마다 순위를 매길 필요가 없음 
            "        DENSE_RANK() OVER ( " +
            "            ORDER BY SUM(fo.food_quantity) DESC, SUM(fo.food_pay_amount) DESC " +
            "        ) AS ranking " +
            "    FROM food_order fo " +
            "    JOIN food f ON fo.food_name = f.food_name " +
            "    WHERE fo.pc_cafe_id = ? " +
            "    GROUP BY fo.food_name, f.price " +
            ") " +
            //ranking <= 5을 조건문으로 쓰기위해 CTE(FoodSales) 생성
            "SELECT ranking, foodName, foodPrice, totalQuantity, totalSales " +
            "FROM FoodSales " +
            "WHERE ranking <= 5 " +
            "ORDER BY ranking";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, pcCafeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                	FoodRankingReport report = new FoodRankingReport();
                    report.setRanking(rs.getInt("ranking"));
                    report.setFoodName(rs.getString("foodName"));
                    report.setFoodPrice(rs.getInt("foodPrice"));
                    report.setTotalQuantity(rs.getInt("totalQuantity"));
                    report.setTotalSales(rs.getInt("totalSales"));
                    list.add(report);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("지점별 음식 판매 랭킹 TOP 5 조회 중 오류 발생");
        }

        return list;
    }
    
    
    
 // 전체 PC방의 지점별 음식 판매 TOP 5
   //(PC방id,ranking ,음식, 음식 가격, 음식 총 판매 수량, 음식 총 결제 금액 출력 가능)
    @Override
    public List<FoodRankingReport> getTop5FoodRankingAllPcCafe() {
        List<FoodRankingReport> list = new ArrayList<>();

        String sql =
        		//food_order와 food를 조인해서 food의 price를 select하고 
        		//pcCafeId, foodName, foodPrice, 
        		//SUM(fo.food_quantity) -> totalQuantity, SUM(fo.food_pay_amount)-> totalSales를 select
            "WITH FoodSales AS ( " +
            "    SELECT " +
            "        fo.pc_cafe_id AS pcCafeId, " +
            "        fo.food_name AS foodName, " +
            "        f.price AS foodPrice, " +
            "        SUM(fo.food_quantity) AS totalQuantity, " +
            "        SUM(fo.food_pay_amount) AS totalSales, " +
            
            //PC방마다 순위를 부여해야하기 떄문에 GROUP BY 가 아닌 PARTITION BY 사용
            "        DENSE_RANK() OVER ( " +
            "            PARTITION BY fo.pc_cafe_id " +
            "            ORDER BY SUM(fo.food_quantity) DESC, SUM(fo.food_pay_amount) DESC " +
            "        ) AS ranking " +
            "    FROM food_order fo " +
            "    JOIN food f ON fo.food_name = f.food_name " +
            "    GROUP BY fo.pc_cafe_id, fo.food_name, f.price " +
            ") " +
            //ranking <= 5을 조건문으로 쓰기위해 CTE(FoodSales) 생성 
            "SELECT pcCafeId, ranking, foodName, foodPrice, totalQuantity, totalSales " +
            "FROM FoodSales " +
            "WHERE ranking <= 5 " +
            "ORDER BY pcCafeId, ranking";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                FoodRankingReport report = new FoodRankingReport();
                report.setPcCafeId(rs.getString("pcCafeId"));
                report.setRanking(rs.getInt("ranking"));
                report.setFoodName(rs.getString("foodName"));
                report.setFoodPrice(rs.getInt("foodPrice"));
                report.setTotalQuantity(rs.getInt("totalQuantity"));
                report.setTotalSales(rs.getInt("totalSales"));
                list.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("전체 지점별 음식 판매 랭킹 TOP 5 조회 중 오류 발생");
        }

        return list;
    }
}
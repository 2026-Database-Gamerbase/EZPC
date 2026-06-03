package controller;

import dao.CustomerDAO;
import dao.EventScheduleDAO;
import dao.FoodDAO;
import dao.LogDAO;
import dao.OrderDAO;
import dao.PC_MemberDAO;
import dao.PcCafeDAO;
import dao.ReviewDAO;
import dao.StockDAO;
import daoImpl.CustomerDAOImpl;
import daoImpl.EventScheduleDAOImpl;
import daoImpl.FoodDAOImpl;
import daoImpl.LogDAOImpl;
import daoImpl.OrderDAOImpl;
import daoImpl.PC_MemberDAOImpl;
import daoImpl.PcCafeDAOImpl;
import daoImpl.ReviewDAOImpl;
import daoImpl.StockDAOImpl;
import db.DatabaseConnector;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JDialog;
import model.Customer;
import model.Food;
import model.PC_Member;
import model.PcCafe;
import model.Review;
import service.CustomerService;
import service.FoodService;
import service.OrderService;
import service.PC_MemberService;
import service.PcCafeService;
import service.ReviewService;
import service.StockService;
import dao.ChargeDAO;
import daoImpl.ChargeDAOImpl;
import model.Charge;
import service.ChargeService;
import controller.LoginController;
import view.user.UserBranchSelectView;
import view.user.UserFoodOrderView;
import view.user.UserMainDashboardView;
import view.user.UserReviewManageView;
import view.user.UserSeatSelectView;
import view.user.UserTimeChargeView;


public class UserController {
    private Connection conn;
    private final String memberType;
    private final PC_Member member;
    private PcCafeService pcCafeService;
    private CustomerService customerService;
    private PC_MemberService pcMemberService;
    private FoodService foodService;
    private OrderService orderService;
    private ReviewService reviewService;
    private StockService stockService;
    private EventScheduleDAO eventScheduleDao;
    private ChargeService chargeService;

    public UserController(Connection conn, PC_Member member) {
        this.conn = conn;
        this.member = member;
        this.memberType = member.getMemberType();
        ensureOpenConnection();
        System.out.println("[UserController] connection open: " + isConnectionOpen());
    }

    private boolean isConnectionOpen() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    private void openServices() {
        PcCafeDAO pcCafeDao = new PcCafeDAOImpl(conn);
        CustomerDAO customerDao = new CustomerDAOImpl(conn);
        LogDAO logDao = new LogDAOImpl(conn);
        PC_MemberDAO memberDao = new PC_MemberDAOImpl(conn);
        FoodDAO foodDao = new FoodDAOImpl(conn);
        ReviewDAO reviewDao = new ReviewDAOImpl(conn);
        OrderDAO orderDao = new OrderDAOImpl(conn);
        StockDAO stockDao = new StockDAOImpl(conn);
        this.eventScheduleDao = new EventScheduleDAOImpl(conn);
        
        ChargeDAO chargeDao = new ChargeDAOImpl(conn);
        this.chargeService = new ChargeService(chargeDao);

        this.pcCafeService = new PcCafeService(pcCafeDao);
        this.customerService = new CustomerService(conn, customerDao, logDao, memberDao);
        this.pcMemberService = new PC_MemberService(memberDao);
        this.foodService = new FoodService(foodDao);
        this.stockService = new StockService(stockDao);
        this.orderService = new OrderService(conn, orderDao, stockService, foodDao, eventScheduleDao);
        this.reviewService = new ReviewService(reviewDao);
    }

    private void ensureOpenConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                System.out.println("[UserController] 연결이 닫혀 있어 재생성합니다: " + memberType);
                try {
                    conn = DatabaseConnector.getConnection(memberType);
                } catch (SQLException roleEx) {
                    System.out.println("[UserController] 역할별 연결 실패: " + roleEx.getMessage());
                    System.out.println("[UserController] 루트 연결로 재시도합니다.");
                    conn = DatabaseConnector.getRootConnection();
                }
            }
            openServices();
        } catch (SQLException e) {
            throw new RuntimeException("DB 연결을 열 수 없습니다.", e);
        }
    }

    public void start() {
        showBranchSelection();
    }

    private void showBranchSelection() {
        JFrame frame = new JFrame("지점 선택");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);

        UserBranchSelectView branchView = new UserBranchSelectView();
        try {
            ensureOpenConnection();
            List<PcCafe> cafes = pcCafeService.getAllPcCafes();
            System.out.println("[UserController] 로드된 지점 수: " + (cafes == null ? 0 : cafes.size()));
            branchView.setBranches(cafes);
            branchView.setBranchButtonListener(e -> {
                String selectedPcId = e.getActionCommand();
                frame.dispose();
                showSeatSelection(selectedPcId);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "지점 정보를 로드할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            branchView.setBranches(null);
        }

        frame.add(branchView);
        frame.pack();
        frame.setSize(700, 500);
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private void showSeatSelection(String pcCafeId) {
        try {
            ensureOpenConnection();
            PcCafe cafe = pcCafeService.getPcCafe(pcCafeId);
            if (cafe == null) {
                JOptionPane.showMessageDialog(null, "선택한 지점을 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                showBranchSelection();
                return;
            }

            JFrame frame = new JFrame(cafe.getPcName() + " 좌석 선택");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(820, 700);
            frame.setLocationRelativeTo(null);

            UserSeatSelectView seatView = new UserSeatSelectView();
            seatView.setBranchName(cafe.getPcName());

            Set<Integer> occupiedSeats = new HashSet<>();
            List<Customer> activeCustomers = customerService.getCustomersInPcCafe(pcCafeId);
            for (Customer customer : activeCustomers) {
                occupiedSeats.add(customer.getSeatNum());
            }

            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 6; col++) {
                    int seatNum = row * 6 + col + 1;
                    boolean available = !occupiedSeats.contains(seatNum);
                    seatView.setSeatStatus(row, col, available);
                    final int r = row;
                    final int c = col;
                    seatView.setSeatButtonListener(row, col, e -> {
                        if (!available) {
                            return;
                        }
                        seatView.setSelectedSeat(r, c);
                    });
                }
            }

            seatView.setConfirmButtonListener(e -> {
                int row = seatView.getSelectedSeatRow();
                int col = seatView.getSelectedSeatCol();
                if (row < 0 || col < 0) {
                    JOptionPane.showMessageDialog(frame, "좌석을 먼저 선택해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int seatNum = row * 6 + col + 1;
                Customer newCustomer = new Customer();
                newCustomer.setPcCafeId(pcCafeId);
                newCustomer.setSeatNum(seatNum);
                
                String memberId = member.getMemberId();
                try {
                    PC_Member existingMember = pcMemberService.getMember(member);
                    if (existingMember == null) {
                        System.out.println("[UserController] 회원 '" + memberId + "'이 DB에 없어서 비회원으로 처리합니다.");
                        memberId = null;
                    }
                } catch (Exception ex) {
                    System.out.println("[UserController] 회원 확인 중 오류 발생, 비회원으로 처리: " + ex.getMessage());
                    memberId = null;
                }
                
                newCustomer.setMemberId(memberId);
                int remainTime = member.getRemainTime();
                if (remainTime <= 0) {
                    remainTime = 60;
                    System.out.println("[UserController] remainTime이 0 이하여서 기본값 60분으로 설정");
                }
                newCustomer.setRemainTime(remainTime);

                boolean success = customerService.checkIn(newCustomer);
                if (!success) {
                    System.out.println("[UserController] checkIn 실패 상세 정보:");
                    System.out.println("  - 지점: " + pcCafeId);
                    System.out.println("  - 좌석: " + seatNum);
                    System.out.println("  - 회원ID: " + member.getMemberId());
                    System.out.println("  - 남은시간: " + member.getRemainTime());
                    JOptionPane.showMessageDialog(frame, "선택한 좌석을 점유할 수 없습니다. 다시 시도해주세요. (콘솔 확인)", "알림", JOptionPane.WARNING_MESSAGE);
                    showSeatSelection(pcCafeId);
                    frame.dispose();
                    return;
                }

                frame.dispose();
                showDashboard(cafe.getPcName(), seatNum, newCustomer);
            });

            seatView.setBackButtonListener(e -> {
                frame.dispose();
                showBranchSelection();
            });

            frame.add(seatView);
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "좌석 정보를 로드할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            showBranchSelection();
        }
    }

    private void showDashboard(String branchName, int seatNumber, Customer customer) {
        UserMainDashboardView dashboard = new UserMainDashboardView();
        String grade = (member != null) ? member.getGradeType() : null;
        dashboard.setSessionInfo(branchName, seatNumber, grade);
        
        // 💡 1. 정지되어 있던 시간 표시 대신, 1분마다 작동하는 타이머 시작!
        dashboard.startTimer(customer.getRemainTime()); 

        dashboard.setFoodOrderButtonListener(e -> showFoodOrderView(dashboard, customer.getPcCafeId(), seatNumber));
        dashboard.setReviewButtonListener(e -> showReviewView(dashboard, customer.getPcCafeId(), member));
        dashboard.setTimeChargeButtonListener(e -> showTimeChargeView(dashboard, customer, member));
        
     // 사용한 시간만큼 DB에서 빼고 처음 로그인 화면으로 이동
        dashboard.setLogoutButtonListener(e -> {
            dashboard.stopTimer(); // 타이머 정지
            int usedTime = dashboard.getUsedMinutes();
            
            // 회원일 경우 DB(pc_member)의 잔여 시간에서 '사용한 시간' 차감
            if (member != null && member.getMemberId() != null) {
                pcMemberService.deductUsedTime(member.getMemberId(), usedTime); 
            }
            
            // 비회원/회원 공통 현재 점유 중인 PC방 좌석 세션(customer) 삭제
            customerService.checkOut(customer);
            
            dashboard.dispose();
            
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("[UserController] 로그아웃 - 유저 DB 연결 종료");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            new LoginController().start(); 
        });

        dashboard.setVisible(true);
    }

    private void showFoodOrderView(JFrame parent, String pcCafeId, int seatNumber) {
        try {
            ensureOpenConnection();
            UserFoodOrderView foodOrderView = new UserFoodOrderView(parent);
            List<Food> foods = foodService.getMenuBoard();
            Map<String, Integer> stockMap = new HashMap<>();
            stockService.getCafeStockList(pcCafeId).forEach(stock -> stockMap.put(stock.getFoodName(), stock.getStockQuantity()));
            double paymentRate = eventScheduleDao.findCurrentOrderPaymentRate(pcCafeId);

            foodOrderView.setMenuData(foods, stockMap, paymentRate);
            foodOrderView.setFoodTableSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) {
                        // 총 가격 업데이트
                        int price = foodOrderView.getSelectedFoodPrice();
                        int qty = foodOrderView.getSelectedQuantity();
                        foodOrderView.setTotalPrice(price * qty);

                        // 추천 메뉴 DB 연동 및 업데이트
                        String selectedFood = foodOrderView.getSelectedFoodName();
                        if (selectedFood != null) {
                            try {
                                // UserController에 OrderDAO가 멤버 변수로 없으므로 직접 생성해서 사용
                                OrderDAO orderDao = new OrderDAOImpl(conn);
                                List<Food> recommendedFoods = orderDao.getRecommendedFoods(selectedFood);

                                if (recommendedFoods != null && !recommendedFoods.isEmpty()) {
                                    StringBuilder sb = new StringBuilder("[" + selectedFood + "] 추천 조합: ");
                                    for (int i = 0; i < recommendedFoods.size(); i++) {
                                        sb.append(i + 1).append("위 ").append(recommendedFoods.get(i).getFoodName());
                                        if (i < recommendedFoods.size() - 1) sb.append(", ");
                                    }
                                    foodOrderView.setRecommendMessage(sb.toString());
                                } else {
                                    foodOrderView.setRecommendMessage("아직 [" + selectedFood + "]와(과) 함께 많이 주문된 메뉴가 없습니다.");
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            });

            // 2. 수량(Spinner) 변경 이벤트 리스너 (람다식 미사용)
            foodOrderView.setQuantityChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int price = foodOrderView.getSelectedFoodPrice();
                    int qty = foodOrderView.getSelectedQuantity();
                    foodOrderView.setTotalPrice(price * qty);
                }
            });

            foodOrderView.setOrderButtonListener(e -> {
                String selectedFood = foodOrderView.getSelectedFoodName();
                if (selectedFood == null) {
                    foodOrderView.setStatusMessage("음식을 먼저 선택해 주세요.");
                    return;
                }
                int quantity = foodOrderView.getSelectedQuantity();
                if (quantity <= 0) {
                    foodOrderView.setStatusMessage("수량을 올바르게 입력해 주세요.");
                    return;
                }

                try {
                    boolean success = orderService.placeOrder(pcCafeId, seatNumber, selectedFood, quantity);
                    if (success) {
                        foodOrderView.setStatusMessage("주문이 완료되었습니다.");
                        stockMap.clear();
                        stockService.getCafeStockList(pcCafeId).forEach(stock -> stockMap.put(stock.getFoodName(), stock.getStockQuantity()));
                        foodOrderView.setMenuData(foods, stockMap, paymentRate);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    foodOrderView.setStatusMessage("주문에 실패했습니다. 재고 또는 DB를 확인하세요.");
                }
            });

            foodOrderView.setCancelButtonListener(e -> foodOrderView.dispose());
            foodOrderView.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "음식 주문 창을 열 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReviewView(JFrame parent, String pcCafeId, PC_Member member) {
        try {
            ensureOpenConnection();
            UserReviewManageView reviewView = new UserReviewManageView();
            PcCafe pcCafe = pcCafeService.getPcCafe(pcCafeId);
            if (pcCafe != null) {
                reviewView.setBranchName(pcCafe.getPcName());
            }
            reviewView.refreshReviews(reviewService.checkReviewByPcCafeId(pcCafeId));

            reviewView.setSubmitReviewButtonListener(e -> {
                String memberId = member == null ? null : member.getMemberId();
                if (memberId == null || memberId.isEmpty()) {
                    reviewView.setStatusMessage("리뷰 작성은 회원만 가능합니다.");
                    return;
                }

                String content = reviewView.getReviewText().trim();
                if (content.isEmpty()) {
                    reviewView.setStatusMessage("리뷰 내용을 입력해 주세요.");
                    return;
                }

                Review review = new Review();
                review.setMemberId(memberId);
                review.setPcCafeId(pcCafeId);
                review.setStarRating(reviewView.getSelectedRating());
                String title = content.length() > 10 ? content.substring(0, 10) : content;
                review.setReviewTitle(title);
                review.setReviewContent(content);

                try {
                    reviewService.writeReview(review);
                    reviewView.setStatusMessage("리뷰가 등록되었습니다.");
                    reviewView.clearReviewInput();
                    reviewView.refreshReviews(reviewService.checkReviewByPcCafeId(pcCafeId));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    reviewView.setStatusMessage("리뷰 등록에 실패했습니다. 다시 시도해 주세요.");
                }
            });

            reviewView.setClearButtonListener(e -> reviewView.clearReviewInput());

            JFrame reviewFrame = new JFrame("리뷰 관리");
            reviewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            reviewFrame.setSize(800, 600);
            reviewFrame.setLocationRelativeTo(parent);
            reviewFrame.add(reviewView);
            reviewFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "리뷰 창을 열 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showTimeChargeView(JFrame parent, Customer customer, PC_Member member) {
        try {
            ensureOpenConnection();
            UserTimeChargeView chargeView = new UserTimeChargeView();
            
            // 모달 팝업으로 띄우기
            JDialog dialog = new JDialog(parent, "시간 충전", true);
            dialog.setContentPane(chargeView);
            dialog.setSize(450, 450);
            dialog.setLocationRelativeTo(parent);

            // 1. 회원 정보 세팅 (비회원은 null 처리 방어)
            String userName = (member != null && member.getMemberName() != null) ? member.getMemberName() : "비회원";
            String grade = (member != null && member.getGradeType() != null) ? member.getGradeType() : "NONE";
            
            int tempDiscount = 0;
            if ("SILVER".equalsIgnoreCase(grade)) tempDiscount = 5;
            else if ("GOLD".equalsIgnoreCase(grade)) tempDiscount = 10;
            
            final int discountRate = tempDiscount;
            
            chargeView.setUserInfo(userName, grade.toUpperCase(), discountRate);

            // 2. 결제 버튼 클릭 시 동작
            chargeView.setPaymentButtonListener(e -> {
                String selectedOption = chargeView.getSelectedTimeOption();
                int addMinutes = 0;
                int finalPrice = 0;

                // 콤보박스에서 선택된 시간에 따라 분(minute)과 가격 파싱
                if (selectedOption != null) {
                    if (selectedOption.contains("1시간")) { addMinutes = 60; finalPrice = 2000; }
                    else if (selectedOption.contains("3시간")) { addMinutes = 180; finalPrice = 5500; }
                    else if (selectedOption.contains("5시간")) { addMinutes = 300; finalPrice = 9000; }
                    else if (selectedOption.contains("10시간")) { addMinutes = 600; finalPrice = 17000; }
                }

                // 할인율 반영
                int discountAmount = finalPrice * discountRate / 100;
                finalPrice = finalPrice - discountAmount;

                try {
                	// Charge 객체를 만들어 결제 내역 세팅
                    Charge chargeLog = new Charge();
                    chargeLog.setPcCafeId(customer.getPcCafeId());
                    chargeLog.setSeatNum(customer.getSeatNum());
                    
                    // 비회원이면 memberId에 null이 들어갑니다.
                    chargeLog.setMemberId((member != null && member.getMemberId() != null) ? member.getMemberId() : null);
                    chargeLog.setTicketTime(addMinutes);
                    chargeLog.setChargePayAmount(finalPrice);
                	
                	chargeService.recordCharge(chargeLog);
                	
                    // [핵심 1] 회원일 경우 -> pc_member 테이블 업데이트 (영구 저장용)
                    if (member != null && member.getMemberId() != null) {
                        pcMemberService.chargeTime(member.getMemberId(), addMinutes, finalPrice);
                        // 메모리 상의 회원 정보 잔여시간도 즉시 갱신
                        member.setRemainTime(member.getRemainTime() + addMinutes);
                    }

                    // [핵심 2] 비회원/회원 공통 -> 현재 세션(customer) 테이블 및 메모리 업데이트 (일회용 저장)
                    int newRemain = customer.getRemainTime() + addMinutes;
                    customer.setRemainTime(newRemain); // 메모리 갱신
                    
                    // 만들어두신 CustomerDAOImpl을 이용해 DB의 customer 테이블에도 시간 즉시 추가
                    CustomerDAO customerDao = new CustomerDAOImpl(conn);
                    customerDao.updateRemainingTime(customer.getPcCafeId(), customer.getSeatNum(), newRemain);

                    // [핵심 3] 대시보드(View) 텍스트 실시간 갱신 (늘어난 시간으로)
                    ((UserMainDashboardView) parent).addTime(addMinutes);

                    JOptionPane.showMessageDialog(dialog, addMinutes + "분 충전이 완료되었습니다!\n결제 금액: " + finalPrice + "원");
                    dialog.dispose();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    chargeView.setStatusMessage("결제 처리 중 오류가 발생했습니다.");
                }
            });

            // 취소 버튼
            chargeView.setCancelButtonListener(e -> dialog.dispose());

            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "충전 창을 열 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

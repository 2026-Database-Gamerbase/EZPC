package controller;

import dao.ChargeDAO;
import dao.CustomerDAO;
import dao.EventScheduleDAO;
import dao.FoodDAO;
import dao.GradeDAO;
import dao.LogDAO;
import dao.OrderDAO;
import dao.PC_MemberDAO;
import dao.PcCafeDAO;
import dao.ReviewDAO;
import dao.StockDAO;
import dao.TicketDAO;
import daoImpl.ChargeDAOImpl;
import daoImpl.CustomerDAOImpl;
import daoImpl.EventScheduleDAOImpl;
import daoImpl.FoodDAOImpl;
import daoImpl.GradeDAOImpl;
import daoImpl.LogDAOImpl;
import daoImpl.OrderDAOImpl;
import daoImpl.PC_MemberDAOImpl;
import daoImpl.PcCafeDAOImpl;
import daoImpl.ReviewDAOImpl;
import daoImpl.StockDAOImpl;
import daoImpl.TicketDAOImpl;
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
import model.Charge;
import model.Grade;
import model.Ticket;
import service.ChargeService;
import service.GradeService;
import service.TicketService;
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
    private GradeService gradeService;
    private TicketService ticketService;

    public UserController(Connection conn, PC_Member member) {
        this.conn = conn;
        this.member = member;
        //비회원일 경우 자동으로 user 처리
        this.memberType = (member != null) ? member.getMemberType() : "user";
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

        GradeDAO gradeDao = new GradeDAOImpl(conn);
        this.gradeService = new GradeService(gradeDao);

        TicketDAO ticketDao = new TicketDAOImpl(conn);
        this.ticketService = new TicketService(ticketDao);

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
            
            int totalSeats = cafe.getTotalSeats();
            seatView.setupSeats(totalSeats);
            
            Set<Integer> occupiedSeats = new HashSet<>();
            List<Customer> activeCustomers = customerService.getCustomersInPcCafe(pcCafeId);
            for (Customer customer : activeCustomers) {
                occupiedSeats.add(customer.getSeatNum());
            }

            int viewRows = seatView.getRows();
            int viewCols = seatView.getCols();

            for (int row = 0; row < viewRows; row++) {
                for (int col = 0; col < viewCols; col++) {
                    int seatNum = row * viewCols + col + 1; // 🚀 상수를 변수로 변경
                    
                    if (seatNum > totalSeats) continue; // 🚀 실제 좌석 범위를 초과하는 빈 공간은 이벤트 생략

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

                int seatNum = row * viewCols + col + 1;
                Customer newCustomer = new Customer();
                newCustomer.setPcCafeId(pcCafeId);
                newCustomer.setSeatNum(seatNum);
                
                String memberId = null;
                if (member != null) {
                    try {
                        PC_Member existingMember = pcMemberService.getMember(member);
                        if (existingMember == null) { //비회원인 경우
                            System.out.println("[UserController] 회원 '" + member.getMemberId() + "'이 DB에 없어서 비회원으로 처리합니다.");
                        } else { //회원인 경우
                            memberId = member.getMemberId();
                        }
                    } catch (Exception ex) {
                        System.out.println("[UserController] 회원 확인 중 오류 발생, 비회원으로 처리: " + ex.getMessage());
                    }
                }

                newCustomer.setMemberId(memberId);
                int remainTime = (member != null) ? member.getRemainTime() : 0;
                if (remainTime <= 0) {
                    remainTime = 0;
                }
                newCustomer.setRemainTime(remainTime);

                boolean success = customerService.checkIn(newCustomer);
                if (!success) {
                    JOptionPane.showMessageDialog(frame, "선택한 좌석을 점유할 수 없습니다. 다시 시도해주세요.", "알림", JOptionPane.WARNING_MESSAGE);
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

            seatView.setRefreshButtonListener(e -> {
                try {
                    // 1. 혹시 사용자가 누르고 있던 좌석 선택 해제 (꼬임 방지)
                    seatView.setSelectedSeat(-1, -1);

                    // 2. DB에서 현재 지점의 사용 중인(체크인 된) 좌석 목록 다시 가져오기
                    Set<Integer> newOccupiedSeats = new HashSet<>();
                    List<Customer> newActiveCustomers = customerService.getCustomersInPcCafe(pcCafeId);
                    for (Customer c : newActiveCustomers) {
                        newOccupiedSeats.add(c.getSeatNum());
                    }

                    // 3. UI의 5x6 좌석 전체를 반복하면서 상태 최신화
                    for (int row = 0; row < viewRows; row++) {
                        for (int col = 0; col < viewCols; col++) {
                            int seatNum = row * viewCols + col + 1;
                            if (seatNum > totalSeats) continue; // 🚀 실제 좌석 범위 넘어가면 패스
                            
                            boolean available = !newOccupiedSeats.contains(seatNum); 
                            seatView.setSeatStatus(row, col, available);
                        }
                    }
                    
                    // 새로고침 완료 알림 (선택사항: 너무 번거로우면 주석 처리하셔도 됩니다)
                    JOptionPane.showMessageDialog(frame, "좌석 현황이 최신화되었습니다.", "새로고침", JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "좌석 정보를 갱신하는데 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
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
        String name = (member != null) ? member.getMemberName() : null;
        dashboard.setSessionInfo(branchName, seatNumber, grade, name);
        
        // 💡 1. 정지되어 있던 시간 표시 대신, 1분마다 작동하는 타이머 시작!
        dashboard.startTimer(customer.getRemainTime()); 

        dashboard.setFoodOrderButtonListener(e -> showFoodOrderView(dashboard, customer.getPcCafeId(), seatNumber));
        dashboard.setReviewButtonListener(e -> {
            if (member == null) { //비회원이 리뷰 작성 버튼을 누를 경우
                JOptionPane.showMessageDialog(dashboard, "리뷰 작성은 회원만 가능합니다.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showReviewView(dashboard, customer.getPcCafeId(), member);
        });
        dashboard.setTimeChargeButtonListener(e -> showTimeChargeView(dashboard, customer, member));
        
        dashboard.setLogoutButtonListener(e -> {
            dashboard.stopTimer(); // 1. 타이머 정지
            int usedTime = dashboard.getUsedMinutes(); // 사용 시간 계산
            
            // 2. 회원일 경우 DB 잔여시간 차감
            if (member != null && member.getMemberId() != null) {
                pcMemberService.deductUsedTime(member.getMemberId(), usedTime); 
            }
            
            // 3. 좌석 사용 로그 종료 및 세션 삭제
            customerService.checkOut(customer);
            
            // 4. 대시보드 화면 종료
            dashboard.dispose(); 
            
            // 5. [핵심] 로그인 정보 및 세션 통신 완벽 초기화
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close(); // 자바 메모리에 물려있던 DB 커넥션을 끊어 초기화합니다.
                    System.out.println("[UserController] 로그아웃 성공 - 세션 초기화 완료");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
            // 6. 완전히 처음 로그인 화면(LoginController) 복귀
            new controller.LoginController().start(); 
        });

        dashboard.setVisible(true);

        // 로그인/좌석 선택 직후 잔여 시간이 0분 이하라면 자동으로 충전 유도
        if (customer.getRemainTime() <= 0) {
            // 안내 팝업창 출력
            JOptionPane.showMessageDialog(dashboard, 
                "잔여시간이 없습니다.\n시간 충전 페이지로 이동합니다.", 
                "시간 알림", 
                JOptionPane.WARNING_MESSAGE);
            
            showTimeChargeView(dashboard, customer, member);
        }
        // =========================================================================
    }

    private void showFoodOrderView(JFrame parent, String pcCafeId, int seatNumber) {
        try {
            ensureOpenConnection();
            UserFoodOrderView foodOrderView = new UserFoodOrderView(parent);
            List<Food> foods = foodService.getMenuBoard();
            Map<String, Integer> stockMap = new HashMap<>();
            stockService.getCafeStockList(pcCafeId).forEach(s -> stockMap.put(s.getFoodName(), s.getStockQuantity()));
            double paymentRate = eventScheduleDao.findCurrentOrderPaymentRate(pcCafeId);

            foodOrderView.setMenuData(foods, stockMap, paymentRate);

            // 메뉴 선택 시 추천 메뉴 표시
            foodOrderView.setFoodTableSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedFood = foodOrderView.getSelectedFoodName();
                    if (selectedFood != null) {
                        try {
                            OrderDAO orderDao = new OrderDAOImpl(conn);
                            List<Food> recommended = orderDao.getRecommendedFoods(selectedFood);
                            if (recommended != null && !recommended.isEmpty()) {
                                StringBuilder sb = new StringBuilder("[" + selectedFood + "] 추천 조합: ");
                                for (int i = 0; i < recommended.size(); i++) {
                                    sb.append(i + 1).append("위 ").append(recommended.get(i).getFoodName());
                                    if (i < recommended.size() - 1) sb.append(", ");
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
            });

            // 담기 버튼: 선택한 음식 + 수량을 장바구니에 추가
            foodOrderView.setAddToCartButtonListener(e -> {
                String foodName = foodOrderView.getSelectedFoodName();
                if (foodName == null) {
                    foodOrderView.setStatusMessage("담을 음식을 메뉴에서 선택해 주세요.");
                    return;
                }
                int qty   = foodOrderView.getSelectedQuantity();
                int price = foodOrderView.getSelectedFoodCurrentPrice();
                int stock = stockMap.getOrDefault(foodName, 0);
                if (qty > stock) {
                    foodOrderView.setStatusMessage("재고가 부족합니다. (재고: " + stock + "개)");
                    return;
                }
                foodOrderView.addToCart(foodName, qty, price);
                foodOrderView.setStatusMessage("[" + foodName + "] " + qty + "개가 장바구니에 담겼습니다.");
            });

            // 주문하기 버튼: 장바구니 전체를 한 번에 주문
            foodOrderView.setOrderButtonListener(e -> {
                if (foodOrderView.isCartEmpty()) {
                    foodOrderView.setStatusMessage("장바구니가 비어있습니다. 음식을 먼저 담아주세요.");
                    return;
                }
                Map<String, Integer> cart = foodOrderView.getCartQuantities();
                try {
                    boolean success = orderService.placeCartOrder(pcCafeId, seatNumber, cart);
                    if (success) {
                        foodOrderView.setStatusMessage("주문이 완료되었습니다!");
                        foodOrderView.clearCart();
                        stockMap.clear();
                        stockService.getCafeStockList(pcCafeId).forEach(s -> stockMap.put(s.getFoodName(), s.getStockQuantity()));
                        foodOrderView.refreshMenuStock(stockMap);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    foodOrderView.setStatusMessage("주문에 실패했습니다: " + ex.getMessage());
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

            // 1. ticket 테이블에서 시간권 목록 조회
            List<Ticket> tickets = ticketService.getAllTickets();
            if (tickets == null || tickets.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "등록된 시간권이 없습니다. 관리자에게 문의하세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }

            UserTimeChargeView chargeView = new UserTimeChargeView(tickets);

            JDialog dialog = new JDialog(parent, "시간 충전", true);
            dialog.setContentPane(chargeView);
            dialog.setSize(450, 520);
            dialog.setLocationRelativeTo(parent);

            // 2. 회원 정보 세팅 (비회원은 null 처리 방어)
            String userName = (member != null && member.getMemberName() != null) ? member.getMemberName() : "비회원";
            String grade = (member != null && member.getGradeType() != null) ? member.getGradeType() : "NONE";

            // 3. grade 테이블에서 등급 할인율 조회 (benefit: 0.0~1.0)
            int discountRate = 0;
            try {
                Grade gradeInfo = gradeService.getGrade(grade);
                if (gradeInfo != null) {
                    discountRate = (int) (gradeInfo.getBenefit() * 100);
                }
            } catch (Exception ex) {
                System.out.println("[UserController] 등급 할인율 조회 실패, 0% 적용: " + ex.getMessage());
            }
            final int finalDiscountRate = discountRate;

            // 4. 충전 이벤트 할인율 조회 (payment_rate: 1.0이면 이벤트 없음)
            double eventPaymentRate = 1.00;
            try {
                eventPaymentRate = eventScheduleDao.findCurrentChargePaymentRate(customer.getPcCafeId());
            } catch (Exception ex) {
                System.out.println("[UserController] 충전 이벤트 조회 실패, 이벤트 할인 없음: " + ex.getMessage());
            }
            final double finalEventPaymentRate = eventPaymentRate;

            chargeView.setUserInfo(userName, grade, finalDiscountRate, finalEventPaymentRate);

            // 4. 결제 버튼 클릭 시 동작
            chargeView.setPaymentButtonListener(e -> {
                Ticket selected = chargeView.getSelectedTicket();
                if (selected == null) return;

                int addMinutes = selected.getTicketTime();
                int basePrice = selected.getPrice();
                // 프로시저·뷰와 동일한 공식: 기본가 × 이벤트비율 × (1 - 등급할인율)
                int finalPrice = (int) Math.round(basePrice * finalEventPaymentRate * (1.0 - finalDiscountRate / 100.0));

                try {
                    // Charge 객체를 만들어 결제 내역 세팅
                    Charge chargeLog = new Charge();
                    chargeLog.setPcCafeId(customer.getPcCafeId());
                    chargeLog.setSeatNum(customer.getSeatNum());
                    chargeLog.setMemberId((member != null && member.getMemberId() != null) ? member.getMemberId() : null);
                    chargeLog.setTicketTime(addMinutes);
                    chargeLog.setChargePayAmount(finalPrice);

                    chargeService.recordCharge(chargeLog);

                    // [핵심 1] 회원일 경우 -> pc_member 테이블 업데이트 (영구 저장용)
                    if (member != null && member.getMemberId() != null) {
                        pcMemberService.chargeTime(member.getMemberId(), addMinutes, finalPrice);
                        member.setRemainTime(member.getRemainTime() + addMinutes);
                    }

                    // [핵심 2] customer 메모리 업데이트
                    // (DB customer.remain_time은 charge_by_customer 프로시저가 이미 증가시킴)
                    customer.setRemainTime(customer.getRemainTime() + addMinutes);

                    // [핵심 3] 대시보드 실시간 갱신
                    if (parent instanceof UserMainDashboardView) {
                        ((UserMainDashboardView) parent).addTime(addMinutes);
                    }

                    JOptionPane.showMessageDialog(dialog, addMinutes + "분 충전이 완료되었습니다!\n결제 금액: " + finalPrice + "원");
                    dialog.dispose();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    chargeView.setStatusMessage("결제 처리 중 오류가 발생했습니다.");
                }
            });

            chargeView.setCancelButtonListener(e -> dialog.dispose());
            dialog.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "충전 창을 열 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

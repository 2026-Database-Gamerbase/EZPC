package controller;

import dao.CustomerDAO;
import dao.FoodDAO;
import dao.LogDAO;
import dao.OrderDAO;
import dao.PC_MemberDAO;
import dao.PcCafeDAO;
import dao.ReviewDAO;
import daoImpl.CustomerDAOImpl;
import daoImpl.FoodDAOImpl;
import daoImpl.LogDAOImpl;
import daoImpl.OrderDAOImpl;
import daoImpl.PC_MemberDAOImpl;
import daoImpl.PcCafeDAOImpl;
import daoImpl.ReviewDAOImpl;
import db.DatabaseConnector;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
import view.user.UserBranchSelectView;
import view.user.UserFoodOrderView;
import view.user.UserMainDashboardView;
import view.user.UserReviewManageView;
import view.user.UserSeatSelectView;

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
        this.pcCafeService = new PcCafeService(pcCafeDao);
        this.customerService = new CustomerService(conn, customerDao, logDao, memberDao);
        this.pcMemberService = new PC_MemberService(memberDao);
        this.foodService = new FoodService(foodDao);
        this.orderService = new OrderService(conn, orderDao, null, foodDao, null);
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
                    conn = DatabaseConnector.getConnection();
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
        dashboard.setSessionInfo(branchName, seatNumber);
        int remainTime = customer.getRemainTime();
        int hours = remainTime / 60;
        int minutes = remainTime % 60;
        dashboard.updateRemainingTime(hours, minutes, 0);

        dashboard.setFoodOrderButtonListener(e -> showFoodOrderView(dashboard, customer.getPcCafeId()));
        dashboard.setReviewButtonListener(e -> showReviewView(dashboard, customer.getPcCafeId()));
        dashboard.setLogoutButtonListener(e -> {
            customerService.checkOut(customer);
            dashboard.dispose();
            showBranchSelection();
        });

        dashboard.setVisible(true);
    }

    private void showFoodOrderView(JFrame parent, String pcCafeId) {
        try {
            ensureOpenConnection();
            UserFoodOrderView foodOrderView = new UserFoodOrderView(parent);
            List<Food> foods = foodService.getMenuBoard();
            
            foodOrderView.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "음식 주문 창을 열 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReviewView(JFrame parent, String pcCafeId) {
        try {
            ensureOpenConnection();
            UserReviewManageView reviewView = new UserReviewManageView();
            List<Review> reviews = reviewService.checkReviewByPcCafeId(pcCafeId);
            
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
}

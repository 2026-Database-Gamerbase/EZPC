// 모든 View 연결 테스트용
package view;

import javax.swing.*;
import view.auth.LoginView;
import view.auth.SignUpView;
import view.owner.OwnerEmployeeManageView;
import view.owner.OwnerFoodStockView;
import view.owner.OwnerMainFrameView;
import view.owner.OwnerMemberManageView;
import view.owner.OwnerSalesStatsView;
import view.owner.OwnerSeatMonitorView;
import view.user.UserBranchSelectView;
import view.user.UserFoodOrderView;
import view.user.UserMainDashboardView;
import view.user.UserReviewManageView;
import view.user.UserSeatSelectView;
import view.user.UserTimeChargeView;

public class ViewIntegrationTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            testLoginView();
        });
    }
    
    // LoginView 테스트
    private static void testLoginView() {
        LoginView loginView = new LoginView();
        
        loginView.setLoginButtonListener(e -> {
            String id = loginView.getInputId();
            String password = loginView.getInputPassword();

            if ("owner".equals(id)) {
                loginView.dispose();
                testOwnerMainFrame();
            } else if (!id.isEmpty()) {
                loginView.dispose();
                testUserBranchSelect();
            } else {
                loginView.setStatusMessage("ID를 입력하세요");
            }
        });
        
        loginView.setSignUpButtonListener(e -> {
            testSignUpView();
        });
        
        loginView.setVisible(true);
    }
    
    // SignUpView 테스트
    private static void testSignUpView() {
        JFrame tempFrame = new JFrame("회원가입 테스트");
        tempFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        tempFrame.setSize(450, 500);
        
        SignUpView signUpView = new SignUpView();
        signUpView.setSignUpButtonListener(e -> {
            String id = signUpView.getInputId();
            String name = signUpView.getInputName();
            String password = signUpView.getInputPassword();
            String confirmPassword = signUpView.getConfirmPassword();
            String phone = signUpView.getInputPhoneNumber();
        });
    }
    
    // User View 테스트
    private static void testUserBranchSelect() {
        JFrame frame = new JFrame("사용자 - 지점 선택");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        
        UserBranchSelectView branchView = new UserBranchSelectView();
        branchView.setAllBranchButtonListener(e -> {
            frame.dispose();
            testUserSeatSelect();
        });
        
        frame.add(branchView);
        frame.setVisible(true);
    }
    
    // UserSeatSelectView 테스트
    private static void testUserSeatSelect() {
        JFrame frame = new JFrame("사용자 - 좌석 선택");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);
        
        UserSeatSelectView seatView = new UserSeatSelectView();
        seatView.setBranchName("강남점");
        
        // 좌석 상태 설정 (샘플)
        seatView.setSeatStatus(0, 0, true);
        seatView.setSeatStatus(0, 1, false);
        seatView.setSeatStatus(1, 0, true);
        
        seatView.setConfirmButtonListener(e -> {
            frame.dispose();
            testUserTimeCharge();
        });
        
        frame.add(seatView);
        frame.setVisible(true);
    }
    
    // UserTimeChargeView 테스트
    private static void testUserTimeCharge() {
        JFrame frame = new JFrame("사용자 - 시간권 구매");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        
        UserTimeChargeView chargeView = new UserTimeChargeView();
        chargeView.setUserInfo("user001", "Silver", 5);
        chargeView.setPriceInfo(2000, 100, 1900);
        
        chargeView.setPaymentButtonListener(e -> {
            frame.dispose();
            testUserMainDashboard();
        });
        
        frame.add(chargeView);
        frame.setVisible(true);
    }
    
    // UserMainDashboardView 테스트
    private static void testUserMainDashboard() {
        UserMainDashboardView dashboardView = new UserMainDashboardView();
        dashboardView.setSessionInfo("강남점", 1);
        dashboardView.updateRemainingTime(1, 0, 0);
        
        dashboardView.setFoodOrderButtonListener(e -> {
            testUserFoodOrder(dashboardView);
        });
        
        dashboardView.setReviewButtonListener(e -> {
            testUserReviewManage();
        });
        
        dashboardView.setLogoutButtonListener(e -> {
            dashboardView.dispose();
        });
        
        dashboardView.setVisible(true);
    }
    
    // UserFoodOrderView 테스트
    private static void testUserFoodOrder(JFrame parent) {
        UserFoodOrderView foodOrderView = new UserFoodOrderView((JFrame) parent);
        foodOrderView.setEventDiscount("라면 할인 이벤트", 10);
        foodOrderView.setTotalPrice(4400);
        
        foodOrderView.setOrderButtonListener(e -> {
            String foodName = foodOrderView.getSelectedFoodName();
            int quantity = foodOrderView.getSelectedQuantity();
        });
        
        foodOrderView.setVisible(true);
    }
    
    // UserReviewManageView 테스트
    private static void testUserReviewManage() {
        JFrame frame = new JFrame("사용자 - 리뷰 관리");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        
        UserReviewManageView reviewView = new UserReviewManageView();
        reviewView.setBranchName("강남점");
        
        reviewView.setSubmitReviewButtonListener(e -> {
            int rating = reviewView.getSelectedRating();
            String reviewText = reviewView.getReviewText();
            reviewView.refreshReviewList();
        });
        
        frame.add(reviewView);
        frame.setVisible(true);
    }
    
    // 운영자 뷰 테스트
    private static void testOwnerMainFrame() {
        OwnerMainFrameView ownerFrame = new OwnerMainFrameView();
        
        OwnerSeatMonitorView seatMonitor = ownerFrame.getSeatMonitorView();
        seatMonitor.updateSeatStats(30, 12, 18);
        
        OwnerSalesStatsView salesStats = ownerFrame.getSalesStatsView();
        salesStats.updateStats(1234560, 456, 2700);
        
        OwnerFoodStockView foodStock = ownerFrame.getFoodStockView();
        OwnerEmployeeManageView employeeManage = ownerFrame.getEmployeeManageView();
        OwnerMemberManageView memberManage = ownerFrame.getMemberManageView();
        
        ownerFrame.setVisible(true);
    }
}

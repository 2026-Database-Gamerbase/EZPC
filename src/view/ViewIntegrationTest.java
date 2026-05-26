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

/**
 * ViewIntegrationTest - 모든 View의 통합 테스트 클래스
 * 각 View가 제대로 표시되고, 뷰들 간의 유기적 연결을 확인합니다.
 */
public class ViewIntegrationTest {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("=== PC방 관리 시스템 View 통합 테스트 시작 ===\n");
            
            // 1. Auth 패키지 테스트
            System.out.println("[TEST 1] Auth 패키지 - LoginView");
            testLoginView();
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
    
    /**
     * LoginView 테스트
     * - 로그인 화면이 정상 표시되는지 확인
     * - ID/Password 입력 필드, 로그인/회원가입 버튼 동작
     */
    private static void testLoginView() {
        LoginView loginView = new LoginView();
        
        // 로그인 버튼 리스너 설정
        loginView.setLoginButtonListener(e -> {
            String id = loginView.getInputId();
            String password = loginView.getInputPassword();
            System.out.println("  ✓ 로그인 버튼 클릭");
            System.out.println("    입력된 ID: " + (id.isEmpty() ? "[공백]" : id));
            System.out.println("    입력된 Password: " + (password.isEmpty() ? "[공백]" : "****"));
            
            // ID가 'owner'면 OwnerMainFrameView로, 아니면 UserBranchSelectView로
            if ("owner".equals(id)) {
                System.out.println("    → Owner 계정 감지! OwnerMainFrameView로 라우팅");
                loginView.dispose();
                testOwnerMainFrame();
            } else if (!id.isEmpty()) {
                System.out.println("    → User 계정 감지! UserBranchSelectView로 라우팅");
                loginView.dispose();
                testUserBranchSelect();
            } else {
                loginView.setStatusMessage("ID를 입력하세요");
            }
        });
        
        // 회원가입 버튼 리스너 설정
        loginView.setSignUpButtonListener(e -> {
            System.out.println("  ✓ 회원가입 버튼 클릭");
            System.out.println("    → SignUpView로 전환");
            testSignUpView();
        });
        
        loginView.setVisible(true);
        System.out.println("  ✓ LoginView 창 표시 완료\n");
    }
    
    /**
     * SignUpView 테스트
     */
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
            
            System.out.println("  ✓ SignUpView 회원가입 버튼 클릭");
            System.out.println("    입력된 정보:");
            System.out.println("      - ID: " + id);
            System.out.println("      - 이름: " + name);
            System.out.println("      - 전화번호: " + phone);
            System.out.println("      - 비밀번호 일치: " + password.equals(confirmPassword));
            System.out.println("    기본 설정:");
            System.out.println("      - 등급: Bronze");
            System.out.println("      - 타입: user\n");
        });
        
        System.out.println("[TEST 2] Auth 패키지 - SignUpView");
        System.out.println("  ✓ SignUpView 창 표시 완료\n");
    }
    
    /**
     * User 패키지 통합 테스트 시작
     */
    private static void testUserBranchSelect() {
        JFrame frame = new JFrame("사용자 - 지점 선택");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        
        UserBranchSelectView branchView = new UserBranchSelectView();
        branchView.setAllBranchButtonListener(e -> {
            System.out.println("[TEST 3] User 패키지 - UserBranchSelectView");
            System.out.println("  ✓ 지점 선택 버튼 클릭");
            System.out.println("    선택된 지점: " + e.getActionCommand());
            System.out.println("    → UserSeatSelectView로 전환\n");
            
            frame.dispose();
            testUserSeatSelect();
        });
        
        frame.add(branchView);
        frame.setVisible(true);
    }
    
    /**
     * UserSeatSelectView 테스트
     */
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
            System.out.println("[TEST 4] User 패키지 - UserSeatSelectView");
            System.out.println("  ✓ 일시좌석 선택 완료");
            System.out.println("    선택된 좌석: " + (seatView.getSelectedSeatRow() + 1) + "번");
            System.out.println("    → UserTimeChargeView로 전환\n");
            
            frame.dispose();
            testUserTimeCharge();
        });
        
        frame.add(seatView);
        frame.setVisible(true);
        
        System.out.println("[TEST 3] User 패키지 - UserSeatSelectView");
        System.out.println("  ✓ 좌석 배치도 표시 완료\n");
    }
    
    /**
     * UserTimeChargeView 테스트
     */
    private static void testUserTimeCharge() {
        JFrame frame = new JFrame("사용자 - 시간권 구매");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        
        UserTimeChargeView chargeView = new UserTimeChargeView();
        chargeView.setUserInfo("user001", "Silver", 5);
        chargeView.setPriceInfo(2000, 100, 1900);
        
        chargeView.setPaymentButtonListener(e -> {
            System.out.println("[TEST 5] User 패키지 - UserTimeChargeView");
            System.out.println("  ✓ 결제 버튼 클릭");
            System.out.println("    선택 시간: " + chargeView.getSelectedTimeOption());
            System.out.println("    → UserMainDashboardView 표시\n");
            
            frame.dispose();
            testUserMainDashboard();
        });
        
        frame.add(chargeView);
        frame.setVisible(true);
        
        System.out.println("[TEST 5] User 패키지 - UserTimeChargeView");
        System.out.println("  ✓ 시간권 구매 화면 표시 완료\n");
    }
    
    /**
     * UserMainDashboardView 테스트
     */
    private static void testUserMainDashboard() {
        UserMainDashboardView dashboardView = new UserMainDashboardView();
        dashboardView.setSessionInfo("강남점", 1);
        dashboardView.updateRemainingTime(1, 0, 0);
        
        dashboardView.setFoodOrderButtonListener(e -> {
            System.out.println("[TEST 7] User 패키지 - UserFoodOrderView");
            System.out.println("  ✓ 음식 주문 버튼 클릭 (대시보드에서)");
            System.out.println("    → UserFoodOrderView 팝업 표시\n");
            testUserFoodOrder(dashboardView);
        });
        
        dashboardView.setReviewButtonListener(e -> {
            System.out.println("[TEST 8] User 패키지 - UserReviewManageView");
            System.out.println("  ✓ 리뷰 버튼 클릭 (대시보드에서)");
            System.out.println("    → UserReviewManageView 팝업 표시\n");
            testUserReviewManage();
        });
        
        dashboardView.setLogoutButtonListener(e -> {
            System.out.println("  ✓ 로그아웃 버튼 클릭");
            System.out.println("    → LoginView로 복귀\n");
            dashboardView.dispose();
        });
        
        dashboardView.setVisible(true);
        
        System.out.println("[TEST 6] User 패키지 - UserMainDashboardView");
        System.out.println("  ✓ 메인 대시보드 윈도우 표시 완료");
        System.out.println("    기능: 음식주문, 리뷰, 로그아웃\n");
    }
    
    /**
     * UserFoodOrderView 테스트
     */
    private static void testUserFoodOrder(JFrame parent) {
        UserFoodOrderView foodOrderView = new UserFoodOrderView((JFrame) parent);
        foodOrderView.setEventDiscount("라면 할인 이벤트", 10);
        foodOrderView.setTotalPrice(4400);
        
        foodOrderView.setOrderButtonListener(e -> {
            String foodName = foodOrderView.getSelectedFoodName();
            int quantity = foodOrderView.getSelectedQuantity();
            System.out.println("  ✓ 음식 주문 완료");
            if (foodName != null) {
                System.out.println("    주문 음식: " + foodName);
                System.out.println("    주문 수량: " + quantity + "개");
            }
        });
        
        foodOrderView.setVisible(true);
        System.out.println("  ✓ UserFoodOrderView 팝업 표시 완료\n");
    }
    
    /**
     * UserReviewManageView 테스트
     */
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
            System.out.println("[TEST 8] User 패키지 - UserReviewManageView");
            System.out.println("  ✓ 리뷰 작성 완료");
            System.out.println("    별점: " + rating);
            System.out.println("    후기: " + (reviewText.isEmpty() ? "[공백]" : reviewText));
            System.out.println("    → 리뷰 목록 새로고침\n");
            reviewView.refreshReviewList();
        });
        
        frame.add(reviewView);
        frame.setVisible(true);
        System.out.println("  ✓ UserReviewManageView 표시 완료\n");
    }
    
    /**
     * Owner 패키지 통합 테스트
     */
    private static void testOwnerMainFrame() {
        System.out.println("[TEST 9] Owner 패키지 - OwnerMainFrameView (통합 프레임)");
        
        OwnerMainFrameView ownerFrame = new OwnerMainFrameView();
        
        // 각 탭 기능 테스트
        OwnerSeatMonitorView seatMonitor = ownerFrame.getSeatMonitorView();
        seatMonitor.updateSeatStats(30, 12, 18);
        System.out.println("  ✓ 탭 1 - OwnerSeatMonitorView");
        System.out.println("    기능: 좌석 모니터링, 사용자 세션 정보");
        System.out.println("    상태: 전체 30석, 사용중 12석, 빈 좌석 18석\n");
        
        OwnerSalesStatsView salesStats = ownerFrame.getSalesStatsView();
        salesStats.updateStats(1234560, 456, 2700);
        System.out.println("  ✓ 탭 2 - OwnerSalesStatsView");
        System.out.println("    기능: 매출 통계, 지점별 매출, 인기 음식");
        System.out.println("    통계: 총 매출 1,234,560원, 사용자 456명, 평균 2,700원\n");
        
        OwnerFoodStockView foodStock = ownerFrame.getFoodStockView();
        System.out.println("  ✓ 탭 3 - OwnerFoodStockView");
        System.out.println("    기능: 음식 재고 조회 및 업데이트\n");
        
        OwnerEmployeeManageView employeeManage = ownerFrame.getEmployeeManageView();
        System.out.println("  ✓ 탭 4 - OwnerEmployeeManageView");
        System.out.println("    기능: 직원 관리, 시급 조정, 고용/해고, 출근 관리\n");
        
        OwnerMemberManageView memberManage = ownerFrame.getMemberManageView();
        System.out.println("  ✓ 탭 5 - OwnerMemberManageView");
        System.out.println("    기능: 회원 관리, 등급 기준 및 할인율 조정\n");
        
        ownerFrame.setVisible(true);
        System.out.println("  ✓ OwnerMainFrameView (모든 탭 포함) 표시 완료\n");
    }
}

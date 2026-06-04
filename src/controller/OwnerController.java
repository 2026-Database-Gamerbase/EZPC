package controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;

// Model
import model.PC_Member;
import model.PcCafe;

// View
import view.owner.OwnerMainFrameView;

// Service
import service.*;

// DAO Interfaces & Implementations
import dao.*;
import daoImpl.*;


// DB 커넥션을 받아 모든 DAO, Service, 서브 컨트롤러를 초기화하고 연결해주는 역할을 합니다.
public class OwnerController {
    private final Connection ownerConn;
    private final PC_Member ownerMember;
    private final OwnerMainFrameView mainView;

    // 공통 서비스 (지점 목록 로드용)
    private PcCafeService pcCafeService;

    // 각 탭별 전담 서브 컨트롤러
    private SeatMonitorController seatMonitorController;
    private SalesStatsController salesStatsController;
    private FoodStockController foodStockController;
    private EmployeeManageController employeeManageController;
    private MemberManageController memberManageController;
    private SystemSetupController systemSetupController;

    public OwnerController(Connection ownerConn, PC_Member ownerMember) {
        this.ownerConn = ownerConn;
        this.ownerMember = ownerMember;
        this.mainView = new OwnerMainFrameView();
        
        // 내부 의존성(DAO, Service) 세팅 및 컨트롤러 생성
        initializeDependencies();
        
        // 뷰 이벤트 리스너 등록
        initEventBindings();
    }

    /**
     * 로그인 성공 후 메인 화면을 띄우는 진입점
     */
    public void start() {
        // 1. 상단 콤보박스에 전체 지점 목록 세팅
        loadPcCafeBranches();
        
        // 2. 메인 프레임 노출
        mainView.setVisible(true);
        
        // 3. 첫 화면(좌석 모니터링) 데이터 최초 1회 로드
        triggerCurrentTabRefresh();
    }

    
    // DB 커넥션을 기반으로 전체 DAO와 Service를 생성하고 서브 컨트롤러에 주입
    private void initializeDependencies() {
        try {
            // [1] DAO 세팅
            PcCafeDAO pcCafeDAO = new PcCafeDAOImpl(ownerConn);
            CustomerDAO customerDAO = new CustomerDAOImpl(ownerConn);
            LogDAO logDAO = new LogDAOImpl(ownerConn);
            OrderDAO orderDAO = new OrderDAOImpl(ownerConn);
            FoodDAO foodDAO = new FoodDAOImpl(ownerConn);
            EventScheduleDAO eventScheduleDAO = new EventScheduleDAOImpl(ownerConn);
            StockDAO stockDAO = new StockDAOImpl(ownerConn);
            EmployeeDAO employeeDAO = new EmployeeDAOImpl(ownerConn);
            PC_MemberDAO pcMemberDAO = new PC_MemberDAOImpl(ownerConn);
            GradeDAO gradeDAO = new GradeDAOImpl(ownerConn);
            TicketDAO ticketDAO = new TicketDAOImpl(ownerConn);
            EventInfoDAO eventInfoDAO = new EventInfoDAOImpl(ownerConn);
            ReviewDAO reviewDAO = new ReviewDAOImpl(ownerConn);
            SalesReportDAO salesReportDAO = new SalesReportDAOImpl(ownerConn);

            // [2] Service 세팅
            this.pcCafeService = new PcCafeService(pcCafeDAO);
            CustomerService customerService = new CustomerService(ownerConn, customerDAO, logDAO, pcMemberDAO);
            StockService stockService = new StockService(stockDAO);
            OrderService orderService = new OrderService(ownerConn, orderDAO, stockService, foodDAO, eventScheduleDAO);
            SalesReportService salesReportService = new SalesReportService(salesReportDAO);
            LogService logService = new LogService(logDAO);
            EmployeeService employeeService = new EmployeeService(employeeDAO);
            PC_MemberService pcMemberService = new PC_MemberService(pcMemberDAO);
            GradeService gradeService = new GradeService(gradeDAO);
            FoodService foodService = new FoodService(foodDAO);
            TicketService ticketService = new TicketService(ticketDAO);
            EventInfoService eventInfoService = new EventInfoService(eventInfoDAO);
            EventScheduleService eventScheduleService = new EventScheduleService(eventScheduleDAO);

            // [3] 서브 컨트롤러 세팅 (View + Service 연결)
            this.seatMonitorController = new SeatMonitorController(mainView.getSeatMonitorView(), customerService, pcCafeService);
            this.salesStatsController = new SalesStatsController(mainView.getSalesStatsView(), mainView, salesReportService, orderService, logService, pcCafeService, eventScheduleService);            
            this.foodStockController = new FoodStockController(mainView.getFoodStockView(), stockService);
            this.employeeManageController = new EmployeeManageController(mainView.getEmployeeManageView(), employeeService);
            this.memberManageController = new MemberManageController(mainView.getMemberManageView(), pcMemberService, gradeService);
            this.systemSetupController = new SystemSetupController(mainView.getSystemSetupView(), pcCafeService, foodService, ticketService, eventInfoService);

        } catch (Exception e) {
            System.err.println("[OwnerController] 의존성 초기화 실패");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "시스템 초기화 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    //상단 지점 변경 및 탭 전환 시 발생하는 이벤트들을 바인딩합니다.
    private void initEventBindings() {
        // 지점 콤보박스 변경 시 현재 탭 새로고침
        mainView.setBranchChangeListener(e -> triggerCurrentTabRefresh());

        // 다른 탭으로 넘어갈 때마다 데이터 새로고침
        mainView.addTabChangeListener(e -> triggerCurrentTabRefresh());
    }

    // DB에서 전체 지점 코드를 조회해 상단 콤보박스에 채워 넣습니다.
    private void loadPcCafeBranches() {
        try {
            List<PcCafe> cafes = pcCafeService.getAllPcCafes();
            String[] branchIds = cafes.stream()
                                      .map(PcCafe::getPcId) 
                                      .toArray(String[]::new);
            mainView.setBranchList(branchIds);
        } catch (SQLException e) {
            System.err.println("[OwnerController] 지점 목록 로드 실패");
            e.printStackTrace();
        }
    }

    // 현재 열려있는 탭과 선택된 지점을 파악하여, 알맞은 서브 컨트롤러의 데이터 갱신 메서드를 호출합니다.
    private void triggerCurrentTabRefresh() {
        String selectedBranchId = mainView.getSelectedBranch();
        if (selectedBranchId == null || selectedBranchId.trim().isEmpty()) return;

        int activeTabIndex = mainView.getSelectedTabIndex();

        switch (activeTabIndex) {
            case 0: // 좌석 모니터링
                seatMonitorController.refreshSeatMonitor(selectedBranchId);
                break;
            case 1: // 매출 통계
                salesStatsController.refreshSalesStats(selectedBranchId);
                break;
            case 2: // 음식 재고
                foodStockController.refreshStockList(selectedBranchId);
                break;
            case 3: // 직원 관리
                employeeManageController.refreshEmployeeList(selectedBranchId);
                break;
            case 4: // 회원 관리 
                memberManageController.refreshMemberAndGradeData();
                break;
            case 5: // 시스템 설정 
                systemSetupController.refreshCurrentCategory();
                break;
        }
    }
}
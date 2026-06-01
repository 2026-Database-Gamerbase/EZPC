package db;

import java.sql.Connection;
import java.sql.SQLException;

import controller.Controller;
import view.View;

import dao.PC_MemberDAO;
import dao.PcCafeDAO;
import dao.ChargeDAO; 
import dao.CustomerDAO;
import dao.EmployeeDAO;
import dao.EventInfoDAO;
import dao.EventScheduleDAO;
import dao.FoodDAO;
import dao.GradeDAO;
import dao.LogDAO;
import dao.OrderDAO;
import dao.ReviewDAO;
import dao.SalesReportDAO;
import dao.StockDAO;
import dao.TicketDAO;

import daoImpl.PC_MemberDAOImpl;
import daoImpl.PcCafeDAOImpl;
import daoImpl.ChargeDAOImpl;
import daoImpl.CustomerDAOImpl;
import daoImpl.EmployeeDAOImpl;
import daoImpl.EventInfoDAOImpl;
import daoImpl.EventScheduleDAOImpl;
import daoImpl.FoodDAOImpl;
import daoImpl.GradeDAOImpl;
import daoImpl.LogDAOImpl;
import daoImpl.OrderDAOImpl;
import daoImpl.ReviewDAOImpl;
import daoImpl.SalesReportDAOImpl;
import daoImpl.StockDAOImpl;
import daoImpl.TicketDAOImpl;

import service.ChargeService;
import service.CustomerService;
import service.EmployeeService;
import service.EventInfoService;
import service.EventScheduleService;
import service.FoodService;
import service.GradeService;
import service.LogService;
import service.OrderService;
import service.PC_MemberService;
import service.PcCafeService;
import service.ReviewService;
import service.SalesReportService;
import service.StockService;
import service.TicketService;

public class Main {

    public static void main(String[] args) {
        
        Connection conn = null;
        
        try {
            //db 연결
            conn = DatabaseConnector.getConnection();
            System.out.println("DB 연결 성공!");

            //view 생성
            View view = new View();
            
            //dao 생성
            PC_MemberDAO memberDao = new PC_MemberDAOImpl(conn);
            PcCafeDAO pcCafeDao = new PcCafeDAOImpl(); 
            ChargeDAO chargeDao = new ChargeDAOImpl(conn);
            CustomerDAO customerDao = new CustomerDAOImpl(conn);
            EmployeeDAO employeeDao = new EmployeeDAOImpl();
            EventInfoDAO eventInfoDao = new EventInfoDAOImpl(conn);
            EventScheduleDAO eventScheduleDao = new EventScheduleDAOImpl(conn);
            FoodDAO foodDao = new FoodDAOImpl(conn);
            GradeDAO gradeDao = new GradeDAOImpl();
            LogDAO logDao = new LogDAOImpl(conn);
            OrderDAO orderDao = new OrderDAOImpl(conn);
            ReviewDAO reviewDao = new ReviewDAOImpl(conn);
            SalesReportDAO salesReportDao = new SalesReportDAOImpl(conn);
            StockDAO stockDao = new StockDAOImpl(conn);
            TicketDAO ticketDao = new TicketDAOImpl();
            
            //서비스 생성 및 dao 
            PC_MemberService pcMemberService = new PC_MemberService(memberDao, gradeDao);
            PcCafeService pcCafeService = new PcCafeService(pcCafeDao);
            CustomerService customerService = new CustomerService(conn, customerDao, logDao, memberDao);
            ChargeService chargeService = new ChargeService(chargeDao);
            EmployeeService employeeService = new EmployeeService(employeeDao);
            EventInfoService eventInfoService = new EventInfoService(eventInfoDao);
            EventScheduleService eventScheduleService = new EventScheduleService(eventScheduleDao);
            FoodService foodService = new FoodService(foodDao);
            GradeService gradeService = new GradeService(gradeDao);
            LogService logService = new LogService(logDao);
            ReviewService reviewService = new ReviewService(reviewDao);
            SalesReportService salesReportService = new SalesReportService(salesReportDao);
            StockService stockService = new StockService(stockDao);
            OrderService orderService = new OrderService(conn, orderDao, stockService, foodDao, eventScheduleDao);
            TicketService ticketService = new TicketService(ticketDao);

            //controller 생성 및 뷰, 서비스 주입
            Controller controller = new Controller(
                    view,
                    chargeService,
                    customerService,
                    employeeService,
                    eventInfoService,       
                    eventScheduleService,
                    foodService,
                    gradeService,
                    logService,
                    orderService,
                    pcMemberService,
                    pcCafeService,
                    reviewService,
                    salesReportService,
                    stockService,
                    ticketService
            );

            // 6. 프로그램 메인 루프 실행
            controller.run();

        } catch (SQLException e) {
            System.out.println("DB 연결 또는 쿼리 실행 중 오류가 발생했습니다.");
            e.printStackTrace();
        } finally {
            // 7. 자원 해제
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
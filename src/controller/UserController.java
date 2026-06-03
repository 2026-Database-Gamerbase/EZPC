package controller;

import dao.ChargeDAO;
import dao.CustomerDAO;
import dao.LogDAO;
import dao.OrderDAO;
import dao.PC_MemberDAO;
import dao.PcCafeDAO;
import dao.TicketDAO;
import daoImpl.ChargeDAOImpl;
import daoImpl.CustomerDAOImpl;
import daoImpl.LogDAOImpl;
import daoImpl.OrderDAOImpl;
import daoImpl.PC_MemberDAOImpl;
import daoImpl.PcCafeDAOImpl;
import daoImpl.TicketDAOImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.Charge;
import model.Customer;
import model.PC_Member;
import model.PcCafe;
import model.Ticket;
import service.ChargeService;
import service.CustomerService;
import service.OrderService;
import service.PC_MemberService;
import service.PcCafeService;
import service.TicketService;
import view.View;

public class UserController {
    private final Connection conn;
    private final PC_Member member;
    private final View view = new View();

    public UserController(Connection conn, PC_Member member) {
        this.conn = conn;
        this.member = member;
    }

    public void start() {
        try {
            // build DAOs and services using the provided connection
            PcCafeDAO pcCafeDao = new PcCafeDAOImpl(conn);
            CustomerDAO customerDao = new CustomerDAOImpl(conn);
            LogDAO logDao = new LogDAOImpl(conn);
            PC_MemberDAO memberDao = new PC_MemberDAOImpl(conn);
            ChargeDAO chargeDao = new ChargeDAOImpl(conn);
            OrderDAO orderDao = new OrderDAOImpl(conn);
            TicketDAO ticketDao = new TicketDAOImpl(conn);

            PcCafeService pcCafeService = new PcCafeService(pcCafeDao);
            CustomerService customerService = new CustomerService(conn, customerDao, logDao, memberDao);
            ChargeService chargeService = new ChargeService(chargeDao);
            PC_MemberService pcMemberService = new PC_MemberService(memberDao);
            TicketService ticketService = new TicketService(ticketDao);
            OrderService orderService = new OrderService(conn, orderDao, null, null, null);

            // 1) branch selection
            List<PcCafe> cafes = pcCafeService.getAllPcCafes();
            String pcNum = view.showAllPcCafe(cafes);
            PcCafe selected = pcCafeService.getPcCafe(pcNum);
            if (selected == null) {
                System.out.println("존재하지 않는 지점입니다.");
                return;
            }

            // 2) seat selection
            int totalSeats = selected.getTotalSeats();
            List<Customer> active = customerService.getCustomersInPcCafe(pcNum);
            java.util.List<Integer> occupied = new java.util.ArrayList<>();
            for (Customer c : active) occupied.add(c.getSeatNum());

            while (true) {
                int seat = view.selectSeat(totalSeats, occupied);
                if (seat < 1 || seat > totalSeats) {
                    System.out.println("잘못된 좌석입니다.");
                    continue;
                }
                if (occupied.contains(seat)) {
                    System.out.println("이미 사용 중인 좌석입니다.");
                    continue;
                }

                Customer newCustomer = new Customer();
                newCustomer.setPcCafeId(pcNum);
                newCustomer.setSeatNum(seat);
                newCustomer.setMemberId(member.getMemberId());
                newCustomer.setRemainTime(member.getRemainTime());

                boolean ok = customerService.checkIn(newCustomer);
                if (!ok) {
                    System.out.println("입실 실패. 다른 좌석을 선택해 주세요.");
                    continue;
                }

                System.out.printf("%d번 좌석 입실 완료\n", seat);
                // enter in-seat menu loop
                pcCafeMenuLoop(newCustomer, ticketService, chargeService, customerService, pcMemberService);
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException ignored) {}
        }
    }

    private void pcCafeMenuLoop(Customer customer, TicketService ticketService, ChargeService chargeService,
                                CustomerService customerService, PC_MemberService pcMemberService) {
        while (true) {
            int menu = view.showPcCafeMenu();
            if (menu == 6) return;

            switch (menu) {
                case 1:
                    view.showRemainTime(customer);
                    break;
                case 2:
                    try {
                        List<Ticket> tickets = ticketService.getAllTickets();
                        int choice = view.showTicket(tickets);
                        if (choice < 1 || choice > tickets.size()) {
                            System.out.println("존재하지 않는 이용권입니다.");
                            break;
                        }
                        Ticket t = tickets.get(choice - 1);
                        int addTime = t.getTicketTime();
                        int pay = t.getPrice();

                        Charge ch = new Charge();
                        ch.setPcCafeId(customer.getPcCafeId());
                        ch.setSeatNum(customer.getSeatNum());
                        ch.setMemberId(customer.getMemberId());
                        ch.setTicketTime(addTime);
                        ch.setChargePayAmount(pay);

                        chargeService.recordCharge(ch);
                        int updated = customer.getRemainTime() + addTime;
                        customerService.addRemainingTime(customer.getPcCafeId(), customer.getSeatNum(), updated);
                        customer.setRemainTime(updated);
                        System.out.printf("[결제 완료] %d분 충전되었습니다. 총 잔여 시간: %d분\n", addTime, updated);

                    } catch (SQLException e) {
                        System.out.println("결제 처리 중 오류가 발생했습니다.");
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    System.out.printf("[%d번 좌석] 퇴실 처리 중...\n", customer.getSeatNum());
                    customerService.checkOut(customer);
                    return;
                default:
                    System.out.println("존재하지 않는 메뉴입니다.");
            }
        }
    }
}

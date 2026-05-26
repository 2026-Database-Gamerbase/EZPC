package controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Charge;
import model.Customer;
import model.PC_Member;
import model.Ticket;
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
import view.View;

public class Controller {
	View view;
	ChargeService chargeService;
	CustomerService customerService;
	EmployeeService employeeService;
	EventInfoService eventInfoService;
	EventScheduleService eventScheduleService;
	FoodService foodService;
	GradeService gradeService;
	LogService logService;
	OrderService orderService;
	PC_MemberService pc_memberService;
	PcCafeService pcCafeService;
	ReviewService reviewService;
	SalesReportService salesReportService;
	StockService stockService;
	TicketService ticketService;
	

	public Controller() {}

	public Controller(View view, ChargeService chargeService, CustomerService customerService,
			EmployeeService employeeService, EventInfoService eventInfoService,
			EventScheduleService eventScheduleService, FoodService foodService, GradeService gradeService,
			LogService logService, OrderService orderService, PC_MemberService pc_memberService,
			PcCafeService pcCafeService, ReviewService reviewService, SalesReportService salesReportService,
			StockService stockService, TicketService ticketService) {
		this.view = view;
		this.chargeService = chargeService;
		this.customerService = customerService;
		this.employeeService = employeeService;
		this.eventInfoService = eventInfoService;
		this.eventScheduleService = eventScheduleService;
		this.foodService = foodService;
		this.gradeService = gradeService;
		this.logService = logService;
		this.orderService = orderService;
		this.pc_memberService = pc_memberService;
		this.pcCafeService = pcCafeService;
		this.reviewService = reviewService;
		this.salesReportService = salesReportService;
		this.stockService = stockService;
		this.ticketService = ticketService;
	}


	public void run() throws SQLException {
		
		// 로그인한 유저 정보를 기억할 변수 (비회원이면 null)
		String loggedInMemberId = null;
		// 로그인한 유저의 잔여 시간 (비회원은 00
		int loggedInMemberRemainingTime = 0;
		
		while(true) {
			int enter = view.enter();
			if (enter == 0) {
				System.out.println("안녕히 가십시오.");
				return;
			}
			
			switch(enter) {
				case 1 : //로그인
					List<String> loginInfo = view.login(); //사용자에게 아이디, 비번 입력받음
					PC_Member mem =  pc_memberService.login(loginInfo.getFirst(), loginInfo.getLast()); //로그인
					if (mem == null) { //아이디 or 비번 불일치, 회원 목록에 없다면 다시 입력
						continue;
					}
					loggedInMemberId = loginInfo.getFirst(); //로그인한 회원의 id 저장
					loggedInMemberRemainingTime = mem.getRemainTime(); //로그인한 회원의 잔여 시간 저장
					break;
				case 2 : //회원가입
					List<String> signUpInfo = view.signUp();
					pc_memberService.signUp(signUpInfo.get(0), signUpInfo.get(1), signUpInfo.get(2));
					continue; //가입 후 다시 첫 화면으로 continue
				case 3: //비회원
					loggedInMemberId = null;
					break;
				default:
					System.out.println("존재하지 않는 메뉴입니다. 다시 입력하세요.");
					continue;
			}
			
			//pc방 선택 - pc방 번호 가져오기
				String pcNum = "";
					model.PcCafe selectedCafe = null;
						
						while (true) {
							pcNum = view.showAllPcCafe(pcCafeService.getAllPcCafes());
							selectedCafe = pcCafeService.getPcCafe(pcNum);
							
							if (selectedCafe == null) {
								System.out.println("존재하지 않는 지점 번호입니다. 다시 입력해주세요.");
							} else {
								break;
							}
						}
						
			// 선택한 PC방의 전체 좌석 수 가져오기
			int totalSeats = selectedCafe.getTotalSeats();
			
			// 해당 PC방의 사용 중인 좌석 번호 리스트 만들기
			List<Customer> activeCustomers = customerService.getCustomersInPcCafe(pcNum); //해당 pc방을 이용중인 손님 리스트
			List<Integer> occupiedSeats = new ArrayList<>(); //해당 손님들이 이용 중인 좌석 번호 리스트
			for (Customer c : activeCustomers) {
				occupiedSeats.add(c.getSeatNum()); //해당 pc방을 이용 중인 손님들의 좌석 번호 가져와서 리스트에 넣기
			}
			
			// 손님 좌석에 밀어넣기
			while(true) {
				// 뷰에 정보 넘겨주고 손님이 선택한 번호 받아오기
				int selectedSeat = view.selectSeat(totalSeats, occupiedSeats);
				
				// 5. 유효성 검사 및 입실(Customer 테이블 insert) 처리
				if (selectedSeat < 1 || selectedSeat > totalSeats) {
					System.out.println("존재하지 않는 좌석입니다. 다시 선택해주세요.");
					continue;
				} else if (occupiedSeats.contains(selectedSeat)) {
					System.out.println("이미 사용 중인 좌석입니다. 다른 좌석을 선택해주세요.");
					continue;
				} else {
					// 빈 자리라면 입실 시도
					model.Customer newCustomer = new model.Customer();
					newCustomer.setPcCafeId(pcNum);
					newCustomer.setSeatNum(selectedSeat);
					newCustomer.setMemberId(loggedInMemberId);
					newCustomer.setRemainingTime(loggedInMemberRemainingTime);
					
					// 동시성 방어 로직 (0.1초 차이로 좌석 뺏긴 경우 처리)
					boolean isSuccess = customerService.checkIn(newCustomer);
					
					if (isSuccess) {
						System.out.println(selectedSeat + "번 좌석 입실이 완료되었습니다!");
						runPcCafeMenu(newCustomer);
						break;
					} else {
						System.out.println("앗! 그 사이 다른 손님이 자리를 차지했습니다. 다른 좌석을 선택해주세요.");
						continue; // 다시 좌석 선택으로 돌아감
					}
				}
			}
			
			return;
		}
	}
	
	public void runPcCafeMenu(Customer customer) throws SQLException {
		//잔여시간 보여줌
		while(true) {
			int menu = view.showPcCafeMenu();
			if (menu == 6) {
				System.out.println("로그아웃 되었습니다. 안녕히 가십시오");
				return;
			}
			
			switch(menu) {
			case 1 : //잔여 시간 보기
				view.showRemainTime(customer);
				continue;
			case 2 : // 충전하기
				//이용권 종류 보여주기
				List<Ticket> tickets = ticketService.getAllTickets();
				int ticketChoice = view.showTicket(tickets);
				if (ticketChoice < 1 || ticketChoice > tickets.size()) {
					System.out.println("존재하지 않는 이용권 번호입니다. 다시 선택해 주세요.");
					continue;
				}
				
				//손님이 선택한 이용권
				Ticket selectedTicket = tickets.get(ticketChoice - 1); //이용권 테이블의 행 번호 구하기
				int addTime = selectedTicket.getTicketTime(); // 선택된 이용권의 시간(분)
				int payAmount = selectedTicket.getPrice();     // 선택된 이용권의 가격
				
				// 충전 내역 로그 객체 생성 및 세팅
				Charge newCharge = new Charge();
				newCharge.setPcCafeId(customer.getPcCafeId()); //pc방 번호
				newCharge.setSeatNum(customer.getSeatNum()); //좌석 번호
				newCharge.setMemberId(customer.getMemberId());  //회원 번호
				newCharge.setTicketTime(addTime); //추가한 시간
				newCharge.setChargePayAmount(payAmount); //가격
				
				try {
					//충전 기록
					chargeService.recordCharge(newCharge);
					
					//손님 테이블 시간 누적 업데이트
					int updatedTime = customer.getRemainingTime() + addTime;
					customerService.addRemainingTime(customer.getPcCafeId(), customer.getSeatNum(), updatedTime);
					
					//현재 콘솔을 이용 중인 customer 객체의 잔여 시간 필드도 동기화 (화면 출력용)
					customer.setRemainingTime(updatedTime);
					
					//회원일 경우에만 누적 결제금액 증가 및 등급 반영
					if (customer.getMemberId() != null) {
						pc_memberService.chargeTime(customer.getMemberId(), addTime, payAmount); //회원아이디, 추가 시간, 결제 금액
					}
					
					System.out.printf("\n[결제 완료] 정상 처리되었습니다. %d분이 충전되었습니다. 총 잔여 시간: %d분\n", addTime, updatedTime);
					
				} catch (SQLException e) {
					System.out.println("결제 처리 중 서버 오류가 발생했습니다.");
					e.printStackTrace();
				}
				
				continue;
//			case 3: //주문하기 (잔여 시간 0분이면 이용 불가)
//				if(customer.getRemainingTime() <= 0) {
//					System.out.println("잔여 시간이 남아있지 않아 주문이 불가능합니다. 이용권을 결제하세요");
//					continue;
//				}
//				view.showFoodMenu(foodService.getMenuBoard());
//				continue;
//			case 4: //게임하기 (1시간 감소? 그냥 의미없는 * 아트보여주고 머문 시간 체크해서 잔여시간 감소?
//				view.playGame();
//			case 5: //리뷰 작성하기
//				if(customer.getMemberId() == null) {
//					System.out.println("회원만 리뷰를 작성할 수 있습니다.");
//				}
//				List<String> review = view.showReviewFormat();
				//음식주문하기
				//로그아웃
			case 3: //로그아웃
				System.out.printf("[%d번 좌석] 퇴실 처리가 완료되었습니다.\n", customer.getSeatNum());
				customerService.checkOut(customer);
				return;
			default:
				System.out.println("존재하지 않는 메뉴입니다. 다시 입력하세요.");
				continue;
		}
		}
	}

}

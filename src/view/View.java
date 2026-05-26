package view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.Customer;
import model.PcCafe;
import model.Ticket;

public class View {
	static Scanner input=new Scanner(System.in);
	
	//입장
	public int enter() {
		System.out.println("=====================================================================");
		System.out.println("체인 EZPC방에 오신 것을 환영합니다.");
		System.out.println("원하시는 메뉴를 선택해주세요.");
		System.out.println("1.로그인 하기 2.회원가입 하기 3. 비회원 이용하기 0.종료");
		System.out.println("=====================================================================");
		System.out.print("메뉴 : ");
		
		try {
			// 입력받은 문자열을 숫자로 변환 시도
			return Integer.parseInt(input.nextLine()); 
		} catch (NumberFormatException e) {
			// 숫자가 아닌 문자가 들어오면 -1 리턴
			return -1; 
		}
	}
	
	//로그인
	public List<String> login() {
		
		List<String> loginInfo = new ArrayList<>();
		
		System.out.println("=====================================================================");
		System.out.print("아이디: "); String id = input.nextLine(); loginInfo.add(id);
		System.out.print("비밀번호: "); String pwd = input.nextLine(); loginInfo.add(pwd);
		System.out.println("=====================================================================");
		return loginInfo;
	}
	
	//회원가입
	public List<String> signUp() {
		
		List<String> signUpInfo = new ArrayList<>();
		
		System.out.println("=====================================================================");
		System.out.print("아이디: "); String id = input.nextLine(); signUpInfo.add(id);
		System.out.print("비밀번호: "); String pwd = input.nextLine(); signUpInfo.add(pwd);
		System.out.print("이름: "); String name = input.nextLine(); signUpInfo.add(name);
		System.out.println("=====================================================================");
		return signUpInfo;
	}
	
	//pc방 목록 보여주기
	public String showAllPcCafe(List<PcCafe> pcCafes) {
		
		System.out.println("=====================================================================");
		System.out.println("                         체인 E Z P C 방                              ");
		System.out.println("이용하고자 하는 PC방 지점을 선택하세요!");
		System.out.printf("%3s|%10s|%8s|%10s \n", "pc방 번호", "지점", "평균 별점", "총 좌석 수");
		System.out.println("=====================================================================");
		for (PcCafe pc : pcCafes) {
			System.out.printf("%3s|%10s|%8s|%10s \n", pc.getPcId(), pc.getPcName(), pc.getAverageStarRating(), pc.getTotalSeats());
		}
		
		System.out.print("pc방 번호: ");
		String pcNum = input.nextLine().toUpperCase(); //자동 대문자 변환
		
		return pcNum;
		
	}
	
	//좌석 선택하기 화면
	public int selectSeat(int totalSeats, List<Integer> occupiedSeats) {
		
		System.out.println("=====================================================================");
		System.out.println("                            [ 좌석 배치도 ]                             ");
		System.out.println("=====================================================================");
			
		// 1번부터 totalSeats까지 반복하면서 좌석 출력
		for (int i = 1; i <= totalSeats; i++) {
			if (occupiedSeats.contains(i)) {
				System.out.printf("[ %2s ] ", "X"); // 이미 이용 중인 좌석은 X 표시
			} else {
				System.out.printf("[ %2d ] ", i);  // 빈 좌석은 번호 표시
			}
				
			// 5좌석마다 줄바꿈을 해서 보기 좋게 정렬 (원하는 대로 숫자 수정 가능)
			if (i % 5 == 0) {
				System.out.println();
			}
		}
			
		System.out.println("\n=====================================================================");
		System.out.print("원하시는 좌석 번호를 입력하세요: ");
		try {
			return Integer.parseInt(input.nextLine()); 
		} catch (NumberFormatException e) {
			return -1; //숫자가 아닌 문자가 들어오면 -1 리턴
		}
	}
	
	//pc방 내부 메뉴
	public int showPcCafeMenu() {
		System.out.println("======================================================================");
		System.out.println("                        EZPC에 오신걸 환영합니다                            ");
		System.out.println("원하시는 메뉴를 선택해주세요.");
		System.out.println("1.잔여시간 확인하기 2.이용권 결제하기 3.로그아웃");
		System.out.println("======================================================================");
		System.out.print("메뉴 : ");
		try {
			// 입력받은 문자열을 숫자로 변환 시도
			return Integer.parseInt(input.nextLine()); 
		} catch (NumberFormatException e) {
			// 숫자가 아닌 문자가 들어오면 -1 리턴
			return -1; 
		}
	}

	//잔여 시간 조회하기
	public void showRemainTime(Customer c) {
		//현재 시스템 시간 가져오기
		LocalDateTime now = LocalDateTime.now();
			
		// 포맷팅 ex.2026-05-26 21:32:08
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String currentTime = now.format(formatter);
			
		System.out.printf("손님의 잔여 시간: %d분 남았습니다. (현재 시각 : %s 기준)\n", c.getRemainingTime(), currentTime);
	}
	
	//이용권 보여주기
	public int showTicket(List<Ticket> tickets) {
		System.out.println("========== 이 용 권 ===========");
		System.out.printf("%2s|%5s|%5s|\n", "번호", "시간 (분)", "가격 (원)");
		System.out.println("=============================");
		
		for (int i = 0; i < tickets.size(); i++) {
			System.out.printf("%2s번|%5s분|%5s원|\n", i+1, tickets.get(i).getTicketTime(), tickets.get(i).getPrice());
		}
		
		System.out.print("원하시는 이용권 번호를 입력하세요: ");
		
		try {
			// 입력받은 문자열을 숫자로 변환 시도
			return Integer.parseInt(input.nextLine()); 
		} catch (NumberFormatException e) {
			// 숫자가 아닌 문자가 들어오면 -1 리턴
			return -1; 
		}
		
	}
}
		

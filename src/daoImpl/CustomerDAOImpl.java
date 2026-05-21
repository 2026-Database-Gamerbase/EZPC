package daoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.CustomerDAO;
import model.Customer;

public class CustomerDAOImpl implements CustomerDAO {

	private Connection conn;

	public CustomerDAOImpl(Connection conn) {
		this.conn = conn;
	}

	// pc방 선택 -> 좌석 선택 -> 로그인 또는 비회원 이용 선택 후 최종적으로 customer 테이블에 삽입
	@Override
	public void insertCustomer(Customer customer) {
		String sql = "INSERT INTO customer (pc_cafe_id, seat_num, member_id, remaining_time) VALUES (?, ?, ?, ?)";
		int result = -1;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, customer.getPcCafeId());
			pstmt.setInt(2, customer.getSeatNum());
			pstmt.setString(3, customer.getMemberId()); // 회원 ID 또는 비회원일 경우 null
			pstmt.setInt(4, customer.getRemainingTime());

			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("손님 등록 중 오류 발생");
		}

		if (result > 0) {
			System.out.printf("[%s] %d번 좌석에 손님 등록이 완료되었습니다.\n", 
					getPcCafeName(customer.getPcCafeId()), customer.getSeatNum());
		} else {
			System.out.println("손님 등록 실패: 입력 데이터를 확인하세요.");
		}
	}

	// 자리 이동 (비회원 대응을 위해 지점 번호와 기존 좌석 번호로 식별)
	@Override
	public void updateSeatNo(String pcCafeId, int oldSeatNum, int newSeatNum) {
		String sql = "UPDATE customer SET seat_num = ? WHERE pc_cafe_id = ? AND seat_num = ?";
		int result = -1;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, newSeatNum);
			pstmt.setString(2, pcCafeId);
			pstmt.setInt(3, oldSeatNum);

			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("좌석 이동 중 오류 발생");
		}

		if (result > 0) {
			System.out.printf("[%s] 손님의 좌석이 %d번에서 %d번으로 변경되었습니다.\n", 
					getPcCafeName(pcCafeId), oldSeatNum, newSeatNum);
		} else {
			System.out.println("좌석 이동 실패: 유효한 좌석 번호인지 확인하세요.");
		}
	}

	// 로그아웃
	@Override
	public void deleteCustomer(String pcCafeId, int seatNum) {
		String sql = "DELETE FROM customer WHERE pc_cafe_id = ? AND seat_num = ?";
		int result = -1;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, pcCafeId);
			pstmt.setInt(2, seatNum);

			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("로그아웃 처리 중 오류 발생");
		}

		if (result > 0) {
			System.out.printf("[%s] %d번 좌석의 손님이 로그아웃되었습니다.\n", getPcCafeName(pcCafeId), seatNum);
		} else {
			System.out.println("퇴실 처리 실패: 해당 좌석에 손님이 없습니다.");
		}
	}

	// 실시간 잔여 시간 갱신
	@Override
	public void updateRemainingTime(String pcCafeId, int seatNum, int remainingTime) {
		String sql = "UPDATE customer SET remaining_time = ? WHERE pc_cafe_id = ? AND seat_num = ?";
		int result = -1;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, remainingTime);
			pstmt.setString(2, pcCafeId);
			pstmt.setInt(3, seatNum);

			result = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("실시간 잔여 시간 갱신 중 오류 발생");
		}

		if (result > 0) {
			System.out.printf("[%s] %d번 좌석 잔여 시간 갱신: %d분\n", getPcCafeName(pcCafeId), seatNum, remainingTime);
		}
	}

	// 특정 PC방의 손님 조회 - 좌석 선택 단계에서 호출하는 함수
	@Override
	public List<Customer> findByPcCafeId(String pcCafeId) {
		String sql = "SELECT * FROM customer WHERE pc_cafe_id = ?";
		List<Customer> customers = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, pcCafeId);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					customers.add(mapRowToCustomer(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("특정 PC방의 손님 조회 중 오류 발생");
		}
		return customers;
	}

	// 특정 좌석의 상태 조회 (빈자리 여부)
	@Override
	public Customer findBySeatNo(String pcCafeId, int seatNum) {
		String sql = "SELECT * FROM customer WHERE pc_cafe_id = ? AND seat_num = ?";
		Customer customer = null;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, pcCafeId);
			pstmt.setInt(2, seatNum);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					customer = mapRowToCustomer(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("특정 PC방의 좌석별 손님 조회 중 오류 발생");
		}
		return customer;
	}

	// 특정 손님의 세션 조회 - 회원 중복 로그인 방지 단계에서 호출
	@Override
	public Customer findByMemberId(String memberId) {
		// 비회원은 중복 로그인 체크 대상x
		if (memberId == null) {
			return null;
		}
		
		String sql = "SELECT * FROM customer WHERE member_id = ?";
		Customer customer = null;

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, memberId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					customer = mapRowToCustomer(rs);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("회원 ID별 조회 중 오류 발생");
		}
		return customer;
	}

	//체인 pc방을 이용중인 전체 손님 조회
	@Override
	public List<Customer> findAll() {
		String sql = "SELECT * FROM customer";
		List<Customer> customers = new ArrayList<>();

		try (PreparedStatement pstmt = conn.prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				customers.add(mapRowToCustomer(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("전체 이용객 조회 중 오류 발생");
		}
		return customers;
	}

	//ResultSet 데이터를 Customer 객체로 매핑
	private Customer mapRowToCustomer(ResultSet rs) throws SQLException {
		Customer c = new Customer();
		c.setPcCafeId(rs.getString("pc_cafe_id"));
		c.setSeatNum(rs.getInt("seat_num"));
		c.setMemberId(rs.getString("member_id"));
		c.setRemainingTime(rs.getInt("remaining_time"));
		return c;
	}
	
	//pc방 id로 pc방 이름 찾기 (출력 메시지 가독성용)
	private String getPcCafeName(String pcCafeId) {
		// TODO Auto-generated method stub
		String sql = "SELECT pc_cafe_name FROM pc_cafe WHERE pc_cafe_id = ?";
		
		try (PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, pcCafeId);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if (rs.next()) {
					return rs.getString(1);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("pc방 번호로 pc방 이름을 가져오는 중 오류 발생");
		}
		
		return null;
	}

}
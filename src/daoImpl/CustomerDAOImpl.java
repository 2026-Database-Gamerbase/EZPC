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

	// 로그인/회원가입/비회원 -> pc방 선택 -> 좌석 선택 후 최종적으로 customer 테이블에 삽입
	@Override
	public boolean insertCustomer(Customer customer) {
		String sql = "INSERT INTO customer (pc_cafe_id, seat_num, member_id, remaining_time) VALUES (?, ?, ?, ?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, customer.getPcCafeId());
			pstmt.setInt(2, customer.getSeatNum());
			pstmt.setString(3, customer.getMemberId()); 
			pstmt.setInt(4, customer.getRemainingTime());

			int result = pstmt.executeUpdate();
			return result > 0; // 성공하면 true
			
		} catch (SQLException e) {
			// 누군가 0.1초 차이로 먼저 자리를 차지해서 DB 유니크 에러가 났을 때
			return false; 
		}
	}

	// 자리 이동 (비회원 대응을 위해 지점 번호와 기존 좌석 번호로 식별)
	@Override
	public void updateSeatNo(String pcCafeId, int oldSeatNum, int newSeatNum) {
		String sql = "UPDATE customer SET seat_num = ? WHERE pc_cafe_id = ? AND seat_num = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, newSeatNum);
			pstmt.setString(2, pcCafeId);
			pstmt.setInt(3, oldSeatNum);

			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("좌석 이동 중 오류 발생");
		}

	}

	// 로그아웃
	@Override
	public void deleteCustomer(String pcCafeId, int seatNum) {
		String sql = "DELETE FROM customer WHERE pc_cafe_id = ? AND seat_num = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, pcCafeId);
			pstmt.setInt(2, seatNum);

			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 실시간 잔여 시간 갱신
	@Override
	public void updateRemainingTime(String pcCafeId, int seatNum, int updatedTime) {
		String sql = "UPDATE customer SET remaining_time = ? WHERE pc_cafe_id = ? AND seat_num = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, updatedTime);
			pstmt.setString(2, pcCafeId);
			pstmt.setInt(3, seatNum);

			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
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
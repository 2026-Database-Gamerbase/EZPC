package service;

import java.sql.Connection;
import java.time.Duration;
import java.util.List;

import dao.CustomerDAO;
import dao.LogDAO;
import dao.PC_MemberDAO;
import model.Customer;
import model.Log;

import java.sql.SQLException;

public class CustomerService {
	private Connection conn;
    private CustomerDAO customerDao;
    private LogDAO logDao;
    private PC_MemberDAO memberDao;
    

    public CustomerService(Connection conn, CustomerDAO customerDao, LogDAO logDao, PC_MemberDAO memberDao) {
    	this.conn = conn;
        this.customerDao = customerDao;
        this.logDao = logDao;
        this.memberDao = memberDao;
    }

    // 특정 PC방의 현재 이용 중인 손님 리스트 가져오기
    public List<Customer> getCustomersInPcCafe(String pcCafeId) {
        return customerDao.findByPcCafeId(pcCafeId);
    }
    
    // 입실 처리 (좌석 선택 완료 시 호출)
    public boolean checkIn(Customer customer) {
        return customerDao.insertCustomer(customer);
    }
    
    //잔여 시간 충전
    public void addRemainingTime(String pcCafeId, int SeatNum, int updatedTime) {
    	customerDao.updateRemainingTime(pcCafeId, SeatNum, updatedTime);
    }
    
    //퇴실 처리
    public void checkOut(Customer customer) {
    	try {
    		//트랜잭션 시작 
    		 conn.setAutoCommit(false);
    		 
    		 
    	//logout 시간 기록 
    	logDao.updateLogoutTime(customer.getPcCafeId(), customer.getSeatNum());
    	
    	
    	// 로그 기록 가져오기 
    	Log log = logDao.findLatestLogoutLog(customer.getPcCafeId(), customer.getSeatNum());
    	
    	if (log == null) {
    	           throw new RuntimeException("퇴실 로그를 찾을 수 없습니다.");
    	            }		
 
        // 로그인 시간 또는 로그아웃 시간이 비어있는 경우 error
        if (log.getLoginTime() == null || log.getLogoutTime() == null) {
            throw new RuntimeException("입실 또는 퇴실 시간이 기록되지 않았습니다. (시간 데이터 누락)");
        }
        
    	
    	 // 사용 시간 계산
        int usedTime = (int) Duration.between(
                log.getLoginTime(),
                log.getLogoutTime()
        ).toMinutes();
        
        //회원이라면 잔여시간 갱신 
        if (customer.getMemberId() != null) {
        	memberDao.updateRemainTimeAfterUse(
                     customer.getMemberId(),
                     customer.getRemainTime(),
                     usedTime
             );
        	
        }
        
        // customer 테이블에서 현재 지점 번호와 좌석 번호로 손님 삭제
    	customerDao.deleteCustomer(customer.getPcCafeId(), customer.getSeatNum());
        
    	conn.commit();
        
    			
    	} catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("퇴실 처리 롤백 실패", rollbackEx);
            }

            throw new RuntimeException("퇴실 처리 실패", e);

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("AutoCommit 복구 실패", e);
            }
        }
    	
    }
    	
    	
    	
}
package service;

import java.util.List;
import dao.CustomerDAO;
import model.Customer;

public class CustomerService {
    private CustomerDAO customerDao;

    public CustomerService(CustomerDAO customerDao) {
        this.customerDao = customerDao;
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
        // customer 테이블에서 현재 지점 번호와 좌석 번호로 손님 삭제
        customerDao.deleteCustomer(customer.getPcCafeId(), customer.getSeatNum());
    }
}
package service;

import java.sql.SQLException;

import java.util.List;

import dao.ChargeDAO;
import daoImpl.ChargeDAOImpl;
import model.Charge;

public class ChargeService {
    private final ChargeDAO chargeDAO;

   // public ChargeService() {
   //     this(new ChargeDAOImpl());
   // }

    public ChargeService(ChargeDAO chargeDAO) {
        this.chargeDAO = chargeDAO;
    }

//    public int recordCharge(Charge charge) throws SQLException {
//    		validateCharge(charge);
//    		return chargeDAO.insert(charge);
//    }
    
	  public void recordCharge(Charge charge) throws SQLException {
		validateCharge(charge);
		// 충전시 등급, 총 결제 금액, 이벤트 확인 진행 (프로시저) 
		chargeDAO.chargeByCustomer(charge);
	}

    public Charge getCharge(int chargeId) throws SQLException {
        return chargeDAO.findById(chargeId);
    }

    public List<Charge> getAllCharges() throws SQLException {
        return chargeDAO.findAll();
    }

    public List<Charge> getChargesByPcCafe(String pcCafeId) throws SQLException {
        return chargeDAO.findByPcCafeId(pcCafeId);
    }

    public List<Charge> getChargesByMember(String memberId) throws SQLException {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new IllegalArgumentException("Member ID is required to query charges by member.");
        }
        return chargeDAO.findByMemberId(memberId);
    }

    public void removeCharge(int chargeId) throws SQLException {
        chargeDAO.deleteById(chargeId);
    }

    private void validateCharge(Charge charge) {
        if (charge == null) {
            throw new IllegalArgumentException("Charge data is required.");
        }
        if (charge.getPcCafeId() == null || charge.getPcCafeId().trim().isEmpty()) {
            throw new IllegalArgumentException("PC cafe ID is required.");
        }
        if (charge.getSeatNum() <= 0) {
            throw new IllegalArgumentException("Seat number must be a positive integer.");
        }
        if (charge.getTicketTime() <= 0) {
            throw new IllegalArgumentException("Ticket time must be a positive integer.");
        }
        if (charge.getChargePayAmount() < 0) {
            throw new IllegalArgumentException("Charge payment amount cannot be negative.");
        }
    }
}

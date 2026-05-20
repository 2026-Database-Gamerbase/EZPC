package dao;

import java.sql.SQLException;
import java.util.List;
import model.Charge;

public interface ChargeDAO {
    int insert(Charge charge) throws SQLException;

    Charge findById(int chargeId) throws SQLException;

    List<Charge> findAll() throws SQLException;

    List<Charge> findByPcCafeId(String pcCafeId) throws SQLException;

    List<Charge> findByMemberId(String memberId) throws SQLException;

    void deleteById(int chargeId) throws SQLException;
}

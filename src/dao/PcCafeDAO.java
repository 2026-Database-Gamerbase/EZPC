package dao;

import java.sql.SQLException;
import java.util.List;

import model.PcCafe;

public interface PcCafeDAO {
    void insert(PcCafe pcCafe) throws SQLException;

    PcCafe findById(String pcId) throws SQLException;

    List<PcCafe> findAll() throws SQLException;

    void update(PcCafe pcCafe) throws SQLException;

    void deleteById(String pcId) throws SQLException;
}

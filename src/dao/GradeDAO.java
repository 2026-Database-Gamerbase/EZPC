package dao;

import java.sql.SQLException;
import java.util.List;
import model.Grade;

public interface GradeDAO {
    void insert(Grade grade) throws SQLException;

    Grade findByType(String gradeType) throws SQLException;

    List<Grade> findAll() throws SQLException;

    void update(Grade grade) throws SQLException;

    void deleteByType(String gradeType) throws SQLException;
}

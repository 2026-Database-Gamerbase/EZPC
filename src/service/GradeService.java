package service;

import java.sql.SQLException;
import java.util.List;

import dao.GradeDAO;
import daoImpl.GradeDAOImpl;
import model.Grade;

public class GradeService {
    private final GradeDAO gradeDAO;

    public GradeService(GradeDAO gradeDAO) {
        this.gradeDAO = gradeDAO;
    }

    public void addGrade(Grade grade) throws SQLException {
        validateGrade(grade);
        gradeDAO.insert(grade);
    }

    public Grade getGrade(String gradeType) throws SQLException {
        return gradeDAO.findByType(gradeType);
    }

    public List<Grade> getAllGrades() throws SQLException {
        return gradeDAO.findAll();
    }

    public void updateGrade(Grade grade) throws SQLException {
        validateGrade(grade);
        gradeDAO.update(grade);
    }

    public void removeGrade(String gradeType) throws SQLException {
        gradeDAO.deleteByType(gradeType);
    }

    private void validateGrade(Grade grade) {
        if (grade == null) {
            throw new IllegalArgumentException("Grade data is required.");
        }
        if (grade.getGradeType() == null || grade.getGradeType().trim().isEmpty()) {
            throw new IllegalArgumentException("Grade type is required.");
        }
        if (grade.getBenefit() < 0.0 || grade.getBenefit() > 1.0) {
            throw new IllegalArgumentException("Grade benefit must be between 0.00 and 1.00.");
        }
        if (grade.getGradeStandard() < 0) {
            throw new IllegalArgumentException("Grade standard must be a non-negative integer.");
        }
    }
}

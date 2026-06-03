package daoImpl;

import model.Grade;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.GradeDAO;

public class GradeDAOImpl implements GradeDAO {

    private final Connection conn;

    public GradeDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Grade grade) throws SQLException {
        String sql = "INSERT INTO grade (grade_type, benefit, grade_standard) VALUES (?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, grade.getGradeType());
            statement.setDouble(2, grade.getBenefit());
            statement.setInt(3, grade.getGradeStandard());
            statement.executeUpdate();
        }
    }

    @Override
    public Grade findByType(String gradeType) throws SQLException {
        String sql = "SELECT * FROM grade WHERE grade_type = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, gradeType);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToGrade(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Grade> findAll() throws SQLException {
        String sql = "SELECT * FROM grade";
        List<Grade> grades = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                grades.add(mapToGrade(resultSet));
            }
        }

        return grades;
    }

    @Override
    public void update(Grade grade) throws SQLException {
        String sql = "UPDATE grade SET benefit = ?, grade_standard = ? WHERE grade_type = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setDouble(1, grade.getBenefit());
            statement.setInt(2, grade.getGradeStandard());
            statement.setString(3, grade.getGradeType());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteByType(String gradeType) throws SQLException {
        String sql = "DELETE FROM grade WHERE grade_type = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, gradeType);
            statement.executeUpdate();
        }
    }

    private Grade mapToGrade(ResultSet resultSet) throws SQLException {
        return new Grade(
                resultSet.getString("grade_type"),
                resultSet.getDouble("benefit"),
                resultSet.getInt("grade_standard")
        );
    }
}

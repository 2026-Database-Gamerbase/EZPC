package employee;

import db.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {
    @Override
    public int insert(Employee employee) throws SQLException {
        // 직원 1명 추가 후 자동 생성된 ID 반환 / Insert one employee and return the generated id.
        String sql = "INSERT INTO employee (employee_name, pc_cafe_id, employee_position, hour_wage, is_currently_working) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, employee.getEmployeeName());
            statement.setString(2, employee.getPcId());
            statement.setString(3, employee.getEmployeePosition());
            statement.setInt(4, employee.getHourWage());
            statement.setBoolean(5, employee.isCurrentlyWorking());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int employeeId = generatedKeys.getInt(1);
                    employee.setEmployeeId(employeeId);
                    return employeeId;
                }
            }
        }

        return 0;
    }

    @Override
    public Employee findById(int employeeId) throws SQLException {
        // 기본키로 직원 1명 조회 / Select one employee by primary key.
        String sql = "SELECT * FROM employee WHERE employee_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToEmployee(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Employee> findAll() throws SQLException {
        // 직원 전체 조회 / Select every employee row.
        String sql = "SELECT * FROM employee";
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                employees.add(mapToEmployee(resultSet));
            }
        }

        return employees;
    }

    @Override
    public List<Employee> findByPcId(String pcId) throws SQLException {
        // 특정 PC방의 직원 조회 / Select employees that belong to one pc cafe.
        String sql = "SELECT * FROM employee WHERE pc_cafe_id = ?";
        List<Employee> employees = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pcId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    employees.add(mapToEmployee(resultSet));
                }
            }
        }

        return employees;
    }

    @Override
    public void update(Employee employee) throws SQLException {
        // 기본키로 직원 정보 수정 / Update employee data by primary key.
        String sql = "UPDATE employee SET employee_name = ?, pc_cafe_id = ?, employee_position = ?, hour_wage = ?, is_currently_working = ? WHERE employee_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, employee.getEmployeeName());
            statement.setString(2, employee.getPcId());
            statement.setString(3, employee.getEmployeePosition());
            statement.setInt(4, employee.getHourWage());
            statement.setBoolean(5, employee.isCurrentlyWorking());
            statement.setInt(6, employee.getEmployeeId());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteById(int employeeId) throws SQLException {
        // 기본키로 직원 1명 삭제 / Delete one employee by primary key.
        String sql = "DELETE FROM employee WHERE employee_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            statement.executeUpdate();
        }
    }

    // 조회 결과 한 줄을 Employee 객체로 변환 / Convert one ResultSet row into an Employee object.
    private Employee mapToEmployee(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("employee_id"),
                resultSet.getString("employee_name"),
                resultSet.getString("pc_cafe_id"),
                resultSet.getString("employee_position"),
                resultSet.getInt("hour_wage"),
                resultSet.getBoolean("is_currently_working")
        );
    }
}

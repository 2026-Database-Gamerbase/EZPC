package dao;

import java.sql.SQLException;
import java.util.List;

import model.Employee;

public interface EmployeeDAO {
    int insert(Employee employee) throws SQLException;

    Employee findById(int employeeId) throws SQLException;

    List<Employee> findAll() throws SQLException;

    List<Employee> findByPcId(String pcId) throws SQLException;

    void update(Employee employee) throws SQLException;

    void deleteById(int employeeId) throws SQLException;
}

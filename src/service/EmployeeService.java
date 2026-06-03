package service;

import java.sql.SQLException;
import java.util.List;

import dao.EmployeeDAO;
import daoImpl.EmployeeDAOImpl;
import model.Employee;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;

    public EmployeeService(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
    }

    public int insertEmployee(Employee employee) throws SQLException {
        return employeeDAO.insert(employee);
    }

    public Employee getEmployee(int employeeId) throws SQLException {
        return employeeDAO.findById(employeeId);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.findAll();
    }

    public List<Employee> getEmployeesByPc(String pcId) throws SQLException {
        return employeeDAO.findByPcId(pcId);
    }

    public void updateEmployee(Employee employee) throws SQLException {
        employeeDAO.update(employee);
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        employeeDAO.deleteById(employeeId);
    }
}

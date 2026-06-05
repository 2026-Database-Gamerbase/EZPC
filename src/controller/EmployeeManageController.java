package controller;

import java.awt.GridLayout;
import java.util.List;
import javax.swing.*;
import model.Employee;
import service.EmployeeService;
import view.owner.OwnerEmployeeManageView;

public class EmployeeManageController {
    private final OwnerEmployeeManageView view;
    private final EmployeeService employeeService;
    private String currentBranchId;

    public EmployeeManageController(OwnerEmployeeManageView view, EmployeeService employeeService) {
        this.view = view;
        this.employeeService = employeeService;
        initEventBindings();
    }

    private void initEventBindings() {
        view.setSaveButtonListener(e -> handleUpdateEmployee());
        view.setHireButtonListener(e -> handleHireEmployee());
        view.setFireButtonListener(e -> handleFireEmployee());

        // 테이블 행 클릭 시 직원의 현재 시급/출근 상태를 하단 폼에 동기화
        view.setTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateInputFieldsFromSelection();
            }
        });
    }

    // 뷰에서 선택된 행 데이터를 가져와서 스피너와 체크박스 값 세팅
    private void updateInputFieldsFromSelection() {
        String wageStr = view.getSelectedWageString();
        String attendanceStr = view.getSelectedAttendanceString();
        
        if (wageStr != null) {
            try {
                // "13,000원" 형태의 문자열에서 숫자만 추출
                int wage = Integer.parseInt(wageStr.replaceAll("[^0-9]", ""));
                view.setWage(wage);
            } catch (NumberFormatException ignored) {}
        }
        
        if (attendanceStr != null) {
            view.setAttendanceChecked("출근중".equals(attendanceStr));
        }
    }

    // 직원 목록 로드
    public void refreshEmployeeList(String branchId) {
        this.currentBranchId = branchId;
        try {
            List<Employee> employees = employeeService.getEmployeesByPc(branchId);
            
            Object[][] tableData = new Object[employees.size()][5];
            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                tableData[i][0] = emp.getEmployeeId();
                tableData[i][1] = emp.getEmployeeName();
                tableData[i][2] = emp.getEmployeePosition();
                tableData[i][3] = String.format("%,d원", emp.getHourWage());
                tableData[i][4] = emp.isCurrentlyWorking() ? "출근중" : "퇴근";
            }
            view.setEmployeeTableData(tableData);
        } catch (Exception e) {
            System.err.println("[EmployeeManageController] 직원 목록 로드 실패");
            e.printStackTrace();
        }
    }

    // 기존 직원 정보 업데이트
    private void handleUpdateEmployee() {
        String empIdStr = view.getSelectedEmployeeId();
        if (empIdStr == null || empIdStr.equals("null")) {
            JOptionPane.showMessageDialog(view, "수정할 직원을 테이블에서 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int empId = Integer.parseInt(empIdStr);
            int newWage = view.getWage();
            boolean isWorking = view.isAttendanceChecked();

            Employee emp = employeeService.getEmployee(empId);
            if (emp != null) {
                emp.setHourWage(newWage);
                emp.setCurrentlyWorking(isWorking);
                
                employeeService.updateEmployee(emp);
                view.setStatusMessage("[" + emp.getEmployeeName() + "] 직원의 정보가 수정되었습니다.");
                view.clearInputForm();
                refreshEmployeeList(currentBranchId);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "직원 정보 수정 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // 신규 채용 (이름, 직급 콤보박스, 시급 입력 받기)
    private void handleHireEmployee() {
        JTextField nameField = new JTextField(10);
        JComboBox<String> positionCombo = new JComboBox<>(new String[]{"매니저", "아르바이트"});
        JSpinner wageSpinner = new JSpinner(new SpinnerNumberModel(10000, 0, 50000, 1000));

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("이름:"));
        panel.add(nameField);
        panel.add(new JLabel("직급:"));
        panel.add(positionCombo);
        panel.add(new JLabel("시급 (원):"));
        panel.add(wageSpinner);

        int result = JOptionPane.showConfirmDialog(view, panel, "신규 채용", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String position = positionCombo.getSelectedItem().toString();
            int wage = (int) wageSpinner.getValue();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(view, "이름을 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Employee newEmp = new Employee(name, currentBranchId, position, wage, false);
                employeeService.insertEmployee(newEmp);
                
                view.setStatusMessage("[" + name + "]님이 성공적으로 채용되었습니다.");
                refreshEmployeeList(currentBranchId);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "채용 처리 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 해고
    private void handleFireEmployee() {
        String empIdStr = view.getSelectedEmployeeId();
        String empName = view.getSelectedEmployeeName();
        if (empIdStr == null) {
            JOptionPane.showMessageDialog(view, "해고 처리할 직원을 테이블에서 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, 
                "정말로 [" + empName + "] 직원을 해고 처리하시겠습니까?", "해고 경고", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            employeeService.deleteEmployee(Integer.parseInt(empIdStr));
            view.setStatusMessage("[" + empName + "] 직원이 해고 처리 완료되었습니다.");
            view.clearInputForm();
            refreshEmployeeList(currentBranchId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

package controller.owner;

import java.sql.SQLException;
import java.util.List;
import model.Customer;
import model.PcCafe;
import view.owner.OwnerSeatMonitorView;
import service.CustomerService;
import service.PcCafeService;

public class SeatMonitorController {
    private final OwnerSeatMonitorView view;
    private final CustomerService customerService;
    private final PcCafeService pcCafeService;

    public SeatMonitorController(OwnerSeatMonitorView view, CustomerService customerService, PcCafeService pcCafeService) {
        this.view = view;
        this.customerService = customerService;
        this.pcCafeService = pcCafeService;
    }

    // 메인 컨트롤러에 의해 호출되어 해당 지점의 실시간 좌석 현황을 뷰에 렌더링
    public void refreshSeatMonitor(String branchId) {
        try {
            // 1. 지점 마스터 정보 조회 및 좌석 레이아웃 동적 생성
            PcCafe pcCafe = pcCafeService.getPcCafe(branchId);
            int totalSeats = 0;
            if (pcCafe != null) {
                totalSeats = pcCafe.getTotalSeats(); 
            }
            
            // 뷰의 좌석 배치도 초기화 및 버튼 생성
            view.renderSeatLayout(totalSeats);

            // 2. 현재 이용 중인 실시간 손님 리스트 조회 (Customer 테이블)
            List<Customer> currentCustomers = customerService.getCustomersInPcCafe(branchId);
            
            int usedCount = currentCustomers.size();
            int emptyCount = totalSeats - usedCount;

            // 상단 통계 수치 갱신
            view.updateSeatStats(totalSeats, usedCount, emptyCount);

            // 3. 사용 중인 좌석 번호 배열 추출 및 버튼 배경색 변경
            int[] usedSeatNumbers = currentCustomers.stream().mapToInt(Customer::getSeatNum).toArray();
            view.updateUsedSeats(usedSeatNumbers);

            // 4. 뷰 컬럼: {"좌석", "사용자명", "타입", "잔여 시간"}
            Object[][] tableData = new Object[usedCount][4];
            for (int i = 0; i < usedCount; i++) {
                Customer customer = currentCustomers.get(i);
                
                tableData[i][0] = customer.getSeatNum() + "번";
                
                // 회원/비회원 식별 가공
                if (customer.getMemberId() != null && !customer.getMemberId().trim().isEmpty()) {
                    tableData[i][1] = customer.getMemberId();
                    tableData[i][2] = "회원";
                } else {
                    tableData[i][1] = "비회원_" + customer.getSeatNum();
                    tableData[i][2] = "비회원";
                }
                
                tableData[i][3] = customer.getRemainTime() + "분";
            }

            // 뷰의 테이블 데이터 주입
            view.setUserSessionTableData(tableData);

        } catch (SQLException e) {
            System.err.println("[SeatMonitorController] 실시간 좌석 데이터 로드 중 예외 발생");
            e.printStackTrace();
        }
    }
}
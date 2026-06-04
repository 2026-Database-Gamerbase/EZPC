package controller;

import java.util.List;
import javax.swing.JOptionPane;
import model.*;
import service.*;
import view.owner.OwnerSystemSetupView;

public class SystemSetupController {
    private final OwnerSystemSetupView view;
    
    // 각 탭(지점, 음식, 요금제, 이벤트) 처리를 담당하는 서비스 객체들
    private final PcCafeService pcCafeService;
    private final FoodService foodService;
    private final TicketService ticketService;
    private final EventInfoService eventInfoService;

    public SystemSetupController(OwnerSystemSetupView view, PcCafeService pcCafeService, 
                                 FoodService foodService, TicketService ticketService, EventInfoService eventInfoService) {
        this.view = view;
        this.pcCafeService = pcCafeService;
        this.foodService = foodService;
        this.ticketService = ticketService;
        this.eventInfoService = eventInfoService;

        initEventBindings();
    }

    private void initEventBindings() {
        // 좌측 메뉴(카테고리) 클릭 시 화면 갱신
        view.setCategorySelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshCurrentCategory();
            }
        });

        // 표에서 항목 클릭하면 하단 입력창에 데이터 채워주기
        view.setTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableLineSelect();
            }
        });

        // 하단 추가/수정/삭제 버튼 이벤트 연결
        view.setSaveButtonListener(e -> handleFormSave());
        view.setDeleteButtonListener(e -> handleFormDelete());
        view.setClearButtonListener(e -> view.clearForm());
    }

    // 선택된 카테고리에 맞춰서 테이블 컬럼명과 입력 폼 구조를 변경하고 데이터 불러오기
    public void refreshCurrentCategory() {
        int index = view.getSelectedCategoryIndex();
        view.clearForm();

        try {
            switch (index) {
                case 0: // 지점 관리
                    view.setViewMode("지점(PC방) 관리", 
                        new String[]{"지점 코드", "지점명", "총 좌석수", "평균 평점"}, 
                        new String[]{"지점 코드", "지점명", "총 좌석수"});
                    loadPcCafeData();
                    break;
                case 1: // 공통 음식 메뉴 관리
                    view.setViewMode("공통 음식 메뉴 관리", 
                        new String[]{"음식 이름", "판매 가격"}, 
                        new String[]{"음식 이름", "판매 가격"});
                    loadFoodData();
                    break;
                case 2: // 공통 요금제 관리
                    view.setViewMode("공통 요금제 관리", 
                        new String[]{"이용 시간 (분)", "금액 (원)"}, 
                        new String[]{"이용 시간 (분)", "금액 (원)"});
                    loadTicketData();
                    break;
                case 3: // 이벤트 템플릿 관리
                    view.setViewMode("이벤트 템플릿 관리",
                        new String[]{"이벤트 타입", "내역 설명", "할인 종류", "결제 비율"},
                        new String[]{"이벤트 타입", "내역 설명", "타입 번호 (0 또는 1)", "결제 비율 (0.0~1.0)"});
                    loadEventInfoData();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // --- 카테고리별 데이터 로드용 메서드들 ---
    
    private void loadPcCafeData() throws Exception {
        List<PcCafe> list = pcCafeService.getAllPcCafes();
        Object[][] data = new Object[list.size()][4];
        for (int i = 0; i < list.size(); i++) {
            PcCafe c = list.get(i);
            data[i][0] = c.getPcId(); data[i][1] = c.getPcName(); data[i][2] = c.getTotalSeats(); data[i][3] = c.getAverageStarRating();
        }
        view.setTableData(data);
    }

    private void loadFoodData() {
        List<Food> list = foodService.getMenuBoard();
        Object[][] data = new Object[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            Food f = list.get(i); data[i][0] = f.getFoodName(); data[i][1] = f.getPrice();
        }
        view.setTableData(data);
    }

    private void loadTicketData() throws Exception {
        List<Ticket> list = ticketService.getAllTickets();
        Object[][] data = new Object[list.size()][2];
        for (int i = 0; i < list.size(); i++) {
            Ticket t = list.get(i); data[i][0] = t.getTicketTime(); data[i][1] = t.getPrice();
        }
        view.setTableData(data);
    }

    private void loadEventInfoData() throws Exception {
        List<EventInfo> list = eventInfoService.getAllEventInfos();
        Object[][] data = new Object[list.size()][4];
        for (int i = 0; i < list.size(); i++) {
            EventInfo ev = list.get(i); 
            data[i][0] = ev.getEventType();
            data[i][1] = ev.getEventContent();
            data[i][2] = ev.getEventTypeNum() == 0 ? "음식" : "이용권";
            data[i][3] = ev.getPaymentRate();
        }
        view.setTableData(data);
    }

    // 표에서 항목을 클릭했을 때 선택한 데이터를 하단 폼에 띄워줌
    private void handleTableLineSelect() {
        String selectedRowId = view.getSelectedRowId();
        if (selectedRowId == null) return;

        int index = view.getSelectedCategoryIndex();
        try {
            switch (index) {
                case 0: // 지점
                    for (PcCafe c : pcCafeService.getAllPcCafes()) {
                        if (c.getPcId().equals(selectedRowId)) {
                            view.fillFormInputs(new String[]{c.getPcId(), c.getPcName(), String.valueOf(c.getTotalSeats())});
                            break;
                        }
                    }
                    break;
                case 1: // 음식
                    for (Food f : foodService.getMenuBoard()) {
                        if (f.getFoodName().equals(selectedRowId)) {
                            view.fillFormInputs(new String[]{f.getFoodName(), String.valueOf(f.getPrice())});
                            break;
                        }
                    }
                    break;
                case 2: // 요금제
                    for (Ticket t : ticketService.getAllTickets()) {
                        if (String.valueOf(t.getTicketTime()).equals(selectedRowId)) {
                            view.fillFormInputs(new String[]{String.valueOf(t.getTicketTime()), String.valueOf(t.getPrice())});
                            break;
                        }
                    }
                    break;
                case 3: // 이벤트
                    for (EventInfo ev : eventInfoService.getAllEventInfos()) {
                        if (ev.getEventType().equals(selectedRowId)) {
                            view.fillFormInputs(new String[]{ev.getEventType(), ev.getEventContent(), String.valueOf(ev.getEventTypeNum()), String.valueOf(ev.getPaymentRate())});
                            break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 폼 입력값을 바탕으로 새 데이터를 추가(INSERT)할지 기존 데이터를 덮어쓸지(UPDATE) 결정
    private void handleFormSave() {
        int index = view.getSelectedCategoryIndex();
        String[] inputs = view.getFormInputs();
        
        // 표에서 항목이 선택된 상태라면 '수정', 아니면 '신규 추가'로 간주
        String selectedRowId = view.getSelectedRowId(); 
        boolean isUpdate = (selectedRowId != null);

        try {
            switch (index) {
                case 0: 
                    PcCafe cafe = new PcCafe(inputs[0], inputs[1], 0.0, 0, Integer.parseInt(inputs[2]));
                    if (isUpdate) {
                        pcCafeService.updatePcCafe(cafe); 
                    } else {
                        pcCafeService.insertPcCafe(cafe);
                    }
                    break;
                case 1: 
                    if (isUpdate) {
                        foodService.modifyFoodPrice(inputs[0], Integer.parseInt(inputs[1])); 
                    } else {
                        foodService.registerFood(inputs[0], Integer.parseInt(inputs[1]));
                    }
                    break;
                case 2: 
                    Ticket t = new Ticket(Integer.parseInt(inputs[0]), Integer.parseInt(inputs[1]));
                    if (isUpdate) {
                        ticketService.updateTicket(t); 
                    } else {
                        ticketService.addTicket(t);
                    }
                    break;
                case 3: 
                    EventInfo ev = new EventInfo(inputs[0], inputs[1], Integer.parseInt(inputs[2]), Double.parseDouble(inputs[3]));
                    if (isUpdate) {
                        eventInfoService.updateEventInfo(ev); 
                    } else {
                        eventInfoService.insertEventInfo(ev);
                    }
                    break;
            }
            view.setStatusMessage(isUpdate ? "데이터가 성공적으로 수정되었습니다." : "새 데이터가 추가되었습니다.");
            refreshCurrentCategory();
        } catch (Exception ex) {
            view.setStatusMessage("저장 실패: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // 선택한 항목 DB에서 삭제 처리
    private void handleFormDelete() {
        int index = view.getSelectedCategoryIndex();
        String targetId = view.getSelectedRowId();
        
        if (targetId == null) {
            JOptionPane.showMessageDialog(view, "삭제할 데이터 행을 테이블에서 먼저 선택하세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (index) {
                case 0: pcCafeService.deletePcCafe(targetId); break;
                case 1: foodService.removeFood(targetId); break;
                case 2: ticketService.removeTicket(Integer.parseInt(targetId)); break;
                case 3: eventInfoService.deleteEventInfo(targetId); break;
            }
            view.setStatusMessage("선택한 항목이 삭제되었습니다.");
            refreshCurrentCategory();
        } catch (Exception ex) {
            view.setStatusMessage("삭제 실패: " + ex.getMessage());
        }
    }
}
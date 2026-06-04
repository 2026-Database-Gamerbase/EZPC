package controller;

import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;
import model.Grade;
import model.PC_Member;
import service.PC_MemberService;
import service.GradeService;
import view.owner.OwnerMemberManageView;

public class MemberManageController {
    private final OwnerMemberManageView view;
    private final PC_MemberService memberService;
    private final GradeService gradeService;

    public MemberManageController(OwnerMemberManageView view, PC_MemberService memberService, GradeService gradeService) {
        this.view = view;
        this.memberService = memberService;
        this.gradeService = gradeService;
        
        initEventBindings();
    }

    private void initEventBindings() {
        // 체크박스 누르면 바로 리스트 갱신
        view.setDormantFilterListener(e -> refreshMemberAndGradeData());
        
        // 우측 등급 테이블 행 클릭 시 하단 수정 폼 자동 채우기
        view.setGradeTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleGradeTableSelect();
            }
        });
        
        // 등급 기준 저장 버튼 
        view.setSaveGradeButtonListener(e -> handleUpdateGradeStandard());
    }

    // 우측 등급표 클릭 시 하단 수정 폼에 값 세팅
    private void handleGradeTableSelect() {
        String standardStr = view.getSelectedGradeStandardString();
        String discountStr = view.getSelectedGradeDiscountString();

        if (standardStr != null && discountStr != null) {
            try {
                // 숫자만 추출
                int standard = Integer.parseInt(standardStr.replaceAll("[^0-9]", ""));
                int discount = Integer.parseInt(discountStr.replaceAll("[^0-9]", ""));

                view.setStandardAmount(standard);
                view.setDiscountRate(discount);
            } catch (NumberFormatException ignored) {}
        }
    }

    // 좌측 회원 목록이랑 우측 등급표 한 번에 새로고침 
    public void refreshMemberAndGradeData() {
        try {
            boolean filterDormant = view.isDormantFilterSelected();
            List<PC_Member> displayList;
            
            // 1. 휴면 체크표시 확인
            if (filterDormant) {
                displayList = memberService.getDormantMembers(); 
            } else {
                displayList = memberService.getAllUsers();
            }

            Object[][] memberData = new Object[displayList.size()][5];
            for (int i = 0; i < displayList.size(); i++) {
                PC_Member m = displayList.get(i);
                memberData[i][0] = m.getMemberId();
                memberData[i][1] = m.getMemberName();
                memberData[i][2] = (m.getGradeType() != null) ? m.getGradeType().toUpperCase() : "BRONZE";
                memberData[i][3] = String.format("%,d원", m.getTotalPaymentAmount());
                memberData[i][4] = m.getRemainTime() + "분"; 
            }
            view.setMemberTableData(memberData);

            // 2. 등급표 세팅
            List<Grade> gradeList = gradeService.getAllGrades();
            gradeList.sort(Comparator.comparingInt(Grade::getGradeStandard));

            Object[][] gradeData = new Object[gradeList.size()][4];
            for (int i = 0; i < gradeList.size(); i++) {
                Grade g = gradeList.get(i);
                int discountPercent = (int) (g.getBenefit() * 100);
                
                gradeData[i][0] = g.getGradeType().toUpperCase(); 
                gradeData[i][1] = String.format("%,d원", g.getGradeStandard());
                gradeData[i][2] = discountPercent + "%";
                gradeData[i][3] = (discountPercent == 0) ? "기본 등급" : discountPercent + "% 할인";
            }
            view.setGradeTableData(gradeData);

        } catch (Exception e) {
            System.err.println("[MemberManageController] 데이터 새로고침 실패");
            e.printStackTrace();
        }
    }

    // 선택한 등급 기준금액/할인율 업데이트
    private void handleUpdateGradeStandard() {
        String selectedGradeType = view.getSelectedGrade();
        if (selectedGradeType == null) {
            JOptionPane.showMessageDialog(view, "기준을 변경할 등급명을 우측 표에서 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int standardAmount = view.getStandardAmount();
            int discountPercent = view.getDiscountRate();
            double benefitRate = discountPercent / 100.0;

            Grade grade = new Grade();
            grade.setGradeType(selectedGradeType.toLowerCase());
            grade.setGradeStandard(standardAmount);
            grade.setBenefit(benefitRate);

            gradeService.updateGrade(grade);
            view.setStatusMessage("[" + selectedGradeType + "] 등급 마스터 승급 기준금액 및 혜택률 변경이 완료되었습니다.");
            refreshMemberAndGradeData(); 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
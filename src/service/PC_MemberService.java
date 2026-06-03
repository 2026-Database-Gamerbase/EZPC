package service;

import dao.GradeDAO;
import dao.PC_MemberDAO;
import java.util.List;
import model.Grade;
import model.PC_Member;

public class PC_MemberService {

    private PC_MemberDAO memberDao;

    // 의존성 주입
    public PC_MemberService(PC_MemberDAO memberDao) {
        this.memberDao = memberDao;
    }
    
    // 회원 가입
    public void signUp(String id, String pw, String name) {
        // 중복 검사
        if (memberDao.findByID(id) != null) {
            System.out.println("회원가입 실패: 이미 존재하는 아이디입니다.");
            return;
        }

        // 새 회원 객체 생성 (초기값 세팅)
        PC_Member newMember = new PC_Member();
        newMember.setMemberId(id);
        newMember.setMemberPassword(pw);
        newMember.setMemberName(name);
        newMember.setRemainTime(0);              // 초기 시간 0
        newMember.setGradeType("브론즈");        // 기본 등급
        newMember.setTotalPaymentAmount(0);      // 초기 결제 0
        newMember.setMemberType("user");         // 일반 사용자만 회원 가입 가능, 운영자로 회원 가입 불가능

        memberDao.insertMember(newMember);
        System.out.println("회원가입이 완료되었습니다! 환영합니다, " + name + "님.");
    }

    // 2. 로그인
    public PC_Member login(String memberId, String password) {
        PC_Member member = memberDao.findByID(memberId);
        
        if (member == null) {
            System.out.println("로그인 실패: 존재하지 않는 아이디입니다.");
            return null;
        }
        
        if (!member.getMemberPassword().equals(password)) {
            System.out.println("로그인 실패: 비밀번호가 일치하지 않습니다.");
            return null;
        }
        
        System.out.println(member.getMemberName() + "님 환영합니다!");
        return member; // 리턴된 객체의 getMemberType()으로 화면 분기 가능
    }

    // 3. 회원정보 수정
    public void updateInfo(PC_Member member) {
        memberDao.updateMember(member);
        System.out.println("회원 정보가 수정되었습니다.");
    }

    // 4. 회원 탈퇴
    public void withdraw(PC_Member member) {
        memberDao.deleteMember(member);
        System.out.println("회원 탈퇴가 완료되었습니다.");
    }
    
    // 5. 특정 회원 조회
    public PC_Member getMember(PC_Member member) {
    	return memberDao.findByID(member.getMemberId());
    }

    // 6. 전체 회원 조회 (owner 포함)
    public List<PC_Member> getAllMembers() {
        return memberDao.findAll();
    }
    
    // 7. user 타입 회원만 조회 (운영자 뷰 회원 관리용)
    public List<PC_Member> getAllUsers(){
    	return memberDao.findAllUsers();
    }
    // 8. 30일 이상 미방문 회원 조회
    public List<PC_Member> getDormantMembers() {
        System.out.println("[Service] 휴면 회원(30일 이상 미방문) 조회를 요청합니다.");
        List<PC_Member> dormantList = memberDao.findDormantMembers();
        
        if(dormantList.isEmpty()) {
            System.out.println("[Service] 현재 휴면 상태인 회원이 없습니다.");
        } else {
            System.out.println("[Service] 총 " + dormantList.size() + "명의 휴면 회원이 조회되었습니다.");
        }
        
        return dormantList;
    }
    
    // 7. 잔여시간 추가
//    public void chargeTime(String memberId, int addTime, int paymentAmount) {
//        //회원 테이블의 잔여 시간 및 결제 금액 누적
//        memberDao.updateRemainTime(memberId, addTime);
//        memberDao.addTotalPayment(memberId, paymentAmount);
//        
//        //회원의 총 결제 금액, 현재 등급 가져오기
//        PC_Member member = memberDao.findByID(memberId);
//        if (member != null) {
//            int totalPayment = member.getTotalPaymentAmount(); // 총 결제 금액
//            String currentGrade = member.getGradeType(); //현재 등급
//            
//            String newGrade = "bronze"; // 기본 등급 세팅 (총 결제 금액 20만 원 미만일 경우 기본값)
//            int maxStandard = 0;
//            
//            try {
//                //grade 테이블에 등록된 모든 등급 기준 목록을 가져옴
//                //브론즈(20만), 실버(30만), 골드(50만), 다이아(80만)
//                List<Grade> gradeList = gradeDao.findAll();
//                
//                for (Grade g : gradeList) {
//                    // 회원의 총 결제 금액이 등급 기준 금액 이상이면서, 
//                    // 지금까지 판별한 기준 금액보다 더 높은 등급 기준이라면 갱신
//                    if (totalPayment >= g.getGradeStandard() && g.getGradeStandard() >= maxStandard) {
//                        newGrade = g.getGradeType();
//                        maxStandard = g.getGradeStandard();
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println("등급 기준을 조회하는 중 오류가 발생했습니다. 기본 등급을 유지합니다.");
//                e.printStackTrace();
//            }
//            
//            //계산된 등급이 현재 회원의 등급과 다를 때만 db 업데이트 수행
//            if (!currentGrade.equalsIgnoreCase(newGrade)) {
//                memberDao.updateUserGrade(memberId, newGrade); // pc_member 테이블 변경
//                System.out.printf("\n[등급 변경] 축하합니다! %s님의 등급이 [%s] -> [%s]로 상승했습니다!\n", 
//                                  memberId, currentGrade, newGrade);
//            }
//        }
//    }
    

}
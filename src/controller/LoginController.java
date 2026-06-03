package controller;

import java.sql.Connection;
import java.sql.SQLException;

import dao.PC_MemberDAO;
import daoImpl.PC_MemberDAOImpl;
import db.DatabaseConnector;
import model.PC_Member;
import service.PC_MemberService;
import view.auth.LoginView;
import view.auth.SignUpView;

public class LoginController {

    private Connection authConn;
    private PC_MemberService memberService;
    private LoginView loginView;
    
    //생성자
    public LoginController() {
        try {
            authConn = DatabaseConnector.getAuthConnection();
            PC_MemberDAO memberDao = new PC_MemberDAOImpl(authConn);
            memberService = new PC_MemberService(memberDao);
        } catch (SQLException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            System.err.println("=================================================");
            System.err.println("[DB 연결 실패]");
            if (msg.contains("communications link failure") || msg.contains("connection refused")) {
                System.err.println("원인: DB 서버에 접속할 수 없습니다.");
                System.err.println("확인: DatabaseConnector의 URL(IP/포트)이 올바른지,");
                System.err.println("      MariaDB 서버가 실행 중인지 확인하세요.");
            } else if (msg.contains("access denied")) {
                System.err.println("원인: 계정 인증 실패 (아이디 또는 비밀번호 오류)");
                System.err.println("확인: DatabaseConnector의 AUTH_USER/AUTH_PASS를 확인하세요.");
            } else if (msg.contains("unknown database")) {
                System.err.println("원인: 'EZPC' 데이터베이스가 존재하지 않습니다.");
                System.err.println("확인: db 생성 쿼리.sql을 먼저 실행했는지 확인하세요.");
            } else {
                System.err.println("원인: " + e.getMessage());
            }
            System.err.println("=================================================");
        }
    }

    public void start() {
        loginView = new LoginView(); //로그인 뷰 생성
        loginView.setLoginButtonListener(e -> handleLogin()); //로그인 버튼에 리스너 추가
        loginView.setSignUpButtonListener(e -> handleSignUp()); //회원가입 버튼에 리스너 추가
        loginView.setVisible(true);
    }
    
    //로그인 처리
    private void handleLogin() {
    	//사용자에게 아이디와 비밀번호를 입력받음
        String id = loginView.getInputId();
        String pw = loginView.getInputPassword();
        
        //로그인 로직 메소드 호출 (pc_member 테이블에 일치하는 아이디/비밀번호가 있는지 확인)
        PC_Member member = memberService.login(id, pw);
        
        //일치하는 회원이 없는 경우
        if (member == null) {
            loginView.setStatusMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
            return;
        }
        
        //일치하는 회원이 있는 경우
        try {
            //로그인 전용 계정의 db 연결 해제, 사용자/운영자 db 계정으로 db 연결
            authConn.close();

            //역할별 연결 생성 후 해당 Controller로 이동
            //member의 type이 owner면 db에 모든 권한, user면 user에 해당하는 권한만 부여된 계정으로 db 연결
            Connection roleConn = DatabaseConnector.getConnection(member.getMemberType());

            loginView.dispose();
            
            //owner 회원일 경우 owner 컨트롤러에 owner 계정으로 연결된 db 연결자 전달
            if ("owner".equals(member.getMemberType())) {
                new OwnerController(roleConn, member).start();
            //user 회원일 경우 user 컨트롤러에 user 계정으로 연겶된 db 연결자 전달
            } else {
                new UserController(roleConn, member).start();
            }

        } catch (SQLException e) {
            loginView.setStatusMessage("서버 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
    
    
    //회원가입 처리
    private void handleSignUp() {
    	//회원가입 뷰 생성
        SignUpView signUpView = new SignUpView();

        signUpView.setSignUpButtonListener(e -> {
        	//사용자에게 아이디, 비밀번호, 이름 입력받음
            String id   = signUpView.getInputId();
            String pw   = signUpView.getInputPassword();
            String name = signUpView.getInputName();
            
            if (!pw.equals(signUpView.getConfirmPassword())) {
                signUpView.setStatusMessage("비밀번호가 일치하지 않습니다.");
                return;
            }
            	
            //서비스 단에서 회원가입 로직 처리
            memberService.signUp(id, pw, name);
            signUpView.dispose();
        });

        signUpView.setCancelButtonListener(e -> signUpView.dispose());
        signUpView.setVisible(true);
    }
}

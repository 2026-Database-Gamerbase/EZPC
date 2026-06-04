package controller;

import dao.CustomerDAO;
import dao.PC_MemberDAO;
import daoImpl.CustomerDAOImpl;
import daoImpl.PC_MemberDAOImpl;
import db.DatabaseConnector;
import java.sql.Connection;
import java.sql.SQLException;
import model.PC_Member;
import service.PC_MemberService;
import view.auth.LoginView;
import view.auth.SignUpView;

/**
 * LoginController - thin controller that handles LoginView and SignUpView
 * Uses the shared DatabaseConnector.getConnection() for simplicity.
 */
public class LoginController {

    private Connection authConn;
    private PC_MemberService memberService;
    private LoginView loginView;
    
    //생성자
    public LoginController() {
        try {
            authConn = DatabaseConnector.getAuthConnection();
            PC_MemberDAO memberDao = new PC_MemberDAOImpl(authConn);
            CustomerDAO customerDao = new CustomerDAOImpl(authConn);
            memberService = new PC_MemberService(memberDao, customerDao);
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

    // 로그인 처리
    private void handleLogin() {
        String id = loginView.getInputId();
        String pw = loginView.getInputPassword();

        PC_Member member;
        try {
            member = memberService.login(id, pw);
        } catch (RuntimeException e) {
            loginView.setStatusMessage(e.getMessage());
            return;
        }

        Connection roleConn = null;
        try {
            roleConn = DatabaseConnector.getConnection(member.getMemberType());
            if (roleConn == null || roleConn.isClosed()) {
                throw new SQLException("역할별 DB 연결을 생성할 수 없습니다.");
            }

            if (authConn != null && !authConn.isClosed()) {
                authConn.close();
            }

            System.out.println("[LoginController] roleConn open: " + !roleConn.isClosed());
            loginView.dispose();

            if ("owner".equalsIgnoreCase(member.getMemberType())) {
                new OwnerController(roleConn, member).start();
            } else {
                new UserController(roleConn, member).start();
            }

        } catch (SQLException e) {
            loginView.setStatusMessage("서버 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }

    // 회원가입 처리
    private void handleSignUp() {
        SignUpView signUpView = new SignUpView();

        signUpView.setSignUpButtonListener(e -> {
            String id = signUpView.getInputId();
            String pw = signUpView.getInputPassword();
            String name = signUpView.getInputName();

            if (!pw.equals(signUpView.getConfirmPassword())) {
                signUpView.setStatusMessage("비밀번호가 일치하지 않습니다.");
                return;
            }

            memberService.signUp(id, pw, name);
            signUpView.dispose();
        });

        signUpView.setCancelButtonListener(e -> signUpView.dispose());
        signUpView.setVisible(true);
    }
}

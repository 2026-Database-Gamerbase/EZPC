package controller;

import dao.PC_MemberDAO;
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

    public LoginController() {
        try {
            authConn = DatabaseConnector.getAuthConnection();
            PC_MemberDAO memberDao = new PC_MemberDAOImpl(authConn);
            memberService = new PC_MemberService(memberDao);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        loginView = new LoginView(); //로그인 뷰 생성
        loginView.setLoginButtonListener(e -> handleLogin());
        loginView.setSignUpButtonListener(e -> handleSignUp());
        loginView.setVisible(true);
    }

    // 로그인 처리
    private void handleLogin() {
        String id = loginView.getInputId();
        String pw = loginView.getInputPassword();

        PC_Member member = memberService.login(id, pw);

        if (member == null) {
            loginView.setStatusMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
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

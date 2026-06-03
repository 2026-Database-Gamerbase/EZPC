

import controller.LoginController;

public class Main {
    public static void main(String[] args) {
    	
            try {
            	//로그인 컨트롤러 연결 - db연결, 뷰 연결 처리 여기서 함
            	LoginController loginController = new LoginController();
            	loginController.start();

            } catch (java.sql.SQLException e) {
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
            } catch (Exception e) {
                System.err.println("=================================================");
                System.err.println("[예상치 못한 오류] " + e.getClass().getSimpleName());
                System.err.println("원인: " + e.getMessage());
                System.err.println("=================================================");
                e.printStackTrace();
            }
    }
}

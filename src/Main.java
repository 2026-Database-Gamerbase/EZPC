

import controller.LoginController;

public class Main {
    public static void main(String[] args) {
    	
            try {
            	//로그인 컨트롤러 연결 - db연결, 뷰 연결 처리 여기서 함
            	LoginController loginController = new LoginController();
            	loginController.start();

            } catch (Exception e) {
                System.err.println("=================================================");
                System.err.println("[예상치 못한 오류] " + e.getClass().getSimpleName());
                System.err.println("원인: " + e.getMessage());
                System.err.println("=================================================");
                e.printStackTrace();
            }
    }
}
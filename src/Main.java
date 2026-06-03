

import controller.LoginController;

public class Main {
    public static void main(String[] args) {
            try {
            	//로그인 컨트롤러 연결 - db연결, 뷰 연결 처리 여기서 함
                new LoginController().start();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("로그인 컨트롤러 생성 중 에러 발생");
            }
    }
}

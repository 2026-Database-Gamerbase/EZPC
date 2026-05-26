package db;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;
import view.auth.LoginView;
import view.FontUtil;

/**
 * Main - PC방 관리 시스템 메인 진입점
 * LoginView를 띄워서 프로그램을 시작합니다.
 */
public class Main {

	public static void main(String[] args) {
		// 시스템 기본 인코딩 설정
		System.setProperty("file.encoding", "UTF-8");
		
		// Swing 이벤트 디스패처 스레드에서 UI 생성
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// 한글 폰트 설정
				FontUtil.setDefaultFont();
				
				System.out.println("=== PC방 관리 시스템 시작 ===");
				System.out.println("로그인 화면이 표시됩니다...\n");
				
				// LoginView 생성 및 표시
				LoginView loginView = new LoginView();
				loginView.setVisible(true);
				
				System.out.println("✓ LoginView 표시 완료");
				System.out.println("  - 테스트 ID: owner");
				System.out.println("  - 테스트 ID: user001");
				System.out.println("\n※ DB 연결 없으므로 실제 인증은 구현되지 않습니다.");
			}
		});
	}

}

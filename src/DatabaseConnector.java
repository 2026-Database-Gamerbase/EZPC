import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
	private static final String URL ="jdbc:mysql://본인IP";
	private static final String USER = "아이디";
	private static final String PASS ="비밀번호";
	
	//기본 생성자
	public DatabaseConnector() {
	}
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASS);	}
}

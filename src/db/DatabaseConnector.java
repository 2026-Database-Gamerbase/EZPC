package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    private static final String URL = "jdbc:mysql://localhost/EZPC";
    
    //루트 계정
    private static final String ROOT_USER = "root";
    private static final String ROOT_Password = "";

    //로그인 인증 전용 계정 (pc_member SELECT, INSERT만 허용)
    private static final String AUTH_USER = "ezpc_auth";
    private static final String AUTH_PASS = "auth1234";

    //일반 사용자 계정
    private static final String USER_USER = "ezpc_user";
    private static final String USER_PASS = "user1234";

    //운영자 계정
    private static final String OWNER_USER = "ezpc_owner";
    private static final String OWNER_PASS = "owner1234";
    
    //루트 전용 연결
    public static Connection getRootConnection() throws SQLException{
    	return DriverManager.getConnection(URL, ROOT_USER, ROOT_Password);
    }

    //로그인 화면에서 사용 — 인증 전용
    public static Connection getAuthConnection() throws SQLException {
        return DriverManager.getConnection(URL, AUTH_USER, AUTH_PASS);
    }

    //로그인 성공 후 memberType에 따라 역할별 연결 반환
    //memberType: "owner" → 운영자 계정 / 그 외 → 일반 사용자 계정
    public static Connection getConnection(String memberType) throws SQLException {
        if ("owner".equalsIgnoreCase(memberType)) {
            return DriverManager.getConnection(URL, OWNER_USER, OWNER_PASS);
        }
        return DriverManager.getConnection(URL, USER_USER, USER_PASS);
    }
}
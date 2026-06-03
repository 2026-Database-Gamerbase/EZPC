package controller;

import java.sql.Connection;
import model.PC_Member;

/** Minimal OwnerController stub so owner logins don't crash the app.
 *  This can be expanded later with owner-specific views and services.
 */
public class OwnerController {
    private final Connection conn;
    private final PC_Member member;

    public OwnerController(Connection conn, PC_Member member) {
        this.conn = conn;
        this.member = member;
    }

    public void start() {
        System.out.println("Owner area is not implemented yet. Member: " + member.getMemberId());
        try { if (conn != null && !conn.isClosed()) conn.close(); } catch (Exception ignored) {}
    }
}

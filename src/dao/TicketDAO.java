package dao;

import java.sql.SQLException;
import java.util.List;
import model.Ticket;

public interface TicketDAO {
    void insert(Ticket ticket) throws SQLException;

    Ticket findByTime(int ticketTime) throws SQLException;

    List<Ticket> findAll() throws SQLException;

    void update(Ticket ticket) throws SQLException;

    void deleteByTime(int ticketTime) throws SQLException;
}

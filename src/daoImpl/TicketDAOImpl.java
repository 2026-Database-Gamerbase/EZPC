package daoImpl;

import model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.TicketDAO;

public class TicketDAOImpl implements TicketDAO {

    private final Connection conn;

    public TicketDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void insert(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO ticket (ticket_time, price) VALUES (?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, ticket.getTicketTime());
            statement.setInt(2, ticket.getPrice());
            statement.executeUpdate();
        }
    }

    @Override
    public Ticket findByTime(int ticketTime) throws SQLException {
        String sql = "SELECT * FROM ticket WHERE ticket_time = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, ticketTime);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapToTicket(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Ticket> findAll() throws SQLException {
        String sql = "SELECT * FROM ticket";
        List<Ticket> tickets = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                tickets.add(mapToTicket(resultSet));
            }
        }

        return tickets;
    }

    @Override
    public void update(Ticket ticket) throws SQLException {
        String sql = "UPDATE ticket SET price = ? WHERE ticket_time = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, ticket.getPrice());
            statement.setInt(2, ticket.getTicketTime());
            statement.executeUpdate();
        }
    }

    @Override
    public void deleteByTime(int ticketTime) throws SQLException {
        String sql = "DELETE FROM ticket WHERE ticket_time = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, ticketTime);
            statement.executeUpdate();
        }
    }

    private Ticket mapToTicket(ResultSet resultSet) throws SQLException {
        return new Ticket(
                resultSet.getInt("ticket_time"),
                resultSet.getInt("price")
        );
    }
}

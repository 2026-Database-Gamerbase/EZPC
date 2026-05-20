package service;

import java.sql.SQLException;
import java.util.List;

import dao.TicketDAO;
import dao.TicketDAOImpl;
import model.Ticket;

public class TicketService {
    private final TicketDAO ticketDAO;

    public TicketService() {
        this(new TicketDAOImpl());
    }

    public TicketService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public void addTicket(Ticket ticket) throws SQLException {
        validateTicket(ticket);
        ticketDAO.insert(ticket);
    }

    public Ticket getTicket(int ticketTime) throws SQLException {
        return ticketDAO.findByTime(ticketTime);
    }

    public List<Ticket> getAllTickets() throws SQLException {
        return ticketDAO.findAll();
    }

    public void updateTicket(Ticket ticket) throws SQLException {
        validateTicket(ticket);
        ticketDAO.update(ticket);
    }

    public void removeTicket(int ticketTime) throws SQLException {
        ticketDAO.deleteByTime(ticketTime);
    }

    private void validateTicket(Ticket ticket) {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket information is required.");
        }
        if (ticket.getTicketTime() <= 0) {
            throw new IllegalArgumentException("Ticket time must be a positive integer.");
        }
        if (ticket.getPrice() < 0) {
            throw new IllegalArgumentException("Ticket price cannot be negative.");
        }
    }
}

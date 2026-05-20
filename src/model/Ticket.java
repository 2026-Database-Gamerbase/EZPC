package model;

public class Ticket {
    private int ticketTime;
    private int price;

    public Ticket() {
    }

    public Ticket(int ticketTime, int price) {
        this.ticketTime = ticketTime;
        this.price = price;
    }

    public int getTicketTime() {
        return ticketTime;
    }

    public void setTicketTime(int ticketTime) {
        this.ticketTime = ticketTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketTime=" + ticketTime +
                ", price=" + price +
                '}';
    }
}

package com.example.backend;

import com.example.backend.Service.TicketService;
import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketPool {
    private final List<Integer> tickets = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private final int maxTicketCapacity;

    private WebSocketSession session;

    private TicketService ticketService;

    private Configuration configuration;

    public TicketPool(Configuration config, WebSocketSession session, TicketService ticketService) {
        this.configuration = config;
        this.maxTicketCapacity = config.getMaxTicketCapacity();
        this.session = session;
        this.ticketService = ticketService;
    }

    private Log logger = new Log();

    public synchronized void addTickets(Vendor vendor) {
        int ticketID = Vendor.getLastTicketId();
        try {
            if(tickets.size() < maxTicketCapacity) {
                tickets.add(++ticketID);
                Vendor.setLastTicketId(ticketID);
                logger.writeLog("Vendor " + vendor.getVendorId() + " added new ticket with ID " + ticketID, "INFO");
                Vendor.setCurrentVendorID((Vendor.getCurrentVendorID() % Vendor.getTotalVendors()) + 1);
                Vendor.getCondition().signalAll();
                sendMessageToClient(session, "+ Vendor " + vendor.getVendorId() + " added new ticket with ID " + ticketID);
            } else {
                logger.writeLog("Vendors cannot add Tickets cuz total tickets reached", "WARNING");
                if(Vendor.getLastTicketId() == configuration.getTotalTickets()) {
                    Vendor.setStopAll(true);
                    return;
                } else {
                    wait();
                }
            }
        } catch(Exception e) {
            System.out.println("An Error Occurred");
        }
        notifyAll();
    }

    public synchronized void buyTicket(Customer customer) {
        int ticket = Customer.getTicketsBought();
        try {
            if(tickets.isEmpty()) {
                logger.writeLog("Customer cannot buy cuz ticket pool is empty", "WARNING");
                if(Customer.getTicketsBought() == configuration.getTotalTickets()) {
                    Customer.setStopAll(true);
                    return;
                } else {
                    wait();
                }
            } else {
                tickets.removeLast();
                Customer.setTicketsBought(++ticket);
                logger.writeLog("Customer " + customer.getCustomerId() + " Bought Ticket with ID " + ticket, "INFO");
                Customer.setCurrentCustomerID((Customer.getCurrentCustomerID() % Customer.getTotalCustomers()) + 1);
                Customer.getCondition().signalAll();
                sendMessageToClient(session, "- Customer " + customer.getCustomerId() + " bought Ticket with ID " + ticket);
            }
        } catch(Exception e) {
            System.out.println("An Error Occurred");
        }
        notifyAll();
    }

    // Method to send a message to the client
    public void sendMessageToClient(WebSocketSession session, String message) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            } else {
                System.out.println("Session is closed or null. Message not sent: " + message);
                ticketService.stopAll();
                logger.writeLog("All Operation Closed", "INFO");
            }
        } catch (Exception e) {
            ticketService.stopAll();
            logger.writeLog("All Operation Closed", "INFO");
        }
    }
}

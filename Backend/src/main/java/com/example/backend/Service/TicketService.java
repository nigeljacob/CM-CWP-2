package com.example.backend.Service;

import com.example.backend.*;
import com.example.backend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {


    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    private Configuration configuration;
    private WebSocketSession session;
    private TicketPool ticketPool;

    private final List<Thread> threads = new ArrayList<>();

    public void startTrading() {

        List<Configuration> configurations = getAllConfiguration();

        if(configurations.size() > 0) {
            this.configuration = getLastConfiuration(configurations);
        } else {
            sendMessageToClient(session, "No Configuration Found.. Create One to Continue");
            this.stopAll();
            try{
                session.close();
            } catch (Exception e) {}
            return;
        }

        this.ticketPool = new TicketPool(configuration, session, this);

        Vendor.setCurrentVendorID(1);
        Vendor.setLastTicketId(0);
        Customer.setCurrentCustomerID(1);
        Customer.setTicketsBought(0);
        Vendor.setTotalVendors(0);
        Customer.setTotalCustomers(0);

        this.stopAll();

        Vendor vendor1 = new Vendor(1, configuration.getTicketReleaseRate(), ticketPool, configuration.getTotalTickets());
        Vendor vendor2 = new Vendor(2, configuration.getTicketReleaseRate(), ticketPool, configuration.getTotalTickets());

        Customer customer1 = new Customer(1, configuration.getCustomerRetrievalRate(), ticketPool, configuration.getTotalTickets());
        Customer customer2 = new Customer(2, configuration.getCustomerRetrievalRate(), ticketPool, configuration.getTotalTickets());

        Thread vendorThread1 = new Thread(vendor1);
        Thread vendorThread2 = new Thread(vendor2);
        Thread customerThread1 = new Thread(customer1);
        Thread customerThread2 = new Thread(customer2);

        threads.add(vendorThread1);
        threads.add(customerThread1);
        threads.add(customerThread2);
        threads.add(vendorThread2);

        vendorThread1.start();
        vendorThread2.start();
        customerThread1.start();
        customerThread2.start();

        try {
            vendorThread1.join();
            vendorThread2.join();
            customerThread1.join();
            customerThread2.join();
        } catch (InterruptedException e) {
            this.stopAll();
            e.printStackTrace();
        }

        sendMessageToClient(session, "Trading complete");

    }

    public void stopAll() {
        Vendor.setStopAll(true);
        Customer.setStopAll(true);


        synchronized (TicketPool.class) {
            TicketPool.class.notifyAll();
        }

        for(int i = 0; i < threads.size(); i++) {
            threads.get(i).interrupt();
        }

        Vendor.setStopAll(false);
        Customer.setStopAll(false);
    }

    public List<Configuration> getAllConfiguration() {
        return ticketRepository.findAll();
    }

    public Configuration getLastConfiuration(List<Configuration> all) {
        return all.get(all.size() - 1);
    }

    public Configuration writeConfiguration(Configuration configuration) {
        return ticketRepository.save(configuration);
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public void sendMessageToClient(WebSocketSession session, String message) {
        try {
            if (session != null && session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            } else {
                System.out.println("Session is closed or null. Message not sent: " + message);
            }
        } catch (Exception e) {
            System.err.println("Error while sending message to client: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

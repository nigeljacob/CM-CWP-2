package com.example.backend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Customer implements Runnable {
    @Getter
    private final int customerId;
    private final int retrievalInterval;
    private final TicketPool ticketPool;
    private final int totalTickets;

    private static Lock lock = new ReentrantLock();
    @Getter
    private static final Condition condition = lock.newCondition();

    private Log logger = new Log();

    @Getter @Setter
    private static int ticketsBought = 0;
    @Getter @Setter
    private static int currentCustomerID = 1;
    @Getter @Setter
    private static int totalCustomers = 0;

    @Getter @Setter
    private static volatile boolean stopAll = false;

    public Customer(int customerId, int retrievalInterval, TicketPool ticketPool, int totalTickets) {
        this.customerId = customerId;
        this.retrievalInterval = retrievalInterval;
        this.ticketPool = ticketPool;
        this.totalTickets = totalTickets;
        totalCustomers += 1;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            while(ticketsBought < totalTickets) {

                if (stopAll) {
                    Thread.currentThread().interrupt();
                    break;
                }

                while(customerId != currentCustomerID) {
                    if (stopAll) {
                        logger.writeLog("Customer " + this.customerId + " stopping while waiting", "INFO");
                        return;
                    }
                    condition.await();
                }

                Thread.sleep(this.retrievalInterval);
                ticketPool.buyTicket(this);
            }
            logger.writeLog("Customer " + this.customerId + " terminated", "INFO");
        } catch (InterruptedException e) {
            System.err.println("Customer " + this.customerId + " interrupted.");
            logger.writeLog("Customer " + this.customerId + " interrupted", "ERROR");
        } finally {
            lock.unlock();
        }
    }
}

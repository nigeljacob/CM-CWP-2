package com.example.backend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Vendor implements Runnable {
    @Getter
    private final int vendorId;
    private final int ticketsPerRelease;
    private final TicketPool ticketPool;
    private final int totalTickets;

    private static Lock lock = new ReentrantLock();
    @Getter
    private static final Condition condition = lock.newCondition();

    private Log logger = new Log();

    @Getter @Setter
    private static int lastTicketId = 0;
    @Getter @Setter
    private static int currentVendorID = 1;
    @Getter @Setter
    private static int totalVendors = 0;

    @Getter @Setter
    private static volatile boolean stopAll = false;

    public Vendor(int vendorId, int ticketsPerRelease, TicketPool ticketPool, int totalTickets) {
        this.vendorId = vendorId;
        this.ticketsPerRelease = ticketsPerRelease;
        this.ticketPool = ticketPool;
        this.totalTickets = totalTickets;
        totalVendors += 1;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            while (lastTicketId < totalTickets) {

                if (stopAll) {
                    Thread.currentThread().interrupt();
                    break;
                }

                while (vendorId != currentVendorID) {
                    if (stopAll) {
                        logger.writeLog("Vendor " + this.vendorId + " stopping while waiting", "INFO");
                        return;
                    }
                    condition.await();
                }

                if (Thread.interrupted()) {
                    logger.writeLog("Vendor " + this.vendorId + " interrupted", "INFO");
                    return;
                }

                ticketPool.addTickets(this);
                Thread.sleep(this.ticketsPerRelease);
            }

            logger.writeLog("Vendor " + this.vendorId + " terminated", "INFO");
        } catch (InterruptedException e) {
            System.err.println("Vendor " + this.vendorId + " interrupted.");
            logger.writeLog("Vendor " + this.vendorId + " interrupted", "ERROR");
        } finally {
            lock.unlock();
        }
    }

}

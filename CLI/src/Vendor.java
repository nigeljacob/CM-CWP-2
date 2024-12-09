import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Vendor implements Runnable {

    private final int vendorId;
    private final int ticketsPerRelease;
    private final TicketPool ticketPool;
    private final int totalTickets;

    private static Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    private Log logger = new Log();

    private static volatile boolean stopAll = false;

    private static int lastTicketId = 0;
    private static int currentVendorID = 1;
    private static int totalVendors = 0;

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

    public int getVendorId() {
        return vendorId;
    }

    public static void setLock(Lock lock) {
        Vendor.lock = lock;
    }

    public static int getLastTicketId() {
        return lastTicketId;
    }

    public static void setLastTicketId(int lastTicketId) {
        Vendor.lastTicketId = lastTicketId;
    }

    public static int getCurrentVendorID() {
        return currentVendorID;
    }

    public static void setCurrentVendorID(int currentVendorID) {
        Vendor.currentVendorID = currentVendorID;
    }

    public static int getTotalVendors() {
        return totalVendors;
    }

    public static Condition getCondition() {
        return condition;
    }

    public static void setStopAll(boolean stopAll) {
        Vendor.stopAll = stopAll;
    }
}
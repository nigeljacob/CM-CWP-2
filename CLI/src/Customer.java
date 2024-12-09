import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Customer implements Runnable {
    private final int customerId;
    private final int retrievalInterval;
    private final TicketPool ticketPool;
    private final int totalTickets;

    private static Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    private Log logger = new Log();

    private static volatile boolean stopAll = false;

    private static int ticketsBought = 0;
    private static int currentCustomerID = 1;
    private static int totalCustomers = 0;

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

    public int getCustomerId() {
        return customerId;
    }

    public static int getTicketsBought() {
        return ticketsBought;
    }

    public static void setTicketsBought(int ticketsBought) {
        Customer.ticketsBought = ticketsBought;
    }

    public static int getCurrentCustomerID() {
        return currentCustomerID;
    }

    public static void setCurrentCustomerID(int currentCustomerID) {
        Customer.currentCustomerID = currentCustomerID;
    }

    public static int getTotalCustomers() {
        return totalCustomers;
    }

    public static Condition getCondition() {
        return condition;
    }

    public static void setStopAll(boolean stopAll) {
        Customer.stopAll = stopAll;
    }
}

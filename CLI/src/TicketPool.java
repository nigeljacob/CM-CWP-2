import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketPool {
    private final List<Integer> tickets = Collections.synchronizedList(new ArrayList<>());

    private final int maxTicketCapacity;

    private Configuration configuration;

    public TicketPool(Configuration config) {
        this.configuration = config;
        this.maxTicketCapacity = config.getMaxTicketCapacity();
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
            }
        } catch(Exception e) {
            System.out.println("An Error Occurred");
        }
        notifyAll();
    }
}

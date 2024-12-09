import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Configuration implements Serializable {

    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    private static final String CONFIG_FILE = "configuration.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Configuration() {
        this.totalTickets = 8;
        this.ticketReleaseRate = 5000;
        this.customerRetrievalRate = 8000;
        this.maxTicketCapacity = 4;
    }

    public Configuration(int totalTickets, int ticketReleaseRate, int customerRetrievalRate, int maxTicketCapacity) {
        this.totalTickets = totalTickets;
        this.ticketReleaseRate = ticketReleaseRate;
        this.customerRetrievalRate = customerRetrievalRate;
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public Boolean loadConfiguration() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            System.out.println("Configuration file not found!");
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            Configuration loadedConfig = gson.fromJson(reader, Configuration.class);

            if (loadedConfig != null) {
                this.totalTickets = loadedConfig.totalTickets;
                this.ticketReleaseRate = loadedConfig.ticketReleaseRate;
                this.customerRetrievalRate = loadedConfig.customerRetrievalRate;
                this.maxTicketCapacity = loadedConfig.maxTicketCapacity;

                System.out.println("Configuration loaded successfully!");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveConfiguration() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(this, writer);
            System.out.println("Configuration saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }
}

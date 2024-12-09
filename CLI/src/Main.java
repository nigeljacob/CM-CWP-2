import java.util.Scanner;

public class Main {

    // Configuration parameters
    private static int totalTickets;
    private static int ticketReleaseInterval;
    private static int customerRetrievalInterval;
    private static int maxTicketCapacity;

    public static void main(String[] args) {
        System.out.println("Welcome to the Ticket Management System!");

        Configuration configuration = new Configuration();

        System.out.println("\nLoading Configuration.....\n");

        Boolean isAvailable = configuration.loadConfiguration();

        if(!isAvailable) {
            getConfigurationAndStartTrading();
        } else {
            System.out.println("\nSystem is starting with previous configuration.....");

            startTrading(configuration);

            System.out.println("\nTrading Complete... System is shutting down...");
        }
    }

    private static void getConfigurationAndStartTrading() {
        Scanner scanner = new Scanner(System.in);

        // Input and validate total tickets
        totalTickets = promptForInt(scanner, "Enter the total number of tickets to be managed: ",
                "Invalid input. Please enter a positive integer.", x -> x > 0);

        // Input and validate ticket release interval
        ticketReleaseInterval = promptForInt(scanner, "Enter the ticket release interval (in milliseconds): ",
                "Invalid input. Please enter a positive integer greater than zero.", x -> x > 0);

        // Input and validate customer retrieval interval
        customerRetrievalInterval = promptForInt(scanner, "Enter the customer retrieval interval (in milliseconds): ",
                "Invalid input. Please enter a positive integer greater than zero.", x -> x > 0);

        // Input and validate maximum ticket capacity
        maxTicketCapacity = promptForInt(scanner, "Enter the maximum ticket pool capacity: ",
                "Invalid input. Please enter a positive integer greater than zero.", x -> x > 0);

        System.out.println("\nConfiguration complete. System parameters:");
        System.out.println("Total Tickets: " + totalTickets);
        System.out.println("Ticket Release Interval: " + ticketReleaseInterval + " ms");
        System.out.println("Customer Retrieval Interval: " + customerRetrievalInterval + " ms");
        System.out.println("Max Ticket Capacity: " + maxTicketCapacity);

        Configuration configuration = new Configuration(
                totalTickets,
                ticketReleaseInterval,
                customerRetrievalInterval,
                maxTicketCapacity
        );

        configuration.saveConfiguration();

        // Placeholder for starting and stopping the ticket handling operations
        System.out.println("\nType 'start' to begin the system, or 'exit' to quit.");
        String command;
        while (true) {
            System.out.print("> ");
            command = scanner.nextLine().trim().toLowerCase();
            if (command.equals("start")) {
                System.out.println("System starting...");
                startTrading(configuration);
                break; // Exiting loop as the system starts
            } else if (command.equals("exit")) {
                System.out.println("Exiting the system. Goodbye!");
                System.exit(0);
            } else {
                System.out.println("Invalid command. Please type 'start' or 'exit'.");
            }
        }

        System.out.println("\nTrading Complete... System is shutting down...");

        scanner.close();
    }

    // Helper method to prompt and validate integer input
    private static int promptForInt(Scanner scanner, String prompt, String errorMessage, ValidationRule rule) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                if (rule.isValid(input)) {
                    return input;
                } else {
                    System.out.println(errorMessage);
                }
            } catch (NumberFormatException e) {
                System.out.println(errorMessage);
            }
        }
    }

    // Functional interface for input validation
    @FunctionalInterface
    interface ValidationRule {
        boolean isValid(int value);
    }

    private static void startTrading(Configuration configuration) {
        TicketPool ticketPool = new TicketPool(configuration);

        Vendor vendor1 = new Vendor(1, configuration.getTicketReleaseRate(), ticketPool, configuration.getTotalTickets());
        Vendor vendor2 = new Vendor(2, configuration.getTicketReleaseRate(), ticketPool, configuration.getTotalTickets());

        Customer customer1 = new Customer(1, configuration.getCustomerRetrievalRate(), ticketPool, configuration.getTotalTickets());
        Customer customer2 = new Customer(2, configuration.getCustomerRetrievalRate(), ticketPool, configuration.getTotalTickets());

        Thread vendor1Thread = new Thread(vendor1);
        Thread vendor2Thread = new Thread(vendor2);
        Thread customer1Thread = new Thread(customer1);
        Thread customer2Thread = new Thread(customer2);

        vendor1Thread.start();
        vendor2Thread.start();
        customer1Thread.start();
        customer2Thread.start();

        try {
            vendor1Thread.join();
            vendor2Thread.join();
            customer1Thread.join();
            customer2Thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
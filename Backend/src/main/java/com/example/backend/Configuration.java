package com.example.backend;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
public class Configuration implements Serializable  {

    // Define the id as an instance field, not static
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // No need to make it static

    @Getter
    @Setter
    private int totalTickets;

    @Getter
    @Setter
    private int ticketReleaseRate;

    @Getter
    @Setter
    private int customerRetrievalRate;

    @Getter
    @Setter
    private int maxTicketCapacity;

    // Default constructor
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
}

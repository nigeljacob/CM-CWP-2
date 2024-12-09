package com.example.backend.repository;

import com.example.backend.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Configuration, Long> {
    // Method to save the configuration data
    Configuration save(Configuration configuration);

    List<Configuration> findAll();
}

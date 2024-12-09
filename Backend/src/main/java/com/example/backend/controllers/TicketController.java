package com.example.backend.controllers;

import com.example.backend.Configuration;
import com.example.backend.Log;
import com.example.backend.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST}, allowedHeaders = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    private Log logger = new Log();

    @CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST}, allowedHeaders = "*")
    @GetMapping("/getConfiguration")
    public ResponseEntity<Configuration> getConfiguration() {
        List<Configuration> configurations = ticketService.getAllConfiguration();
        if(configurations.size() > 0) {
            Configuration configuration = ticketService.getLastConfiuration(configurations);
            return ResponseEntity.ok(configuration);
        }
        return ResponseEntity.notFound().build();
    }

    @CrossOrigin(origins = "http://localhost:4200", methods = {RequestMethod.GET, RequestMethod.POST}, allowedHeaders = "*")
    @PostMapping("/")
    public String saveConfiguration(@RequestBody Configuration configuration) {
        System.out.println(configuration);
        ticketService.writeConfiguration(configuration);
        logger.writeLog("Configuration saved successfully", "INFO");
        return "Configuration Saved Successfully";
    }

    @GetMapping("/")
    public String stopTrading() {
        ticketService.stopAll();
        return "Trading Stopped Successfully";
    }

}

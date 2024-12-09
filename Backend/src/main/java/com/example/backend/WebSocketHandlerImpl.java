package com.example.backend;

import com.example.backend.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

@Component
public class WebSocketHandlerImpl extends TextWebSocketHandler {

    @Autowired
    private TicketService ticketService;

    private Log logger = new Log();

    // Store WebSocket sessions (you can improve with proper session management)
    private final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.writeLog("New session established:" + session.getId(), "INFO");
        ticketService.setSession(session);
        ticketService.startTrading();
        logger.writeLog("Trading Complete", "INFO");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.writeLog("Received message: " + message.getPayload(), "INFO");
        if(message.getPayload().equals("stop")) {
            ticketService.stopAll();
            Vendor.setCurrentVendorID(1);
            Vendor.setLastTicketId(0);
            Customer.setCurrentCustomerID(1);
            Customer.setTicketsBought(0);
            Vendor.setTotalVendors(0);
            Customer.setTotalCustomers(0);
            logger.writeLog("All Operations Stopped", "INFO");
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        logger.writeLog("Received message: " + message.getPayload(), "INFO");
        if(message.getPayload().equals("stop")) {
            ticketService.stopAll();
            Vendor.setCurrentVendorID(1);
            Vendor.setLastTicketId(0);
            Customer.setCurrentCustomerID(1);
            Customer.setTicketsBought(0);
            Vendor.setTotalVendors(0);
            Customer.setTotalCustomers(0);
            logger.writeLog("All Operations Stopped", "INFO");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.writeLog("Connection closed: " + session.getId(), "INFO");
        sessions.remove(session);
    }
}
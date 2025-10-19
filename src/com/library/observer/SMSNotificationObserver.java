package com.library.observer;

import com.library.util.Logger;

/**
 * Concrete Observer for SMS notifications.
 */
public class SMSNotificationObserver implements Observer {
    private final String phoneNumber;
    private final Logger logger;
    
    public SMSNotificationObserver(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.logger = Logger.getInstance();
    }
    
    @Override
    public void update(String message) {
        sendSMS(message);
    }
    
    private void sendSMS(String message) {
        logger.info("SMS sent to " + phoneNumber + ": " + message);
        System.out.println("📱 SMS sent to " + phoneNumber + ": " + message);
    }
}

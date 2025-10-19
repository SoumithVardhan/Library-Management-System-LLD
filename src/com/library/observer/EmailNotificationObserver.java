package com.library.observer;

import com.library.util.Logger;

/**
 * Concrete Observer for Email notifications.
 */
public class EmailNotificationObserver implements Observer {
    private final String email;
    private final Logger logger;
    
    public EmailNotificationObserver(String email) {
        this.email = email;
        this.logger = Logger.getInstance();
    }
    
    @Override
    public void update(String message) {
        sendEmail(message);
    }
    
    private void sendEmail(String message) {
        logger.info("EMAIL sent to " + email + ": " + message);
        System.out.println("📧 Email sent to " + email + ": " + message);
    }
}

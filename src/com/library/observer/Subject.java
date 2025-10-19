package com.library.observer;

/**
 * Subject interface for notification system (Observer Pattern).
 */
public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers(String message);
}

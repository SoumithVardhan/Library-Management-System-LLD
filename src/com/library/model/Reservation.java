package com.library.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a book reservation.
 */
public class Reservation {
    private final String reservationId;
    private final String patronId;
    private final String isbn;
    private final LocalDateTime reservationDate;
    private ReservationStatus status;
    private LocalDateTime notificationSentDate;
    
    public Reservation(String reservationId, String patronId, String isbn) {
        this.reservationId = reservationId;
        this.patronId = patronId;
        this.isbn = isbn;
        this.reservationDate = LocalDateTime.now();
        this.status = ReservationStatus.ACTIVE;
    }
    
    public String getReservationId() {
        return reservationId;
    }
    
    public String getPatronId() {
        return patronId;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public LocalDateTime getReservationDate() {
        return reservationDate;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getNotificationSentDate() {
        return notificationSentDate;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public void setNotificationSentDate(LocalDateTime notificationSentDate) {
        this.notificationSentDate = notificationSentDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", patronId='" + patronId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", reservationDate=" + reservationDate +
                ", status=" + status +
                '}';
    }
}

package exceptions;

// Custom exception for invalid booking attempts
public class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

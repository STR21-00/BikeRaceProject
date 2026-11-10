package model;

// Thrown by a RegistrationStrategy when a racer isn't allowed to register for a race. 
public class IneligibleException extends RuntimeException {
    public IneligibleException(String message) {
        super(message);
    }
}

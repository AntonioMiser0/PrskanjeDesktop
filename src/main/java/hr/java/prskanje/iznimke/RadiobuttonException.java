package hr.java.prskanje.iznimke;

public class RadiobuttonException extends RuntimeException{
    public RadiobuttonException() {
    }

    public RadiobuttonException(String message) {
        super(message);
    }

    public RadiobuttonException(String message, Throwable cause) {
        super(message, cause);
    }
}

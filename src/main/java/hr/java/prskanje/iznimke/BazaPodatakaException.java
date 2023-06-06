package hr.java.prskanje.iznimke;

public class BazaPodatakaException extends Exception {
    public BazaPodatakaException(String message, Throwable cause) {
        super(message, cause);
    }

    public BazaPodatakaException(Throwable cause) {
        super(cause);
    }
}
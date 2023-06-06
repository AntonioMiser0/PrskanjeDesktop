package hr.java.prskanje.iznimke;

public class HektarException extends RuntimeException{
    public HektarException(String message) {
        super(message);
    }

    public HektarException(Throwable cause) {
        super(cause);
    }
}

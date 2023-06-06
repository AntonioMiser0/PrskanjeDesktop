package hr.java.prskanje.entiteti;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UpisGeneric<T> implements Serializable {

    private String prije;
    private T objekt;
    private String datum;

    public UpisGeneric(String prije, T objekt, String datum) {
        this.prije = prije;
        this.objekt = objekt;
        this.datum = datum;
    }


    @Override
    public String toString() {
        return "Zapis: " +
                "'" + prije + '\'' +
                ", objekt=" + objekt +
                ", '" + datum + '\'';
    }
}

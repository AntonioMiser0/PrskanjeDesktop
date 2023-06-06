package hr.java.prskanje.entiteti;

import java.io.Serializable;

public record Polje(String naziv, Integer udaljenost) implements Serializable {
    public Polje(String naziv, Integer udaljenost) {
        this.naziv = naziv;
        this.udaljenost = udaljenost;
    }
}

package hr.java.prskanje.entiteti;

import java.io.Serializable;
import java.time.LocalDate;

public final class Prskanje implements Serializable, PrskanjeZemlje {
    private long id;
private Zemlja zemlja;
private Pesticidi pesticid;
private Integer kolicina;
private LocalDate datum;

    public Prskanje(long id,Zemlja zemlja, Pesticidi pesticid, Integer kolicina, LocalDate datum) {
        this.id=id;
        this.zemlja = zemlja;
        this.pesticid = pesticid;
        this.kolicina = kolicina;
        this.datum = datum;
    }

    public Zemlja getZemlja() {
        return zemlja;
    }

    public void setZemlja(Zemlja zemlja) {
        this.zemlja = zemlja;
    }

    public Pesticidi getPesticid() {
        return pesticid;
    }

    public void setPesticid(Pesticidi pesticid) {
        this.pesticid = pesticid;
    }

    public Integer getKolicina() {
        return kolicina;
    }

    public void setKolicina(Integer kolicina) {
        this.kolicina = kolicina;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public long getId() {
        return id;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    @Override
    public String toString() {
        return "Prskanje{" +
                "id=" + id +
                ", zemlja=" + zemlja +
                ", pesticid=" + pesticid +
                ", kolicina=" + kolicina +
                ", datum=" + datum +
                '}';
    }
}

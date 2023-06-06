package hr.java.prskanje.entiteti;

import java.io.Serializable;

public abstract class Pesticidi implements PesticidiInterface,Serializable {
    private String naziv;
    private Integer miliLitar;
    private Long id;

    private String vrsta;

    public Pesticidi(String naziv, Integer miliLitar, Long id, String vrsta) {
        this.naziv = naziv;
        this.miliLitar = miliLitar;
        this.id = id;
        this.vrsta = vrsta;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Integer getMiliLitar() {
        return miliLitar;
    }

    public void setMiliLitar(Integer miliLitar) {
        this.miliLitar = miliLitar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pesticidi{" +
                "naziv='" + naziv + '\'' +
                ", miliLitar=" + miliLitar +
                ", id=" + id +
                ", vrsta='" + vrsta + '\'' +
                '}';
    }
}

package hr.java.prskanje.entiteti;

import java.io.Serializable;
import java.util.HashMap;

public final class Zemlja implements Serializable {
    private Long id;
    private String naziv;
    private Long hektara;
    private Polje polje;
    private String kultura;
    private String zakup;
    private HashMap<Integer,Pesticidi> pesticid;
    private String vrstaTla;

    private Zemlja(ZemljaBuilder builder) {
        this.id=builder.id;
        this.naziv = builder.naziv;
        this.hektara = builder.hektara;
        this.polje = builder.polje;
        this.kultura = builder.kultura;
        this.zakup=builder.zakup;
        this.pesticid=builder.pesticid;
        this.vrstaTla=builder.vrstaTla;
    }
public static class ZemljaBuilder{
    private Long id;
    private String naziv;
    private Long hektara;
    private Polje polje;
    private String kultura;
    private String zakup;
    private HashMap<Integer,Pesticidi> pesticid;
    private String vrstaTla;


    public ZemljaBuilder(Long id, String naziv, Long hektara, Polje polje, String kultura, HashMap<Integer,Pesticidi> pesticid,String vrstaTla) {
        this.naziv = naziv;
        this.hektara = hektara;
        this.polje = polje;
        this.kultura = kultura;
        this.pesticid=pesticid;
        this.vrstaTla=vrstaTla;
        this.id=id;
    }

    public ZemljaBuilder setZakup(String zakup) {
        this.zakup = zakup;
    return this;
    }
    public Zemlja build(){
        return new Zemlja(this);
    }
}

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Long getHektara() {
        return hektara;
    }

    public void setHektara(Long hektara) {
        this.hektara = hektara;
    }

    public Polje getPolje() {
        return polje;
    }

    public void setPolje(Polje polje) {
        this.polje = polje;
    }

    public String getKultura() {
        return kultura;
    }

    public void setKultura(String kultura) {
        this.kultura = kultura;
    }

    public String getZakup() {
        return zakup;
    }

    public void setZakup(String zakup) {
        this.zakup = zakup;
    }

    public HashMap<Integer,Pesticidi> getPesticid() {
        return pesticid;
    }

    public void setPesticid(HashMap<Integer,Pesticidi>pesticid) {
        this.pesticid = pesticid;
    }

    public String getVrstaTla() {
        return vrstaTla;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setVrstaTla(String vrstaTla) {
        this.vrstaTla = vrstaTla;
    }

    @Override
    public String toString() {
        return "Zemlja{" +
                "id=" + id +
                ", naziv='" + naziv + '\'' +
                ", hektara=" + hektara +
                ", polje=" + polje +
                ", kultura='" + kultura + '\'' +
                ", zakup='" + zakup + '\'' +
                ", pesticid=" + pesticid +
                ", vrstaTla='" + vrstaTla + '\'' +
                '}';
    }
}

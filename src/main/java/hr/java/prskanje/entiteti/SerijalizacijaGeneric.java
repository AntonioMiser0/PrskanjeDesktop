package hr.java.prskanje.entiteti;

import hr.java.prskanje.glavni.Login;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SerijalizacijaGeneric  <T extends String,V>implements Serializable {
    private T tekst;
    private V objekt;

    public SerijalizacijaGeneric(T tekst, V objekt) {
        this.tekst = tekst;
        this.objekt = objekt;
    }

    public void setTekst(T tekst) {
        this.tekst = tekst;
    }

    public void setObjekt(V objekt) {
        this.objekt = objekt;
    }
    List<UpisGeneric<Object>> listo=new ArrayList<>();

    public void ispis(){
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("dat/promjena.txt"))) {
            listo= (List<UpisGeneric<Object>>) in.readObject();
            listo.forEach(System.out::println);
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("dat/promjena.txt")))
        {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            Login login=new Login();
            listo.add(new UpisGeneric<>(login.getProvjera().toString()+" - " +tekst,objekt,dtf.format(now)+"\n"));
            out.writeObject(listo);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    public List<UpisGeneric<Object>> procitaneStavke() {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("dat/promjena.txt"))) {
            listo= (List<UpisGeneric<Object>>) in.readObject();
            listo.forEach(System.out::println);
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }
    return listo;
    }

}

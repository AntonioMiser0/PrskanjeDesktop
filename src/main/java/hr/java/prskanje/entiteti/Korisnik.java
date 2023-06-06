package hr.java.prskanje.entiteti;

import java.io.Serializable;

public class Korisnik implements Serializable {
    String username;
    String password;
    String dopustenje;

    public Korisnik(String username, String password, String dopustenje) {
        this.username = username;
        this.password = password;
        this.dopustenje = dopustenje;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDopustenje() {
        return dopustenje;
    }

    public void setDopustenje(String dopustenje) {
        this.dopustenje = dopustenje;
    }

    @Override
    public String toString() {
        return "Korisnik{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dopustenje='" + dopustenje + '\'' +
                '}';
    }
}

package hr.java.prskanje.Baza;


import hr.java.prskanje.entiteti.*;
import hr.java.prskanje.iznimke.BazaPodatakaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BazaPodataka {

    private static final Logger logger = LoggerFactory.getLogger(BazaPodataka.class);
    private static final String DATABASE_FILE = "dat/bazaPodataka.properties";



    private static Connection spajanjeNaBazu() throws SQLException, IOException,BazaPodatakaException {
        Properties svojstva = new Properties();
        svojstva.load(new FileInputStream(DATABASE_FILE));

        Connection con=DriverManager.getConnection(
                svojstva.getProperty("bazaPodatakaURL"),
                svojstva.getProperty("korisnickoIme"),
                svojstva.getProperty("lozinka"));
    return  con;
    }
    public static List<Pesticidi> dohvatiSvePesticide() throws BazaPodatakaException {

        List<Pesticidi> listaPesticid = new ArrayList<>();
        try {
            Connection con = spajanjeNaBazu();

            Statement stmt = con.createStatement();

            ResultSet rs=stmt.executeQuery("SELECT * FROM PESTICID");
            while(rs.next()){
                Long id = rs.getLong("id");
                String naziv = rs.getString("naziv");
                Integer kolicina = rs.getInt("kolicina");
                String vrsta = rs.getString("vrsta");
                listaPesticid.add(vratiPesticid( id, naziv, kolicina, vrsta));
            }
            con.close();
        }
        catch (Exception e){
            logger.error("ERROR",e);
            String poruka="Došlo je do pogreške kod spajanja na bazu podataka!";
            throw new BazaPodatakaException(poruka, e);
        }
        return listaPesticid;
    }

    public static Pesticidi vratiPesticid(Long id, String naziv, Integer kolicina, String vrsta) {
        if(vrsta.equals("insekticid")){
            return new Insekticidi(naziv, kolicina, id, vrsta);
        }
        else if (vrsta.equals("herbicid")) {
            return new Herbicidi(naziv, kolicina, id, vrsta);
        }
        else {
            return new Fungicidi(naziv, kolicina, id, vrsta);
        }
    }

    public static List<Zemlja> dohvatiSveZemlje() throws BazaPodatakaException {

        List<Zemlja> listaZemlja = new ArrayList<>();
        List<Pesticidi> listaPesticid=new ArrayList<>();
        listaPesticid=dohvatiSvePesticide();
        try {
            Connection con = spajanjeNaBazu();

            Statement stmt = con.createStatement();

            ResultSet rs=stmt.executeQuery("SELECT * FROM ZEMLJA");
            while(rs.next()){

                Long id = rs.getLong("id");
                String naziv = rs.getString("naziv");
                Long brhektara = rs.getLong("brhektara");
                String polje_naziv = rs.getString("polje_naziv");
                Integer polje_udaljenost = rs.getInt("polje_udaljenost");
                String kultura = rs.getString("kultura");
                String zakup = rs.getString("zakup");
                String vrsta_tla = rs.getString("vrsta_tla");
                Statement stmt1 = null;
                Polje polje=new Polje(polje_naziv,polje_udaljenost);
                Zemlja novaZemlja = new Zemlja.ZemljaBuilder(id,naziv,brhektara,polje,kultura,new HashMap<Integer,Pesticidi>(),vrsta_tla).build();
                if(zakup.equals("ima")) {
                    novaZemlja.setZakup("ima");
                }
                else {
                    novaZemlja.setZakup("nema");
                }
                listaZemlja.add(novaZemlja);
                }
            con.close();
        }
        catch (Exception e){
            logger.error("ERROR",e);
            String poruka="Došlo je do pogreške kod spajanja na bazu podataka!";
            throw new BazaPodatakaException(poruka, e);
        }
        return listaZemlja;

    }
    public static List<Prskanje> dohvatiSveZemlja_Pesticid() throws BazaPodatakaException {

        List<Prskanje> listaPrskanje = new ArrayList<>();
        var listaPesticid=dohvatiSvePesticide();
        var listaZemlja=dohvatiSveZemlje();
        try {
            Connection con = spajanjeNaBazu();

            Statement stmt = con.createStatement();

            ResultSet rs=stmt.executeQuery("SELECT * FROM ZEMLJA_PRSKANJE");
            while(rs.next()){
                long id = rs.getLong("id");
                Long zemlja_id = rs.getLong("zemlja_id");
                String pesticid_id = rs.getString("pesticid_id");
                Integer kolicina = rs.getInt("kolicina");
                LocalDate datum = rs.getTimestamp("datum").toInstant().atZone(
                        ZoneId.systemDefault()).toLocalDate();
                Prskanje prskanje=new Prskanje(id,
                        listaZemlja.stream().filter(x -> x.getId().equals(zemlja_id)).toList().get(0),
                  listaPesticid.stream().filter(x->x.getNaziv().equals(pesticid_id)).toList().get(0),
                        kolicina,
                        datum);
                listaPrskanje.add(prskanje);
            }
            con.close();
        }
        catch (Exception e){
            logger.error("ERROR",e);
            String poruka="Došlo je do pogreške kod spajanja na bazu podataka!";
            throw new BazaPodatakaException(poruka, e);
        }
        return listaPrskanje;
    }
    public static void spremiNoviPesticid (Pesticidi pesticid)throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu())
        {

            PreparedStatement preparedStatement = veza.prepareStatement("INSERT INTO PESTICID" +
                    "(naziv, kolicina, vrsta) VALUES(?,?,?)");
            preparedStatement.setString(1, pesticid.getNaziv());
            preparedStatement.setInt(2, pesticid.getMiliLitar());
            preparedStatement.setString(3, pesticid.getVrsta());
            preparedStatement.executeUpdate();



        } catch (SQLException | IOException e) {
            logger.warn("Problem kod spremanja novog pesticida!",e);
            System.out.println("Problem kod spremanja novog pesticida!");
            throw new RuntimeException(e);
        }
    }
    public static void urediPesticid(Long id, Pesticidi pesticid) throws BazaPodatakaException {
        try(Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement = veza.prepareStatement("UPDATE PESTICID SET NAZIV = ?, KOLICINA = ?, VRSTA = ? WHERE ID = ?");
            preparedStatement.setString(1, pesticid.getNaziv());
            preparedStatement.setInt(2, pesticid.getMiliLitar());
            preparedStatement.setString(3, pesticid.getVrsta());
            preparedStatement.setLong(4, id);
            preparedStatement.executeUpdate();

        }
        catch(SQLException | BazaPodatakaException | IOException e)
        {
            logger.warn("Problem kod uredivanja pesticida!",e);
            throw new RuntimeException();
        }
    }
    public static void obrisiPesticid(Pesticidi pesticid) throws BazaPodatakaException {
        try(Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement= veza.prepareStatement("DELETE FROM ZEMLJA_PRSKANJE WHERE PESTICID_ID = ?");
            preparedStatement.setString(1, pesticid.getNaziv());
            preparedStatement.executeUpdate();
            preparedStatement = veza.prepareStatement("DELETE FROM PESTICID WHERE NAZIV = ?");
            preparedStatement.setString(1, pesticid.getNaziv());
            preparedStatement.executeUpdate();
        }
        catch(SQLException | BazaPodatakaException | IOException e)
        {
            logger.warn("Problem kod brisanja pesticida!",e);
            throw new RuntimeException();
        }
    }
    public static void spremiNoviZemlja (Zemlja zemlja)throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement = veza.prepareStatement("INSERT INTO ZEMLJA" +
                    "(naziv, brhektara, polje_naziv,polje_udaljenost,kultura," +
                    "zakup,vrsta_tla) VALUES(?,?,?,?,?,?,?)");
            preparedStatement.setString(1, zemlja.getNaziv());
            preparedStatement.setInt(2, Math.toIntExact(zemlja.getHektara()));
            preparedStatement.setString(3, zemlja.getPolje().naziv());
            preparedStatement.setInt(4, zemlja.getPolje().udaljenost());
            preparedStatement.setString(5, zemlja.getKultura());
            preparedStatement.setString(6, zemlja.getZakup());
            preparedStatement.setString(7, zemlja.getVrstaTla());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.warn("Problem kod spremanja zemlje!",e);
            System.out.println("Problem kod spremanja nove zemlje!");
            throw new RuntimeException(e);
        }
    }
    public static void urediZemlja(Long id, Zemlja zemlja) throws BazaPodatakaException {
        try(Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement = veza.prepareStatement("UPDATE ZEMLJA SET NAZIV = ?, BRHEKTARA = ?, POLJE_NAZIV = ?, " +
                    "POLJE_UDALJENOST = ?, KULTURA = ?, ZAKUP = ?, VRSTA_TLA = ? WHERE ID = ?");
            preparedStatement.setString(1, zemlja.getNaziv());
            preparedStatement.setInt(2, Math.toIntExact(zemlja.getHektara()));
            preparedStatement.setString(3, zemlja.getPolje().naziv());
            preparedStatement.setInt(4, zemlja.getPolje().udaljenost());
            preparedStatement.setString(5, zemlja.getKultura());
            preparedStatement.setString(6, zemlja.getZakup());
            preparedStatement.setString(7, zemlja.getVrstaTla());
            preparedStatement.setLong(8, id);
            preparedStatement.executeUpdate();

        }
        catch(SQLException | BazaPodatakaException | IOException e)
        {
            logger.warn("Problem kod uredivanja zemlje!",e);
            throw new RuntimeException();
        }
    }
    public static void obrisiZemlja(Zemlja zemlja) throws BazaPodatakaException {
        try(Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement= veza.prepareStatement("DELETE FROM ZEMLJA_PRSKANJE WHERE ZEMLJA_ID = ?");
            preparedStatement.setLong(1, zemlja.getId());
            preparedStatement.executeUpdate();
            preparedStatement = veza.prepareStatement("DELETE FROM ZEMLJA WHERE ID = ?");
            preparedStatement.setLong(1, zemlja.getId());
            preparedStatement.executeUpdate();

        }
        catch(SQLException | BazaPodatakaException | IOException e)
        {
            logger.warn("Problem kod brisanja zemlje!",e);
            throw new RuntimeException();
        }
    }
    public static void spremiNoviPrskanje (Prskanje prskanje)throws BazaPodatakaException {
        try (Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement = veza.prepareStatement("INSERT INTO ZEMLJA_PRSKANJE" +
                    "(zemlja_id, pesticid_id, kolicina, datum) VALUES(?,?,?,?)");
            preparedStatement.setLong(1, prskanje.getZemlja().getId());
            preparedStatement.setString(2, prskanje.getPesticid().getNaziv());
            preparedStatement.setInt(3, prskanje.getKolicina());
            preparedStatement.setDate(4, Date.valueOf(prskanje.getDatum()));
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException e) {
            logger.warn("Problem kod spremanja novog prskanja!",e);
            System.out.println("Problem kod spremanja novog prskanja!");
            throw new RuntimeException(e);
        }
    }
    public static void obrisiPrskanje(Prskanje prskanje) throws BazaPodatakaException {
        try(Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement = veza.prepareStatement("DELETE FROM ZEMLJA_PRSKANJE WHERE ID = ?");
            preparedStatement.setLong(1, prskanje.getId());
            preparedStatement.executeUpdate();
        }
        catch(SQLException | BazaPodatakaException | IOException e)
        {
            logger.warn("Problem kod brisanja prskanja!",e);
            throw new RuntimeException();
        }
    }
    public static void urediPrskanje(Long id,Prskanje prskanje) throws BazaPodatakaException {
        try(Connection veza = spajanjeNaBazu())
        {
            PreparedStatement preparedStatement = veza.prepareStatement("UPDATE ZEMLJA_PRSKANJE SET ZEMLJA_ID" +
                    " = ?, PESTICID_ID = ?, KOLICINA = ?, DATUM = ? WHERE ID = ?");
            preparedStatement.setLong(1, prskanje.getZemlja().getId());
            preparedStatement.setString(2, prskanje.getPesticid().getNaziv());
            preparedStatement.setInt(3, prskanje.getKolicina());
            preparedStatement.setDate(4, Date.valueOf(prskanje.getDatum()));
            preparedStatement.setLong(5, id);
            preparedStatement.executeUpdate();

        }
        catch(SQLException | BazaPodatakaException | IOException e)
        {
            logger.warn("Problem kod uredivanja prskanja!",e);
            throw new RuntimeException();
        }
    }



}

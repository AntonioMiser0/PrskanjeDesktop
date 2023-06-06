package hr.java.prskanje.glavni;

import hr.java.prskanje.entiteti.Korisnik;
import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Login {
    private static final Logger logger = LoggerFactory.getLogger(Login.class);

    public static final String FILE_NAME = "dat\\korisnici.txt";
    private static final StringBuilder naslov= new StringBuilder();


    @FXML
    private TextField UsernameTextField;
    @FXML
    private TextField PasswordTextField;
    @FXML
    private AnchorPane rootPane;
private Korisnik tryLogin(String username,String password) {



    try(BufferedReader reader = new BufferedReader(new FileReader(new File(FILE_NAME)))) {
        while ((reader.readLine()) != null) {
            String userline=reader.readLine();
            String passline=reader.readLine();
            String dopustenje= reader.readLine();
            System.out.println(userline+" + " + passline + " + " + dopustenje);
            byte[] resultByteArray = new byte[0];
            byte[] resultByteArray1=new byte[0];

            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(password.getBytes());
                messageDigest.update(username.getBytes());
                resultByteArray = messageDigest.digest();
            } catch (NoSuchAlgorithmException e) {
                logger.warn("Greska kod hashiranja",e);
                e.printStackTrace();
            }
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(passline.getBytes());
                messageDigest.update(userline.getBytes());
                resultByteArray1 = messageDigest.digest();
            } catch (NoSuchAlgorithmException e) {
                logger.error("Greska kod hashiranja",e);
                e.printStackTrace();
            }

            if (Arrays.equals(resultByteArray, resultByteArray1)) {
                return new Korisnik(username, password, dopustenje);
            }
        }
    }
    catch(IOException e)
    {
        logger.error("Nije pronadena datoteka",e);
        throw new RuntimeException("Nije pronađena dadoteka za korisnike!");
    }

    return null;
}

public void Register()throws IOException{
    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), 800, 500);
    HelloApplication.getMainStage().setTitle("Registracija");
    HelloApplication.getMainStage().setScene(scene);
    HelloApplication.getMainStage().show();
}


    public void Login(){
        String username = UsernameTextField.getText();
        String lozinka=PasswordTextField.getText();

        Korisnik provjera=tryLogin(username,lozinka);
        assert provjera != null;

if(provjera == null){
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Pogrešan username ili password");
    alert.setHeaderText("Pokušajte ponovo");
    alert.showAndWait();
}
else{
    SerijalizacijaGeneric<String, Korisnik> serijalizacijaGeneric = new SerijalizacijaGeneric<>("Ulogirao se je: ", provjera);
    serijalizacijaGeneric.ispis();
    naslov.append(provjera.getDopustenje()+": "+provjera.getUsername());
        makeFade(provjera);
}
        System.out.println(username +" "+ lozinka);
    }
    public void showGlavniEkran(Korisnik provjera) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("glavniEkran.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        HelloApplication.getMainStage().setTitle(provjera.getDopustenje()+": "+provjera.getUsername());
        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }
    public StringBuilder getProvjera(){
    return naslov;
    }

    private void makeFade(Korisnik provjera){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(500));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    showGlavniEkran(provjera);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        fadeTransition.play();
    }
}

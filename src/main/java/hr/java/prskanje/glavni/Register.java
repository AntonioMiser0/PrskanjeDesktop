package hr.java.prskanje.glavni;

import hr.java.prskanje.entiteti.Korisnik;
import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Register {
    @FXML
    private TextField UsernameTextField;
    @FXML
    private TextField PasswordTextField;
    @FXML
    private TextField PotvrdaPasswordTextField;
    @FXML
    private RadioButton UserRadioButton;
    @FXML
    private RadioButton AdminRadioButton;
    public static final String FILE_NAME = "dat\\korisnici.txt";
    private static final Logger logger = LoggerFactory.getLogger(Register.class);

    public void Register() throws IOException {
        String username = UsernameTextField.getText();
        String lozinka = PasswordTextField.getText();
        String provjera = PotvrdaPasswordTextField.getText();
        if (!lozinka.equals(provjera)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška kod unosa lozinke");
            alert.setHeaderText("Lozinke se ne podudaraju");
            alert.showAndWait();
        } else if (lozinka.length() < 5) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška kod unosa lozinke");
            alert.setHeaderText("Prekrataka lozinka");
            alert.showAndWait();
        } else if (username.length() < 4) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška kod unosa usernamea");
            alert.setHeaderText("Prekratak username");
            alert.showAndWait();
        } else {

            String dopustenje;
            if (UserRadioButton.isSelected())
                dopustenje = "User";
            else
                dopustenje = "Admin";

            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(new File(FILE_NAME)))) {
                    while ((reader.readLine()) != null) {
                        String userline = reader.readLine();
                        String passline = reader.readLine();
                        String vrstaD = reader.readLine();

                        if (userline.equals(UsernameTextField.getText())) {
                            throw new IOException("Postoji vec taj user");
                        }
                    }


                    Korisnik korisnik = new Korisnik(username, lozinka, dopustenje);
                    try (Formatter out = new Formatter(new FileOutputStream(FILE_NAME, true))) {
                        out.format("\n" + username + "\n" + lozinka + "\n" + dopustenje + "\n");
                    } catch (IOException e) {
                        logger.error("Neuspjesni unos korisnika.", e);
                        System.err.println(e);
                    }
                    SerijalizacijaGeneric<String, Korisnik> serijalizacijaGeneric = new SerijalizacijaGeneric<>("Registrirao se je: ", korisnik);
                    serijalizacijaGeneric.ispis();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Uspješno dodan novi korisnik");
                    alert.setHeaderText("Novi korisnik");
                    alert.setContentText("Ime: " + korisnik.getUsername() + ", lozinka: " + korisnik.getPassword());
                    alert.showAndWait();

                    System.out.println(username + " " + lozinka);
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 800, 500);
                    HelloApplication.getMainStage().setTitle("Login");
                    HelloApplication.getMainStage().setScene(scene);
                    HelloApplication.getMainStage().show();
                }
            }
            catch (IOException e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Taj korisnik vec potoji");
                alert.setHeaderText("Izaberite drugo ime");
                alert.setContentText("Pokušatje ponovo");
                alert.showAndWait();
                logger.warn("Potoji taj korisnik",e);
            }
            }
        }
    }


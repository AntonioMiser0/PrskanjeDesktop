package hr.java.prskanje.glavni;

import hr.java.prskanje.entiteti.Korisnik;
import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.io.IOException;

public class Izbornik {
    public void showZemljaSort() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("zemlja-sort.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        HelloApplication.getMainStage().setTitle("Unos");
        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }
    public void showPesticidiSort() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("pesticidi-sort.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        HelloApplication.getMainStage().setTitle("Pretraga!");
        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }
    public void showGlavniEkran() throws IOException {
        Login login = new Login();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("glavniEkran.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        HelloApplication.getMainStage().setTitle(String.valueOf(login.getProvjera()));
        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }
    public void showPromjeneEkran() throws IOException {
        Login login = new Login();
        String title = String.valueOf(login.getProvjera()).substring(0,5);
        if(title.equals("Admin")) {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("promjene-ekran.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 500);
            HelloApplication.getMainStage().setTitle("Promjene");
            HelloApplication.getMainStage().setScene(scene);
            HelloApplication.getMainStage().show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            SerijalizacijaGeneric<String,String> serijalizacijaGeneric1=new
                    SerijalizacijaGeneric<>("Glavni ekran neovlasten pristup: ","kod ulaza u PromjeneEkran");
            serijalizacijaGeneric1.ispis();
            alert.setTitle("Neovlašten pristup");
            alert.setHeaderText("Trebate biti admin da bi mogli pristupiti");
            alert.showAndWait();
        }
    }
    public void showPrskanjeEkran() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("prskanje-ekran.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        HelloApplication.getMainStage().setTitle("Pregled");
        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }

}

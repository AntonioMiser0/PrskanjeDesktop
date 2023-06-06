package hr.java.prskanje.glavni;

import hr.java.prskanje.Baza.BazaPodataka;
import hr.java.prskanje.entiteti.Korisnik;
import hr.java.prskanje.entiteti.Prskanje;
import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import hr.java.prskanje.entiteti.VrijemeNit;
import hr.java.prskanje.iznimke.BazaPodatakaException;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlavniEkran {

    @FXML
    private Label time;
    @FXML
    private TextField vrijeme;
    @FXML
    private Label vrijemeLabel;
    @FXML
    ImageView slikaView;

    @FXML
    private TableView<Prskanje> prskanjeTableView;
    @FXML
    private TableColumn<Prskanje,String> zemljaPrskanjeColumn;
    @FXML
    private TableColumn<Prskanje,String> pesticidiPrskanjeColumn;
    @FXML
    private TableColumn<Prskanje, String> kolicinaPrskanjeTableColumn;
    @FXML
    private TableColumn<Prskanje, String> datumPrskanjeTableColumn;
    @FXML
    private AnchorPane rootPane;

    private volatile boolean stop=false;
    private static boolean stani=true;
    static int sati;
    static int minute;
    List<Prskanje> prskanjeList=new ArrayList<>();
    LocalDateTime now = LocalDateTime.now();

    private static final Logger logger = LoggerFactory.getLogger(GlavniEkran.class);



    public void  initialize(){
        makeFade();
    Timenow();
    try {
        prskanjeList = BazaPodataka.dohvatiSveZemlja_Pesticid();
    } catch (BazaPodatakaException e) {
        logger.error("ERROR",e);
        e.getMessage();
        e.printStackTrace();;
    }

    zemljaPrskanjeColumn
            .setCellValueFactory(cellData->
                    new SimpleStringProperty(cellData.getValue().getZemlja().getNaziv()));
    pesticidiPrskanjeColumn
            .setCellValueFactory(cellData->
                    new SimpleStringProperty(cellData.getValue().getPesticid().getNaziv()));
    kolicinaPrskanjeTableColumn
            .setCellValueFactory(cellData->
                    new SimpleStringProperty(cellData.getValue().getKolicina().toString()+"ml"));

    datumPrskanjeTableColumn.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<Prskanje, String>,
                    ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(
                        TableColumn.CellDataFeatures<Prskanje, String> x) {
                    SimpleStringProperty property = new
                            SimpleStringProperty();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                    property.setValue(
                            x.getValue().getDatum().format(formatter));
                    return property;
                }
            });
    prskanjeTableView.setItems(FXCollections.observableList((prskanjeList)));

    }



public  void PostaviVrijeme() {
    String title = HelloApplication.getMainStage().getTitle().substring(0,5);
    if (title.equals("Admin")) {
        System.out.println(title);
        LocalTime vr=null;
        try {
            vr = LocalTime.parse(vrijeme.getText(), DateTimeFormatter.ofPattern("HH:mm"));

            LocalTime finalVr = vr;
            VrijemeNit prikazV= new VrijemeNit(finalVr, vrijemeLabel,stani,slikaView);
            if (stani) {
                stani=false;
                prikazV.start();
            }
        }
    catch (RuntimeException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Krivi unos");
            alert.setHeaderText("Trebate unesti HH:mm");
            alert.showAndWait();
            logger.warn("Krivi format",e);
            System.out.println(e);
        }
    }

    else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Neovlašten pristup");
            alert.setHeaderText("Trebate biti admin da bi mogli pristupiti");
        SerijalizacijaGeneric<String,String> serijalizacijaGeneric1=new
                SerijalizacijaGeneric<>("Glavni ekran neovlasten pristup: ",HelloApplication.getMainStage().getTitle()
                .substring(7));
        serijalizacijaGeneric1.ispis();
            alert.showAndWait();
        }
    }
    public void Timenow(){
        Thread thread = new Thread(()->{
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            while(!stop){
                try{Thread.sleep(1000);
                    }catch (Exception e){
                    logger.warn("Greska kod dretve",e);
                        System.out.println(e);
                    }
                    now = LocalDateTime.now();
                     Platform.runLater(()->{
                    time.setText(dtf.format(now));
                });
            }
        });
    thread.start();
    }
    private void makeFade(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(500));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }
}

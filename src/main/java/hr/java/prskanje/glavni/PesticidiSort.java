package hr.java.prskanje.glavni;

import hr.java.prskanje.Baza.BazaPodataka;
import hr.java.prskanje.entiteti.Pesticidi;
import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import hr.java.prskanje.iznimke.BazaPodatakaException;
import hr.java.prskanje.iznimke.MilitarException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PesticidiSort {

    @FXML
    private TextField nazivTextField;
    @FXML
    private TextField kolicinaTextField;
    @FXML
    private ChoiceBox<String> vrstaChoiceBox;
    private static final String[] vrstePesticida={"herbicid","fungicid","insekticid"};
    @FXML
    private TableView<Pesticidi> pesticidiTableView;
    @FXML
    private TableColumn<Pesticidi,String> nazivColumn;
    @FXML
    private TableColumn<Pesticidi,String> IDColumn;
    @FXML
    private TableColumn<Pesticidi,String> kolicinaColumn;
    @FXML
    private TableColumn<Pesticidi,String> vrstaColumn;

    private List<Pesticidi> pesticidiList;
    SerijalizacijaGeneric<String, Pesticidi> serijalizacijaGeneric;
    private static final Logger logger = LoggerFactory.getLogger(PesticidiSort.class);
    private  OptionalLong maksimalniId;
    public void initialize() {

        vrstaChoiceBox.getItems().addAll(vrstePesticida);

        try {
            pesticidiList = BazaPodataka.dohvatiSvePesticide();
        } catch (BazaPodatakaException e) {
            e.getMessage();
            e.printStackTrace();
        }

        IDColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getId().toString()));
        nazivColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getNaziv()));
        kolicinaColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getMiliLitar().toString()));
        vrstaColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getVrsta()));

        maksimalniId = pesticidiList.stream()
                .mapToLong(Pesticidi::getId).max();
        postaviTablicu();
        pesticidiTableView.setRowFactory(tv -> {
            TableRow<Pesticidi> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Pesticidi rowData = row.getItem();
                    kolicinaTextField.setText(rowData.getMiliLitar().toString());
                    nazivTextField.setText(rowData.getNaziv());
                    vrstaChoiceBox.setValue(rowData.getVrsta());
                    System.out.println("Double click on: "+rowData.getMiliLitar().toString());
                }
            });
            return row ;
        });
    }

    private void postaviTablicu() {
         pesticidiTableView.setItems(FXCollections.observableList((pesticidiList)));
    }
    public void upisiPesticid() {
        try {
            if (nazivTextField.getText().isEmpty() || kolicinaTextField.getText().isEmpty() || vrstaChoiceBox.getSelectionModel().isEmpty()) {
                upisGreska();
            } else if (!kolicinaTextField.getText().matches("[0-9]+")) {
                throw new MilitarException("Nije unesen Integer");
            } else {
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Spremanje novog pesticida!");
                alert1.setContentText("Spremi?");
                ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
                alert1.showAndWait().ifPresent(type -> {
                    if (type == okButton) {
                        Long id2 = maksimalniId.getAsLong();

                        try {

                            Pesticidi pesticid = BazaPodataka.vratiPesticid(id2, nazivTextField.getText(), Integer.parseInt(kolicinaTextField.getText()), vrstaChoiceBox.getValue());
                            BazaPodataka.spremiNoviPesticid(pesticid);
                            serijalizacijaGeneric = new SerijalizacijaGeneric<>("Dodan je novi: ", pesticid);
                            serijalizacijaGeneric.ispis();

                        } catch (BazaPodatakaException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            pesticidiList = BazaPodataka.dohvatiSvePesticide();
                        } catch (BazaPodatakaException e) {
                            throw new RuntimeException(e);
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješan unos podataka");
                        alert.setHeaderText("Spremanje podataka o novom pesticidu");
                        alert.setContentText("Podaci o novom pesticidu su uspješno dodani!");
                        alert.showAndWait();
                        postaviTablicu();
                    }
                });

            }
        }
        catch (MilitarException e) {
            upisGreskaKolicina();
            logger.error("Nije unese Integer",e);
        }
    }
    public void izmjeniPesticid() {
       try {
           if (nazivTextField.getText().isEmpty() || kolicinaTextField.getText().isEmpty() || vrstaChoiceBox.getSelectionModel().isEmpty()) {
               upisGreska();
           } else if (!kolicinaTextField.getText().matches("[0-9]+")) {
               throw new MilitarException("Nije unesen Integer");
           } else {
               Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
               alert1.setTitle("Uređivanje novog pesticida!");
               alert1.setContentText("Uredi?");
               ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
               ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
               ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
               alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
               alert1.showAndWait().ifPresent(type -> {
                   if (type == okButton) {
                       Long id2 = maksimalniId.getAsLong();

                       Pesticidi pesticid = BazaPodataka.vratiPesticid(id2, nazivTextField.getText(), Integer.parseInt(kolicinaTextField.getText()), vrstaChoiceBox.getValue());
                       serijalizacijaGeneric = new SerijalizacijaGeneric<>("Promijnjen je: ", pesticid);
                       serijalizacijaGeneric.ispis();
                       long id = pesticidiTableView.getSelectionModel().getSelectedItem().getId();
                       try {
                           BazaPodataka.urediPesticid(pesticidiTableView.getSelectionModel().getSelectedItem().getId(), pesticid);
                       } catch (BazaPodatakaException e) {
                           logger.error("ERROR kod izmjene", e);
                           throw new RuntimeException(e);
                       }


                       try {
                           pesticidiList = BazaPodataka.dohvatiSvePesticide();
                       } catch (BazaPodatakaException e) {
                           logger.error("ERROR kod izmjene", e);
                           throw new RuntimeException(e);
                       }
                       serijalizacijaGeneric = new SerijalizacijaGeneric<>("Novi je: ", pesticidiList.stream()
                               .filter(x -> x.getId().equals(id)).toList().get(0));
                       serijalizacijaGeneric.ispis();
                       Alert alert = new Alert(Alert.AlertType.INFORMATION);
                       alert.setTitle("Uspješna izmjena podataka");
                       alert.setHeaderText("Izmjenjeni podataci o pesticidu");
                       alert.setContentText("Podaci o novom pesticidu su uspješno dodani!");
                       alert.showAndWait();
                       postaviTablicu();
                   }
               });

           }
       }
       catch (MilitarException e) {
           upisGreskaKolicina();
           logger.error("Nije unese Integer",e);
           }
    }
    public void obrisiPesticid() {
       try{
        if (nazivTextField.getText().isEmpty() || kolicinaTextField.getText().isEmpty() || vrstaChoiceBox.getSelectionModel().isEmpty()) {
            upisGreska();
        }else if (!kolicinaTextField.getText().matches("[0-9]+")) {
            throw new MilitarException("Nije unesen Integer");
        }
        else{
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Brisanje pesticida!");
                alert1.setContentText("Obriši?");
                ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
                alert1.showAndWait().ifPresent(type -> {
                    if (type == okButton) {
                        Long id2 = maksimalniId.getAsLong();

                        Pesticidi pesticid = BazaPodataka.vratiPesticid(id2, nazivTextField.getText(), Integer.parseInt(kolicinaTextField.getText()), vrstaChoiceBox.getValue());
                        serijalizacijaGeneric = new SerijalizacijaGeneric<>("Obrisao se pesticid: ", pesticid);
                        serijalizacijaGeneric.ispis();
                        try {
                            BazaPodataka.obrisiPesticid(pesticid);
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR kod brisanja", e);
                            throw new RuntimeException(e);
                        }

                        try {
                            pesticidiList = BazaPodataka.dohvatiSvePesticide();
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR kod brisanja", e);
                            throw new RuntimeException(e);
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješno brisanje podataka");
                        alert.setHeaderText("Obrisan pesticid");
                        alert.setContentText("Podaci o pesticidu su uspješno obrisani!");
                        alert.showAndWait();
                        postaviTablicu();
                    }
                });

            }
        }
        catch (MilitarException e) {
               upisGreskaKolicina();
               logger.error("Nije unese Integer",e);
               }
    }
    public void sortirajPesticid() throws BazaPodatakaException {

        pesticidiList=BazaPodataka.dohvatiSvePesticide();
        String naziv =nazivTextField.getText();
        StringBuilder sb=new StringBuilder();
        List<Pesticidi> filtriraniPesticidi = pesticidiList.stream().filter(s -> s.getNaziv().contains(naziv)).toList();
        if(filtriraniPesticidi.size()<pesticidiList.size())
            sb.append("Sortirano po nazivu. ");
        if(!kolicinaTextField.getText().isEmpty()) {
            try{
                if (!kolicinaTextField.getText().matches("[0-9]+")) {
                    throw new MilitarException("Nije unesen Integer");
                }
                int kolcina =Integer.parseInt(kolicinaTextField.getText());
                filtriraniPesticidi=pesticidiList.stream().filter(s->s.getMiliLitar().equals(kolcina))
                        .toList();
                sb.append("Sortirano po kolicni");
            }
              catch (MilitarException e) {
                upisGreskaKolicina();
                logger.error("Nije unese Integer",e);
            }

        }
        if(!vrstaChoiceBox.getSelectionModel().isEmpty()) {
            filtriraniPesticidi=pesticidiList.stream().filter(s -> s.getVrsta()
                    .equals(vrstaChoiceBox.getValue())).toList();
        sb.append("Sortirano po vrsti");
        }
        SerijalizacijaGeneric<String,String> serijalizacijaGeneric1=new SerijalizacijaGeneric<>("Pesticid: ",sb.toString());
        serijalizacijaGeneric1.ispis();
        pesticidiTableView.setItems(FXCollections.observableList(filtriraniPesticidi));

    }
    private void upisGreskaKolicina(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Krivi Unos");
        alert.setHeaderText("Kolicina treba biti samo broj!");
        alert.showAndWait();
    }
    private void upisGreska() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        StringBuilder text = new StringBuilder();
        if (nazivTextField.getText().isEmpty())
            text.append("Niste unijeli naziv\n");
        if (kolicinaTextField.getText().isEmpty())
            text.append("Niste unijeli kolicinu!\n");
        if (vrstaChoiceBox.getSelectionModel().isEmpty())
            text.append("Niste odabrali vrstu!\n");

        alert.setTitle("Pogrešan unos podataka");
        alert.setHeaderText("Molimo ispravite sljedeće pogreške:");
        alert.setContentText(text.toString());
        SerijalizacijaGeneric<String,String> serijalizacijaGeneric1=new SerijalizacijaGeneric<>("Pesticid greske: ",text.toString());
        serijalizacijaGeneric1.ispis();
        logger.info("Greska kod unosa Pesticida");
        alert.showAndWait();
    }



}

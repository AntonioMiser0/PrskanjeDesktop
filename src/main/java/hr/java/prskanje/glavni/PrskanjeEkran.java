package hr.java.prskanje.glavni;

import hr.java.prskanje.Baza.BazaPodataka;
import hr.java.prskanje.entiteti.Pesticidi;
import hr.java.prskanje.entiteti.Prskanje;
import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import hr.java.prskanje.entiteti.Zemlja;
import hr.java.prskanje.iznimke.BazaPodatakaException;
import hr.java.prskanje.iznimke.MilitarException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

public class PrskanjeEkran {
    @FXML
    ChoiceBox<String> zemljaidChoiceBox;
    @FXML
    ChoiceBox<String> pesticididChoiceBox;
    @FXML
    TextField kolicinaTextField;
    @FXML
    DatePicker datumPicker;
    @FXML
    Label label;
    @FXML
    private TableView<Prskanje> prskanjeTableView;
    @FXML
    private TableColumn<Prskanje, String> zemljaPrskanjeColumn;
    @FXML
    private TableColumn<Prskanje, String> pesticidiPrskanjeColumn;
    @FXML
    private TableColumn<Prskanje, String> kolicinaPrskanjeTableColumn;
    @FXML
    private TableColumn<Prskanje, String> datumPrskanjeTableColumn;

    List<Zemlja> zemljaList = new ArrayList<>();
    List<Pesticidi> pesticidList = new ArrayList<>();
    List<Prskanje> prskanjeList = new ArrayList<>();
    SerijalizacijaGeneric<String, Prskanje> serijalizacijaGeneric;
    private static final Logger logger = LoggerFactory.getLogger(PrskanjeEkran.class);

    public void initialize() {
        try {
            zemljaList = BazaPodataka.dohvatiSveZemlje();
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }
        try {
            pesticidList = BazaPodataka.dohvatiSvePesticide();
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }
        for (Zemlja z : zemljaList) {
            zemljaidChoiceBox.getItems().add(z.getNaziv());
        }

        for (Pesticidi z : pesticidList) {
            pesticididChoiceBox.getItems().add(z.getNaziv());
        }

        try {
            prskanjeList = BazaPodataka.dohvatiSveZemlja_Pesticid();
        } catch (BazaPodatakaException e) {
            logger.error("ERROR", e);
            e.getMessage();
            e.printStackTrace();
        }

        zemljaPrskanjeColumn
                .setCellValueFactory(cellData ->
                        new SimpleStringProperty(cellData.getValue().getZemlja().getNaziv()));
        pesticidiPrskanjeColumn
                .setCellValueFactory(cellData ->
                        new SimpleStringProperty(cellData.getValue().getPesticid().getNaziv()));
        kolicinaPrskanjeTableColumn
                .setCellValueFactory(cellData ->
                        new SimpleStringProperty(cellData.getValue().getKolicina().toString() + "ml"));

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
        prskanjeTableView.setRowFactory(tv -> {
            TableRow<Prskanje> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Prskanje rowData = row.getItem();
                    kolicinaTextField.setText(rowData.getKolicina().toString());
                    zemljaidChoiceBox.setValue(rowData.getZemlja().getNaziv());
                    pesticididChoiceBox.setValue(rowData.getPesticid().getNaziv());
                    datumPicker.setValue(rowData.getDatum());
                    System.out.println("Double click on: "+rowData.getZemlja().getNaziv());
                }
            });
            return row ;
        });
        Thread thread = new Thread(() -> {
            while (true) {
                if (!zemljaidChoiceBox.getSelectionModel().isEmpty()) {
                    try {
                        prskanjeList = BazaPodataka.dohvatiSveZemlja_Pesticid();
                    } catch (BazaPodatakaException e) {
                        throw new RuntimeException(e);
                    }
                    int kolicina = prskanjeList.stream().filter(x -> x.getZemlja().getNaziv().equals(zemljaidChoiceBox.getValue()))
                            .mapToInt(Prskanje::getKolicina).sum();
                    Platform.runLater(() -> {
                        labelIspis(" Na " + zemljaidChoiceBox.getValue() + " je potoršeno: " + kolicina + "ml");
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            while (true) {
                if (!zemljaidChoiceBox.getSelectionModel().isEmpty()
                        && !pesticididChoiceBox.getSelectionModel().isEmpty()) {
                    try {
                        zemljaList = BazaPodataka.dohvatiSveZemlje();
                        pesticidList = BazaPodataka.dohvatiSvePesticide();
                    } catch (BazaPodatakaException e) {
                        throw new RuntimeException(e);
                    }

                    Pesticidi pesticidi = pesticidList.stream().filter(x -> x.getNaziv()
                            .equals(pesticididChoiceBox.getValue())).toList().get(0);
                    Zemlja zemlja = zemljaList.stream().filter(x -> x.getNaziv()
                            .equals(zemljaidChoiceBox.getValue())).toList().get(0);
                    Prskanje prs = new Prskanje(0, zemlja, pesticidi, 0, null);
                    Platform.runLater(() -> {
                        labelIspis(". Na hektar zemlje: "+zemljaidChoiceBox.getValue()+ " se preporučuje potoršiti "+
                                prs.litarPoHektaru(prs)+"ml "+ pesticidi.getNaziv());
                    });
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
        thread2.start();

    }

    private synchronized void labelIspis(String s) {
        label.setText(s);
         }



    public void sortirajPrskanje() throws BazaPodatakaException {
        StringBuilder sb = new StringBuilder();
        List<Prskanje> filtriraniPrskanje = new ArrayList<>();

        if (!zemljaidChoiceBox.getSelectionModel().isEmpty()) {
            filtriraniPrskanje = prskanjeList.stream().filter(s -> s.getZemlja().getNaziv()
                    .equals((zemljaidChoiceBox.getValue()))).toList();
            sb.append("Sortirano po zemlji: ").append(zemljaidChoiceBox.getValue()).append(", ");
        }
        if (!pesticididChoiceBox.getSelectionModel().isEmpty()) {
            filtriraniPrskanje = prskanjeList.stream().filter(s -> s.getPesticid().getNaziv()
                    .equals((pesticididChoiceBox.getValue()))).toList();
            sb.append("Sortirano po pesticidu: ").append(pesticididChoiceBox.getValue()).append(", ");
        }
        if (!pesticididChoiceBox.getSelectionModel().isEmpty() && !zemljaidChoiceBox.getSelectionModel().isEmpty()) {
           pesticidList=BazaPodataka.dohvatiSvePesticide();
            filtriraniPrskanje = prskanjeList.stream().filter(s -> s.getPesticid().getNaziv()
                    .equals((pesticididChoiceBox.getValue()))).filter(s -> s.getZemlja().getNaziv()
                    .equals((zemljaidChoiceBox.getValue()))).toList();
        }
        if (datumPicker.getValue() != null) {
            filtriraniPrskanje = prskanjeList.stream().filter(s -> s.getDatum()
                    .isEqual(datumPicker.getValue())).toList();
            sb.append("Sortirano po datumu: ").append(datumPicker.getValue().toString()).append(", ");
        }

        if (!kolicinaTextField.getText().isEmpty()) {
            try {
                if (!kolicinaTextField.getText().matches("[0-9]+")) {
                    throw new MilitarException("Nije unesen Integer");
                }

            int kolcina = Integer.parseInt(kolicinaTextField.getText());
            filtriraniPrskanje = prskanjeList.stream().filter(s -> s.getKolicina().equals(kolcina))
                    .toList();
            sb.append("Sortirano po kolicni: ").append(kolcina);
            }
            catch (MilitarException e){
                upisGreskaKolicina();
                logger.error("Nije unese Integer",e);
            }
        }

        SerijalizacijaGeneric<String, String> serijalizacijaGeneric1 = new SerijalizacijaGeneric<>("Prskanje: ", sb.toString());
        serijalizacijaGeneric1.ispis();
        prskanjeTableView.setItems(FXCollections.observableList(filtriraniPrskanje));

    }

    public void unesiPrskanje() {
        try {
            if (zemljaidChoiceBox.getSelectionModel().isEmpty() || kolicinaTextField.getText().isEmpty() || pesticididChoiceBox.getSelectionModel().isEmpty()
            || datumPicker.getValue()==null) {
                upisGreska();
            } else if (!kolicinaTextField.getText().matches("[0-9]+")) {
                throw new MilitarException("Nije unesen Integer");
            } else {

                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Spremanje novog prskanja!");
                alert1.setContentText("Spremi?");
                ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
                alert1.showAndWait().ifPresent(type -> {
                    if (type == okButton) {
                        Pesticidi pesticid=null;
                        try {
                            zemljaList=BazaPodataka.dohvatiSveZemlje();
                            pesticidList=BazaPodataka.dohvatiSvePesticide();
                            pesticid=pesticidList.stream().filter(x->x.getNaziv()
                                    .equals(pesticididChoiceBox.getValue())).toList().get(0);
                            int kolicina=pesticid.getMiliLitar()-Integer.parseInt(kolicinaTextField.getText());
                            if(kolicina<0){
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Nedovoljno pesticida");
                                alert.setHeaderText("Trebate nabaviti: "+pesticid.getNaziv());
                                alert.showAndWait();
                                throw new RuntimeException("Nedovoljno Pesticida");
                            }
                            else
                                pesticid.setMiliLitar(kolicina);

                            Prskanje prskanje= new Prskanje(0,zemljaList.stream().filter(x->x.getNaziv()
                                    .equals(zemljaidChoiceBox.getValue())).toList().get(0),
                                    pesticid,
                                    Integer.parseInt(kolicinaTextField.getText()),
                                    datumPicker.getValue());
                            BazaPodataka.spremiNoviPrskanje(prskanje);
                            serijalizacijaGeneric = new SerijalizacijaGeneric<>("Dodan je novi: ", prskanje);
                            serijalizacijaGeneric.ispis();

                        } catch (BazaPodatakaException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            BazaPodataka.urediPesticid(pesticid.getId(),pesticid);
                            prskanjeList = BazaPodataka.dohvatiSveZemlja_Pesticid();
                        } catch (BazaPodatakaException e) {
                            throw new RuntimeException(e);
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješan unos podataka");
                        alert.setHeaderText("Spremanje podataka o novom prskanju");
                        alert.setContentText("Podaci o novom prskanju su uspješno dodani!");
                        alert.showAndWait();
                        prskanjeTableView.setItems(FXCollections.observableList((prskanjeList)));
                    }
                });

            }
        } catch (MilitarException e) {
            upisGreskaKolicina();
            logger.error("Nije unese Integer", e);
        }
    }
    public void obrisiPrskanje() {
        try {
            if (zemljaidChoiceBox.getSelectionModel().isEmpty() || kolicinaTextField.getText().isEmpty() || pesticididChoiceBox.getSelectionModel().isEmpty()
                    || datumPicker.getValue() == null) {
                upisGreska();
            } else if (!kolicinaTextField.getText().matches("[0-9]+")) {
                throw new MilitarException("Nije unesen Integer");
            } else {
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Brisanje Prskanja!");
                alert1.setContentText("Obriši?");
                ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
                alert1.showAndWait().ifPresent(type -> {
                    if (type == okButton) {
                        try {
                            prskanjeList = BazaPodataka.dohvatiSveZemlja_Pesticid();
                        } catch (BazaPodatakaException e) {
                            throw new RuntimeException(e);
                        }
                        long id = prskanjeList.stream().filter(x -> x.getZemlja().getNaziv().equals(zemljaidChoiceBox.getValue())
                                && x.getPesticid().getNaziv().equals(pesticididChoiceBox.getValue()) &&
                                x.getKolicina().equals(Integer.parseInt(kolicinaTextField.getText()))).toList().get(0).getId();
                        Prskanje prskanje = new Prskanje(id, zemljaList.stream().filter(x -> x.getNaziv()
                                .equals(zemljaidChoiceBox.getValue())).toList().get(0),
                                pesticidList.stream().filter(x -> x.getNaziv()
                                        .equals(pesticididChoiceBox.getValue())).toList().get(0),
                                Integer.parseInt(kolicinaTextField.getText()),
                                datumPicker.getValue());
                        serijalizacijaGeneric = new SerijalizacijaGeneric<>("Obrisalo se prskanje: ", prskanje);
                        serijalizacijaGeneric.ispis();
                        try {
                            BazaPodataka.obrisiPrskanje(prskanje);
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR kod brisanja", e);
                            throw new RuntimeException(e);
                        }

                        try {
                            prskanjeList = BazaPodataka.dohvatiSveZemlja_Pesticid();
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR kod brisanja", e);
                            throw new RuntimeException(e);
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješno brisanje podataka");
                        alert.setHeaderText("Obrisan prskanje");
                        alert.setContentText("Podaci o prskanju su uspješno obrisani!");
                        alert.showAndWait();
                        prskanjeTableView.setItems(FXCollections.observableList((prskanjeList)));
                    }
                });

            }
        }
            catch (MilitarException e){
            upisGreskaKolicina();
                logger.error("Nije unese Integer",e);
    }
    }
    public void urediPrskanje() {
        try {
            if (zemljaidChoiceBox.getSelectionModel().isEmpty() || kolicinaTextField.getText().isEmpty() || pesticididChoiceBox.getSelectionModel().isEmpty()
                    || datumPicker.getValue() == null) {
                upisGreska();
            } else if (!kolicinaTextField.getText().matches("[0-9]+")) {
                throw new MilitarException("Nije unesen Integer");
            } else {
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Uređivanje novog prskanja!");
                alert1.setContentText("Uredi?");
                ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
                alert1.showAndWait().ifPresent(type -> {
                    if (type == okButton) {
                        Prskanje prskanje = new Prskanje(0, zemljaList.stream().filter(x -> x.getNaziv()
                                .equals(zemljaidChoiceBox.getValue())).toList().get(0),
                                pesticidList.stream().filter(x -> x.getNaziv()
                                        .equals(pesticididChoiceBox.getValue())).toList().get(0),
                                Integer.parseInt(kolicinaTextField.getText()),
                                datumPicker.getValue());
                        serijalizacijaGeneric = new SerijalizacijaGeneric<>("Promijnjen je: ", prskanje);
                        serijalizacijaGeneric.ispis();
                        long id = prskanjeTableView.getSelectionModel().getSelectedItem().getId();
                        try {
                            BazaPodataka.urediPrskanje(prskanjeTableView.getSelectionModel().getSelectedItem().getId(), prskanje);
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR kod izmjene", e);
                            throw new RuntimeException(e);
                        }


                        try {
                            prskanjeList = BazaPodataka.dohvatiSveZemlja_Pesticid();
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR kod izmjene", e);
                            throw new RuntimeException(e);
                        }
                        serijalizacijaGeneric = new SerijalizacijaGeneric<>("Novi je: ", prskanjeList.stream()
                                .filter(x -> x.getId() == (id)).toList().get(0));
                        serijalizacijaGeneric.ispis();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješna izmjena podataka");
                        alert.setHeaderText("Izmjenjeni podataci o prskanju");
                        alert.setContentText("Podaci o novom prskanju su uspješno dodani!");
                        alert.showAndWait();
                        prskanjeTableView.setItems(FXCollections.observableList((prskanjeList)));
                    }
                });

            }
        }
           catch (MilitarException e){
                upisGreskaKolicina();
                logger.error("Nije unese Integer",e);
            }
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
        if (zemljaidChoiceBox.getSelectionModel().isEmpty())
            text.append("Niste odabrali zemlju\n");
        if (pesticididChoiceBox.getSelectionModel().isEmpty())
            text.append("Niste odabrali pesticid!\n");
        if (kolicinaTextField.getText().isEmpty())
            text.append("Niste unijeli kolicinu!\n");
        if (datumPicker.getValue()==null)
            text.append("Niste unijeli datum!\n");


        alert.setTitle("Pogrešan unos podataka");
        alert.setHeaderText("Molimo ispravite sljedeće pogreške:");
        alert.setContentText(text.toString());
        SerijalizacijaGeneric<String,String> serijalizacijaGeneric1=new SerijalizacijaGeneric<>("Prskanje greske: ",text.toString());
        serijalizacijaGeneric1.ispis();
        logger.info("Greska kod unosa Prskanja");
        alert.showAndWait();
    }


}

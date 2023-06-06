package hr.java.prskanje.glavni;

import hr.java.prskanje.Baza.BazaPodataka;
import hr.java.prskanje.entiteti.Pesticidi;
import hr.java.prskanje.entiteti.Polje;
import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import hr.java.prskanje.entiteti.Zemlja;
import hr.java.prskanje.iznimke.BazaPodatakaException;
import hr.java.prskanje.iznimke.HektarException;
import hr.java.prskanje.iznimke.MilitarException;
import hr.java.prskanje.iznimke.RadiobuttonException;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

public class ZemljaSort {
    @FXML
    private TextField nazivTextField;
    @FXML
    private TextField brHektarTextField;
    @FXML
    private TextField poljeNazivTextField;
    @FXML
    private TextField poljeUdaljenostTextField;
    @FXML
    private ChoiceBox<String> kulturaChoiceBox;
    @FXML
    private ChoiceBox<String> vrstaTlaChoiceBox;
    @FXML
    private RadioButton imaZakupRadioButton;
    @FXML
    private RadioButton nemaZakupRadioButton;
    @FXML
    private TableView<Zemlja> zemljaTableView;
    @FXML
    private TableColumn<Zemlja,String> nazivColumn;
    @FXML
    private TableColumn<Zemlja,String> brHektaraColumn;
    @FXML
    private TableColumn<Zemlja,String> poljeNazivColumn;
    @FXML
    private TableColumn<Zemlja,String> poljeUdaljenostColumn;
    @FXML
    private TableColumn<Zemlja,String> kulturaColumn;
    @FXML
    private TableColumn<Zemlja,String> zakupColumn;
    @FXML
    private TableColumn<Zemlja,String> vrstaTlaColumn;
    private OptionalLong maksimalniId;
    private List<Zemlja> zemljaList;
    private static final String[] vrsteKultureString={"psenica","soja","kukuruz"};
    private static final String[] vrstaTlaString={"plodno","pjeskovito"};
    SerijalizacijaGeneric<String, Zemlja> serijalizacijaGeneric;

    private static final Logger logger = LoggerFactory.getLogger(ZemljaSort.class);

    public void initialize() {

        vrstaTlaChoiceBox.getItems().addAll(vrstaTlaString);
        kulturaChoiceBox.getItems().addAll(vrsteKultureString);

        try {
            zemljaList = BazaPodataka.dohvatiSveZemlje();
        } catch (BazaPodatakaException e) {
            logger.error("Erorr",e);
            e.getMessage();
            e.printStackTrace();
        }
        nazivColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getNaziv()));
        brHektaraColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getHektara().toString()));
        poljeNazivColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getPolje().naziv()));
        poljeUdaljenostColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getPolje().udaljenost().toString()+"min"));
        kulturaColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getKultura()));
        zakupColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getZakup()));

    vrstaTlaColumn
                .setCellValueFactory(cellData->
                        new SimpleStringProperty(cellData.getValue().getVrstaTla()));

        zemljaTableView.setItems(FXCollections.observableList((zemljaList)));
        zemljaTableView.setRowFactory(tv -> {
            TableRow<Zemlja> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Zemlja rowData = row.getItem();
                    nazivTextField.setText(rowData.getNaziv());
                    brHektarTextField.setText(rowData.getHektara().toString());
                    poljeNazivTextField.setText(rowData.getPolje().naziv());
                    poljeUdaljenostTextField.setText(rowData.getPolje().udaljenost().toString()+"min");
                    kulturaChoiceBox.setValue(rowData.getKultura());
                    if(rowData.getZakup().equals("nema")) {
                        nemaZakupRadioButton.setSelected(true);
                    }
                    else if(rowData.getZakup().equals("ima"))
                        imaZakupRadioButton.setSelected(true);
                    vrstaTlaChoiceBox.setValue(rowData.getVrstaTla());
                    System.out.println("Double click on: "+rowData.getNaziv());
                }
            });
            return row ;
        });
    }
    public void zemljaSort() throws BazaPodatakaException {

            zemljaList=BazaPodataka.dohvatiSveZemlje();

        String naziv = nazivTextField.getText();
        String poljeNaz = poljeNazivTextField.getText();
        StringBuilder sb=new StringBuilder();
        List<Zemlja> filtriraneZemlje = zemljaList.stream()
                .filter(s -> s.getNaziv().contains(naziv))
                .filter(s -> s.getPolje().naziv().contains(poljeNaz)).toList();
        if(filtriraneZemlje.size()<zemljaList.size()){
            sb.append("Sortirno po nazivu. ");
        }
        if (!brHektarTextField.getText().isEmpty()) {
            try{
                if(!poljeUdaljenostTextField.getText().matches("[0-9]+")
                        || !brHektarTextField.getText().matches("[0-9]+"))
                    throw new HektarException("Krivo unesen Integer");
            filtriraneZemlje = zemljaList.stream()
                    .filter(s -> s.getHektara()
                            .equals(Long.parseLong(brHektarTextField.getText()))).toList();
            sb.append("Sortirno po broju hektara. ");
            }
            catch (HektarException e){
                upisGreskaKolicina();
                logger.error("Krivo unsen Integer");
            }
        }
        if (!poljeUdaljenostTextField.getText().isEmpty()) {
            try{
                if(!poljeUdaljenostTextField.getText().matches("[0-9]+")
                        || !brHektarTextField.getText().matches("[0-9]+"))
                    throw new HektarException("Krivo unesen Integer");
            filtriraneZemlje = zemljaList.stream()
                    .filter(s -> s.getPolje().udaljenost()
                            .equals(Integer.parseInt(poljeUdaljenostTextField.getText()))).toList();
            sb.append("Sortirno po udaljnosti od polja. ");
            }
            catch (HektarException e){
                upisGreskaKolicina();
                logger.error("Krivo unsen Integer");
            }
        }
        if(!kulturaChoiceBox.getSelectionModel().isEmpty()){
            filtriraneZemlje = zemljaList.stream()
                    .filter(s -> s.getKultura()
                            .equals(kulturaChoiceBox.getValue())).toList();
            sb.append("Sortirno po zasijani kulturi. ");
        }

        if(!vrstaTlaChoiceBox.getSelectionModel().isEmpty()){
            filtriraneZemlje = zemljaList.stream()
                    .filter(s -> s.getVrstaTla()
                            .equals(vrstaTlaChoiceBox.getValue())).toList();
            sb.append("Sortirno po vrsti tla. ");
        }
        if(!vrstaTlaChoiceBox.getSelectionModel().isEmpty()
        && !kulturaChoiceBox.getSelectionModel().isEmpty()){
            zemljaList=BazaPodataka.dohvatiSveZemlje();
            filtriraneZemlje = zemljaList.stream()
                    .filter(s -> s.getVrstaTla()
                            .equals(vrstaTlaChoiceBox.getValue())).filter(s -> s.getKultura()
                            .equals(kulturaChoiceBox.getValue())).toList();
        }
        if(imaZakupRadioButton.isSelected()){
            filtriraneZemlje = zemljaList.stream()
                    .filter(s -> s.getZakup()
                            .equals(imaZakupRadioButton.getText().toLowerCase())).toList();
            sb.append("Sortirne zemlje koje imaju zakup. ");
        }
        if(nemaZakupRadioButton.isSelected()) {
            filtriraneZemlje = zemljaList.stream()
                    .filter(s -> s.getZakup()
                            .equals(nemaZakupRadioButton.getText().toLowerCase())).toList();
            sb.append("Sortirne zemlje koje nemaju zakup. ");
        }
        SerijalizacijaGeneric<String,String> serijalizacijaGeneric1=new SerijalizacijaGeneric<>("Zemlje: ",sb.toString());
        serijalizacijaGeneric1.ispis();
        zemljaTableView.setItems(FXCollections.observableList(filtriraneZemlje));


    }
    public void upisiZemlja() {
        try{
            try {
                zemljaList = BazaPodataka.dohvatiSveZemlje();
                for (Zemlja zem:zemljaList) {
                    if (!nazivTextField.getText().isEmpty()) {
                        if(zem.getNaziv().equals(nazivTextField.getText())){
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Krivi Unos");
                            alert.setHeaderText("Ta zemlja već postoji!");
                            alert.showAndWait();
                            logger.error("Zemlja duplikat");
                        }
                    }
                }
            } catch (BazaPodatakaException e) {
                logger.error("ERROR",e);
                throw new RuntimeException(e);
            }

            if (nazivTextField.getText().isEmpty() || brHektarTextField.getText().isEmpty() || poljeNazivTextField.getText().isEmpty()
        || poljeUdaljenostTextField.getText().isEmpty() || kulturaChoiceBox.getSelectionModel().isEmpty() || vrstaTlaChoiceBox.getSelectionModel().isEmpty()
        || (!nemaZakupRadioButton.isSelected() && !imaZakupRadioButton.isSelected())) {
                upisGreska();
            try {
                if(!nemaZakupRadioButton.isSelected() && !imaZakupRadioButton.isSelected())
                    throw new RadiobuttonException("Nije odabran RadioButton");
            } catch (RadiobuttonException e) {
                logger.error("Nije odabran RadioButton",e);
                throw new RuntimeException(e);
            }
        } else if (!brHektarTextField.getText().matches("[0-9]+")||!poljeUdaljenostTextField.getText().matches("[0-9]+")) {
            throw new HektarException("Hektar ili udaljenost nije broja");
        }  else {
                maksimalniId = zemljaList.stream()
                        .mapToLong(Zemlja::getId).max();
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Spremanje nove zemlje!");
                alert1.setContentText("Spremi?");
                ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
                alert1.showAndWait().ifPresent(type -> {
                    if (type == okButton) {
                        Long id2 = maksimalniId.getAsLong();

                        try {
                            Polje polje=new Polje(poljeNazivTextField.getText(),Integer.parseInt(poljeUdaljenostTextField.getText()));
                            Zemlja zemlja = new Zemlja.ZemljaBuilder(id2, nazivTextField.getText(), Long.parseLong(brHektarTextField.getText()),
                                    polje, kulturaChoiceBox.getValue(), new HashMap<Integer, Pesticidi>(), vrstaTlaChoiceBox.getValue()).build();
                            if (imaZakupRadioButton.isSelected()) {
                                zemlja.setZakup("ima");
                            } else
                                zemlja.setZakup("nema");
                            serijalizacijaGeneric=new SerijalizacijaGeneric<>("Dodan je novi: ",zemlja);
                            serijalizacijaGeneric.ispis();
                            BazaPodataka.spremiNoviZemlja(zemlja);
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR",e);
                            throw new RuntimeException(e);
                        }
                        try {
                            zemljaList = BazaPodataka.dohvatiSveZemlje();
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR",e);
                            throw new RuntimeException(e);
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješan unos podataka");
                        alert.setHeaderText("Spremanje podataka o novoj zemlji");
                        alert.setContentText("Podaci o novoj zemlji su uspješno dodani!");
                        alert.showAndWait();
                        zemljaTableView.setItems(FXCollections.observableList((zemljaList)));
                    }
                });

        }
        } catch (HektarException e) {
           upisGreskaKolicina();
            logger.error("Nije unesen Integer",e);
            throw new RuntimeException(e);
        }
    }
    public void izmjeniZemlja() {
        try {
            if (nazivTextField.getText().isEmpty() || brHektarTextField.getText().isEmpty() || poljeNazivTextField.getText().isEmpty()
                    || poljeUdaljenostTextField.getText().isEmpty() || kulturaChoiceBox.getSelectionModel().isEmpty() || vrstaTlaChoiceBox.getSelectionModel().isEmpty()
                    || (!nemaZakupRadioButton.isSelected() && !imaZakupRadioButton.isSelected())) {
                try {
                    if (!nemaZakupRadioButton.isSelected() && !imaZakupRadioButton.isSelected())
                        throw new RadiobuttonException("Nije odabran RadioButton");
                    upisGreska();
                } catch (RadiobuttonException e) {
                    logger.error("Nije odabran RadioButton", e);
                    throw new RuntimeException(e);
                }

            } else if (!brHektarTextField.getText().matches("[0-9]+") || !poljeUdaljenostTextField.getText().matches("[0-9]+")) {
                throw new HektarException("Hektar ili udaljenost nije broja");
            } else {
                Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
                alert1.setTitle("Uređivanje nove zemlje!");
                alert1.setContentText("Uredi?");
                ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
                ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
                ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
                long id = zemljaTableView.getSelectionModel().getSelectedItem().getId();
                alert1.showAndWait().ifPresent(type -> {
                    if (type == okButton) {
                        try {
                            Polje polje=new Polje(poljeNazivTextField.getText(),Integer.parseInt(poljeUdaljenostTextField.getText()));
                            Zemlja zemlja = new Zemlja.ZemljaBuilder(Long.parseLong("0"), nazivTextField.getText(), Long.parseLong(brHektarTextField.getText()),
                                    polje, kulturaChoiceBox.getValue(), new HashMap<Integer, Pesticidi>(), vrstaTlaChoiceBox.getValue()).build();
                            if (imaZakupRadioButton.isSelected()) {
                                zemlja.setZakup("ima");
                            } else
                                zemlja.setZakup("nema");
                            serijalizacijaGeneric = new SerijalizacijaGeneric<>("Ureden je novi: ", zemlja);
                            serijalizacijaGeneric.ispis();
                            BazaPodataka.urediZemlja(zemljaTableView.getSelectionModel().getSelectedItem().getId(), zemlja);

                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR", e);
                            throw new RuntimeException(e);
                        }

                        try {
                            zemljaList = BazaPodataka.dohvatiSveZemlje();
                        } catch (BazaPodatakaException e) {
                            logger.error("ERROR", e);
                            throw new RuntimeException(e);
                        }
                        serijalizacijaGeneric = new SerijalizacijaGeneric<>("Novi je: ", zemljaList.stream()
                                .filter(x -> x.getId() == (id)).toList().get(0));
                        serijalizacijaGeneric.ispis();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješna izmjena podataka");
                        alert.setHeaderText("Izmjenjeni podataci o pesticidu");
                        alert.setContentText("Podaci o novom pesticidu su uspješno dodani!");
                        alert.showAndWait();
                        zemljaTableView.setItems(FXCollections.observableList((zemljaList)));
                    }
                });

            }
        }
        catch (HektarException e) {
            upisGreskaKolicina();
            logger.error("Nije unesen Integer",e);
            throw new RuntimeException(e);
        }
    }
    public void obrisiPesticid() {
       try{
        if (nazivTextField.getText().isEmpty() || brHektarTextField.getText().isEmpty() || poljeNazivTextField.getText().isEmpty()
                || poljeUdaljenostTextField.getText().isEmpty() || kulturaChoiceBox.getSelectionModel().isEmpty() || vrstaTlaChoiceBox.getSelectionModel().isEmpty()
                || (!nemaZakupRadioButton.isSelected() && !imaZakupRadioButton.isSelected()) ) {
            try {
                if(!nemaZakupRadioButton.isSelected() && !imaZakupRadioButton.isSelected())
                    throw new RadiobuttonException("Nije odabran RadioButton");
                upisGreska();
            } catch (RadiobuttonException e) {
                logger.error("Nije odabran RadioButton",e);
                throw new RuntimeException(e);
            }

        }
        else if (!brHektarTextField.getText().matches("[0-9]+") || !poljeUdaljenostTextField.getText().matches("[0-9]+")) {
            throw new HektarException("Hektar ili udaljenost nije broja");
        }
        else {
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Brisanje zemlje!");
            alert1.setContentText("Obriši?");
            ButtonType okButton = new ButtonType("Da", ButtonBar.ButtonData.YES);
            ButtonType noButton = new ButtonType("Ne", ButtonBar.ButtonData.NO);
            ButtonType cancelButton = new ButtonType("Odustani", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert1.getButtonTypes().setAll(okButton, noButton, cancelButton);
            alert1.showAndWait().ifPresent(type -> {
                if (type == okButton) {
                    try {
                        zemljaList=BazaPodataka.dohvatiSveZemlje();
                    } catch (BazaPodatakaException e) {
                        throw new RuntimeException(e);
                    }
                    Long id2 = zemljaList.stream().filter(x->x.getNaziv().equals(nazivTextField.getText()) &&
                            x.getVrstaTla().equals(vrstaTlaChoiceBox.getValue()) && x.getKultura().equals(kulturaChoiceBox.getValue()))
                            .toList().get(0).getId();

                    Polje polje=new Polje(poljeNazivTextField.getText(),Integer.parseInt(poljeUdaljenostTextField.getText()));
                    Zemlja zemlja = new Zemlja.ZemljaBuilder(id2,nazivTextField.getText(),Long.parseLong(brHektarTextField.getText()),
                            polje, kulturaChoiceBox.getValue(),new HashMap<Integer,Pesticidi>(),vrstaTlaChoiceBox.getValue()).build();
                    if(imaZakupRadioButton.isSelected()){
                        zemlja.setZakup("ima");
                    }
                    else
                        zemlja.setZakup("nema");
                    serijalizacijaGeneric=new SerijalizacijaGeneric<>("Obrisan je: ",zemlja);
                    serijalizacijaGeneric.ispis();
                    try {
                        BazaPodataka.obrisiZemlja(zemlja);
                    } catch (BazaPodatakaException e) {
                        logger.error("ERROR",e);
                        throw new RuntimeException(e);
                    }

                    try {
                        zemljaList = BazaPodataka.dohvatiSveZemlje();
                    } catch (BazaPodatakaException e) {
                        logger.error("ERROR",e);
                        throw new RuntimeException(e);
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Uspješno brisanje podataka");
                    alert.setHeaderText("Obrisan pesticid");
                    alert.setContentText("Podaci o novom pesticidu su uspješno obrisani!");
                    alert.showAndWait();
                    zemljaTableView.setItems(FXCollections.observableList((zemljaList)));
                }
            });

        }
    }
        catch (HektarException e) {
        upisGreskaKolicina();
        logger.error("Nije unesen Integer",e);
        throw new RuntimeException(e);
    }
    }
    private void upisGreska() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        StringBuilder text = new StringBuilder();
        if (nazivTextField.getText().isEmpty())
            text.append("Niste unijeli naziv\n");
        if (brHektarTextField.getText().isEmpty())
            text.append("Niste unijeli broj hektara!\n");
        if (poljeNazivTextField.getText().isEmpty())
            text.append("Niste unijeli naziv polja!\n");
        if (poljeUdaljenostTextField.getText().isEmpty())
            text.append("Niste unijeli udaljenost do polja!\n");
        if (kulturaChoiceBox.getSelectionModel().isEmpty())
            text.append("Niste odabrali kulturu!\n");
        if (!nemaZakupRadioButton.isSelected() && !imaZakupRadioButton.isSelected())
            text.append("Niste odabrali zakup!\n");
        if (vrstaTlaChoiceBox.getSelectionModel().isEmpty())
            text.append("Niste odabrali vrstu tla!\n");
        alert.setTitle("Pogrešan unos podataka");
        alert.setHeaderText("Molimo ispravite sljedeće pogreške:");
        alert.setContentText(text.toString());
        logger.info("Greska kod unosa");
        alert.showAndWait();
    }
    private void upisGreskaKolicina(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Krivi Unos");
        alert.setHeaderText("Udaljenost ili broj hektara treba biti samo broj!");
        alert.showAndWait();
    }





}

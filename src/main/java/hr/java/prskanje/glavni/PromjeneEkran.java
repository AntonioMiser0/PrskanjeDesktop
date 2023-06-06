package hr.java.prskanje.glavni;

import hr.java.prskanje.entiteti.SerijalizacijaGeneric;
import hr.java.prskanje.entiteti.UpisGeneric;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.List;

public class PromjeneEkran {
    @FXML
    private TextArea textArea;
    public void initialize(){
        SerijalizacijaGeneric<String, Object> sb = new SerijalizacijaGeneric<>("nekaj",new Object());
        StringBuilder stringBuilder=new StringBuilder();
        for (UpisGeneric<Object> o:sb.procitaneStavke())
        stringBuilder.append(o);
        textArea.setText(stringBuilder.toString());
    }
}

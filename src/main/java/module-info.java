module com.example.prskanje {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;


    opens hr.java.prskanje.glavni to javafx.fxml;
    exports hr.java.prskanje.glavni;
    exports hr.java.prskanje.entiteti;
    opens hr.java.prskanje.entiteti to javafx.fxml;
}
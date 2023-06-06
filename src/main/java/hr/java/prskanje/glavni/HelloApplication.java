package hr.java.prskanje.glavni;

import hr.java.prskanje.Baza.BazaPodataka;
import hr.java.prskanje.entiteti.Pesticidi;
import hr.java.prskanje.entiteti.Prskanje;
import hr.java.prskanje.entiteti.Zemlja;
import hr.java.prskanje.iznimke.BazaPodatakaException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HelloApplication extends Application {
    private static Stage mainStage;
    public static List<Zemlja> zemljaList;
    public static List<Pesticidi> pesticidList;
    public static List<Prskanje> prskanjeList;
    static {
        try {
            zemljaList = BazaPodataka.dohvatiSveZemlje();
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            pesticidList = BazaPodataka.dohvatiSvePesticide();
        } catch (BazaPodatakaException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void start(Stage stage) throws IOException {
        mainStage=stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.getIcons().add(new Image(Objects.requireNonNull(this.getClass().getResourceAsStream("/hr/java/prskanje/glavni/logo.png"))));
        stage.setTitle("Miser");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });

    }

    public static void main(String[] args) {
        launch();

    }
    public static Stage getMainStage(){
        return mainStage;
    }

}
package hr.java.prskanje.entiteti;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class VrijemeNit extends Thread{
    private LocalTime finalVr;
    private Label vrijemeLabel;
    private ImageView slikaView;
    private boolean x;

    static int sati;
    static int minute;

    private static final Logger logger = LoggerFactory.getLogger(VrijemeNit.class);

    public VrijemeNit(LocalTime finalVr, Label vrijemeLabel,boolean x,ImageView slikaView) {
        this.finalVr = finalVr;
        this.x=x;
        this.vrijemeLabel = vrijemeLabel;
        this.slikaView=slikaView;
    }

    public void run()
    {
        while (true) {
            LocalDateTime now = LocalDateTime.now();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                logger.warn("Greska kod dretve", e);
                System.out.println(e);
            }
            sati = finalVr.getHour() - now.getHour();
            minute = finalVr.getMinute() - now.getMinute();
            if (sati == 1 && (finalVr.getMinute() < now.getMinute())) {
                sati--;
            }
            if ((sati < 2 && sati >= 0) || (sati == 2 && minute <= 0) || (sati == 0 && minute >= 0)) {
                // System.out.println(sati+" "+minute);

                Platform.runLater(() -> {
                    if (minute < 0) {
                        vrijemeLabel.setText("Kiša će za: " + sati + "h i" + (60 - Math.abs(minute)) + "min");
                        System.out.println("Za par sati");
                    } else {
                        vrijemeLabel.setText("Kiša će za: " + sati + "h i" + (Math.abs(minute)) + "min");
                        System.out.println("Skoro da pada");
                    }
                    slikaView.setImage(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hr/java/prskanje/glavni/slike/oblak.png")))
                    );
                });
            }
            if (sati == 0 && minute == 0) {
                Platform.runLater(() -> {
                    vrijemeLabel.setText("Pada kiša");
                    System.out.println("Pada kisa");
                    slikaView.setImage(
                            new Image(Objects.requireNonNull(getClass().getResourceAsStream("/hr/java/prskanje/glavni/slike/kisa.png")))
                    );
                });

                try {
                    Thread.sleep((int) Math.floor(Math.random() * (10000000 - 10000 + 1) + 1000));
                } catch (Exception e) {
                    logger.warn("Greska kod dretve", e);
                    System.out.println(e);
                }
                vrijemeLabel.setText("Kiša je prošla");

            }
        }
    }

    }


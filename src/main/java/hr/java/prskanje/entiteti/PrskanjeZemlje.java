package hr.java.prskanje.entiteti;

import java.math.BigDecimal;
import java.util.List;

public sealed interface PrskanjeZemlje permits Prskanje  {

    default BigDecimal litarPoHektaru(Prskanje prskanje) {
        if (prskanje.getPesticid() instanceof Herbicidi) {
            return vratiLitar(prskanje).add(BigDecimal.valueOf(1).multiply(BigDecimal.valueOf(1000)));
        } else if(prskanje.getPesticid() instanceof Fungicidi) {
            return vratiLitar(prskanje).add(BigDecimal.valueOf(2).multiply(BigDecimal.valueOf(1000)));
        } else if (prskanje.getPesticid() instanceof Insekticidi) {
            return vratiLitar(prskanje).add(BigDecimal.valueOf(3).multiply(BigDecimal.valueOf(1000)));
        }
        return BigDecimal.valueOf(0);
        }
        default BigDecimal vratiLitar (Prskanje prskanje){
            switch (prskanje.getZemlja().getKultura()) {
                case "psenica":
                    if (prskanje.getZemlja().getVrstaTla().equals("pjeskovito")) {
                        return prskanje.getPesticid().jacina(prskanje.getPesticid()).add(BigDecimal.valueOf(5));
                    } else if (prskanje.getZemlja().getVrstaTla().equals("plodno")) {
                        return prskanje.getPesticid().jacina(prskanje.getPesticid()).add(BigDecimal.valueOf(3));
                    }
                case "kukuruz":
                    if (prskanje.getZemlja().getVrstaTla().equals("pjeskovito")) {
                        return prskanje.getPesticid().jacina(prskanje.getPesticid()).add(BigDecimal.valueOf(4));
                    } else if (prskanje.getZemlja().getVrstaTla().equals("plodno")) {
                        return prskanje.getPesticid().jacina(prskanje.getPesticid()).add(BigDecimal.valueOf(2));
                    }
                case "soja":
                    if (prskanje.getZemlja().getVrstaTla().equals("pjeskovito")) {
                        return prskanje.getPesticid().jacina(prskanje.getPesticid()).add(BigDecimal.valueOf(3));
                    } else if (prskanje.getZemlja().getVrstaTla().equals("plodno")) {
                        return prskanje.getPesticid().jacina(prskanje.getPesticid()).add(BigDecimal.valueOf(1));
                    }
            }
            return BigDecimal.valueOf(0);
        }
    }

package hr.java.prskanje.entiteti;

import java.math.BigDecimal;

public interface PesticidiInterface {
    default BigDecimal jacina(Pesticidi pesticid){
        return BigDecimal.valueOf(pesticid.getNaziv().length());
    }


}

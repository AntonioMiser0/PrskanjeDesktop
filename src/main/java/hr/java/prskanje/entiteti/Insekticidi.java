package hr.java.prskanje.entiteti;


public class Insekticidi extends Pesticidi implements PesticidiInterface{
    public Insekticidi(String naziv, int miliLitar, long id,String vrsta)
    {
        super(naziv, miliLitar, id,vrsta);
    }


}

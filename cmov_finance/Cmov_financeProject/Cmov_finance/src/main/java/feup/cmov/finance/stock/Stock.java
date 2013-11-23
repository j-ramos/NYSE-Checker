package feup.cmov.finance.stock;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tiago on 19-11-2013.
 */
public class Stock implements Serializable {
    public String acronym;
    public Integer ammount;
    private ArrayList<Value> history;
    public Float value;
    public Stock(String acronym, Integer amount)
    {
        this.acronym=acronym;
        this.ammount = amount;
        history=new ArrayList<Value>();
    }



    public void addAmmount(Integer ammount) {
        this.ammount += ammount;
    }

    public void subAmmount(Integer ammount) {
        this.ammount -= ammount;
    }

    public ArrayList<Value> getHistory() {
        return history;
    }
    public String getAcronym() { return acronym; }
    public void setHistory(ArrayList<Value> history) {
        this.history = history;
    }

    public Float getTotalValue() {
        return ammount*value;
    }
}

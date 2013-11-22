package feup.cmov.finance.stock;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Tiago on 19-11-2013.
 */
public class Stock implements Serializable {
    public String acronym;
    public Integer amount;
    private ArrayList<Value> history;
    public Float value;
    public Stock(String acronym, Integer amount)
    {
        this.acronym=acronym;
        this.amount = amount;
        history=new ArrayList<Value>();
    }



    public void addAmmount(Integer ammount) {
        this.amount += ammount;
    }

    public void subAmmount(Integer ammount) {
        this.amount -= ammount;
    }

    public ArrayList<Value> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<Value> history) {
        this.history = history;
    }
}

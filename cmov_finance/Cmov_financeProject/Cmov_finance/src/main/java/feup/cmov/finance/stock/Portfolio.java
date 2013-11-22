package feup.cmov.finance.stock;

import java.util.ArrayList;

/**
 * Created by Tiago on 19-11-2013.
 */
public class Portfolio {

    private ArrayList<Stock> stocks;
    public Portfolio()
    {
        stocks = new ArrayList<Stock>();
    }

    public void addStock(Stock s)
    {
        stocks.add(s);
    }
    public ArrayList<Stock> getStocks(){
        return stocks;
    }

    public void subtractShared(String name, int ammount)
    {
        for (Stock s : stocks)
        {
            if(s.acronym.equals(name))
            {
                s.subAmmount(ammount);
            }
        }
    }

    public void addShared(String name, int ammount)
    {
        for (Stock s : stocks)
        {
            if(s.acronym.equals(name))
            {
                s.addAmmount(ammount);
            }
        }
    }

    public Float getValue(){
        return new Float(0);
    }

    public Float getValue(Stock stock){
        return new Float(0);
    }
}

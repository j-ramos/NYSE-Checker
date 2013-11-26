package feup.cmov.finance.stock;

import android.graphics.Color;
import android.os.Handler;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import feup.cmov.finance.connection.Network;
import feup.cmov.finance.connection.WebServiceCallRunnable;

/**
 * Created by Tiago on 19-11-2013.
 */
public class Stock implements Serializable {
    public String acronym;
    public Integer ammount;
    public int color;
    private ArrayList<Value> history;
    public Float value;
    public Stock(String acronym, Integer amount)
    {
        this.acronym=acronym;
        this.ammount = amount;
        history=new ArrayList<Value>();


        int start = Color.rgb(0, 0, 0);
        int end = Color.rgb(255,255,255);
        Random rand = new Random();
        color =rand.nextInt(end-start) + start;

    }

    public void populateHistory() {

        final Handler h = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new RetriveData(h).run();
            }
        });
        thread.start();
    }

    public class RetriveData extends WebServiceCallRunnable {
        public RetriveData(Handler h)
        {
            super(h);
        }

        @Override
        public void run() {
            Calendar now = GregorianCalendar.getInstance();
            Calendar month  = GregorianCalendar.getInstance();
            now.add(Calendar.MONTH, -1);
            Network network = new Network();
            String query = "http://ichart.finance.yahoo.com/table.txt?";
            String initialyear = "&c=" + now.get(Calendar.YEAR);
            String initialmonth = "a=" + now.get(Calendar.MONTH);
            String initialday = "&b=" + now.get(Calendar.DAY_OF_MONTH);
            String finalyear = "&f=" + month.get(Calendar.YEAR);
            String finalmonth = "&d=" + month.get(Calendar.MONTH);
            String finalday = "&e=" + month.get(Calendar.DAY_OF_MONTH);
            String name = "&s=" + acronym;
            query = query  + initialmonth + initialday + initialyear + finalmonth + finalday + finalyear + name + "&g=d";
            final String res = network.get(query);
            String[] sStocks = res.split("\\r?\\n");
            for(int i=1; i < sStocks.length; i++)
            {
                //Date,Open,High,Low,Close,Volume,Adj Close
                String[] t = sStocks[i].split(",");
                Date date = Date.valueOf(t[0]);

                Float f= Float.valueOf(t[4].trim()).floatValue();
                Value value = new Value(f, date);
                history.add(value);

            }

        }
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

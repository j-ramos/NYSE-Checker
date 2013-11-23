package feup.cmov.finance.stock;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tiago on 19-11-2013.
 */
public class Portefolio extends Application{

    private HashMap<String, Stock> stocks;
    private String filename;
    public Portefolio()
    {


    }

    @Override
    public void onCreate() {
        Log.d("teste", "asd");
        super.onCreate();

        FileInputStream inputStream;
        filename = "dataPortefolio";
        try {
            inputStream = FileInputStream(filename);
            ObjectInputStream s = new ObjectInputStream(inputStream);
            stocks = (HashMap<String,Stock>)s.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("teste", "q1eqw");
    }

    @Override
    public void onTerminate() {
        File file = new File(getApplicationContext().getFilesDir(), filename);
        super.onTerminate();
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(stocks);
            outputStream.close();
            Log.d("teste", "gravou");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public HashMap<String, Stock> getStock()
    {
        return stocks;
    }
    public void addStock(Stock s)
    {
        stocks.put(s.acronym, s);
    }
    public ArrayList<Stock> getStocks(){
        return new ArrayList<Stock>(stocks.values());
    }

    public void subtractShared(String name, int ammount)
    {
        Stock s = stocks.get(name);
        s.subAmmount(ammount);
        stocks.put(name, s);
    }

    public void addShared(String name, int ammount)
    {
        Stock s = stocks.get(name);
        s.addAmmount(ammount);
        stocks.put(name, s);
    }
}

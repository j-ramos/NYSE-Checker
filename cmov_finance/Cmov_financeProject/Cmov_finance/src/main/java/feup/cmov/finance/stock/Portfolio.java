package feup.cmov.finance.stock;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
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
public class Portfolio extends Application{

    private HashMap<String, Stock> stocks;
    private String filename;
    public Portfolio()
    {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        stocks = new HashMap<String, Stock>();
        filename = "/saveData";
        File yourFile = new File( Environment.getExternalStorageDirectory()+ filename);
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(yourFile);
            ObjectInputStream s = new ObjectInputStream(inputStream);
            stocks = (HashMap<String,Stock>)s.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        super.onTerminate();
        FileOutputStream outputStream;
        filename = "/saveData";
        File yourFile = new File( Environment.getExternalStorageDirectory()+ filename);
        try {
            if(!yourFile.exists()) {
                yourFile.createNewFile();
            }
            outputStream = new FileOutputStream(yourFile, false);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(stocks);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public HashMap<String, Stock> getStocksHashMap()
    {
        return stocks;
    }

    public ArrayList<Stock> getStocks(){
        return new ArrayList<Stock>(stocks.values());
    }

    public int getWalletSize() {
        return stocks.size();
    }

    public void decreaseStockAmmount(String name, int ammount)
    {
        Stock s = stocks.get(name);
        s.subAmmount(ammount);
        stocks.put(name, s);
    }

    public void increaseStockAmmount(String name, int ammount)
    {
        Stock s = stocks.get(name);
        s.addAmmount(ammount);
        stocks.put(name, s);
    }

    public void createStock(String name, int ammount) {
        Stock s = new Stock(name,ammount);
        stocks.put(name, s);
    }
}

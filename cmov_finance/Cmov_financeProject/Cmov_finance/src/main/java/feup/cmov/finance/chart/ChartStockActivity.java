package feup.cmov.finance.chart;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import feup.cmov.cmov_finance.R;
import feup.cmov.finance.connection.Network;
import feup.cmov.finance.connection.WebServiceCallRunnable;
import feup.cmov.finance.stock.Stock;
import feup.cmov.finance.stock.Value;

public class ChartStockActivity extends Activity {

    public String getName() {
        return "Evolução dos ultimos 30 dias.";
    }
    public String getDesc() {
        return "Valor da acção durante 30dias";
    }
    private Stock stock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_stock);
        Intent intent = getIntent();
        stock = (Stock) intent.getSerializableExtra("stock");
        final Handler h = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new RetriveData(h).run();
            }
        });
        thread.start();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.chart_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public class RetriveData extends WebServiceCallRunnable{
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
            String name = "&s=" + stock.acronym;
            query = query  + initialmonth + initialday + initialyear + finalmonth + finalday + finalyear + name + "&g=d";
            final String res = network.get(query);
            String[] sStocks = res.split("\\r?\\n");
            ArrayList<Value> history= new ArrayList<Value>();
            for(int i=0; i < sStocks.length; i++)
            {
                //Date,Open,High,Low,Close,Volume,Adj Close
                String[] t = sStocks[i].split(",");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = sdf.parse(t[0]);
                    Float f= Float.valueOf(t[4].trim()).floatValue();
                    Value value = new Value(f, date);
                    history.add(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            stock.setHistory(history);
        }
    }
}

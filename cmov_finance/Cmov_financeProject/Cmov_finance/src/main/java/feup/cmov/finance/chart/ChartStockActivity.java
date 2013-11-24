package feup.cmov.finance.chart;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.LinearLayout;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.Random;

import feup.cmov.cmov_finance.R;
import feup.cmov.finance.connection.Network;
import feup.cmov.finance.connection.WebServiceCallRunnable;
import feup.cmov.finance.stock.Portfolio;
import feup.cmov.finance.stock.Stock;
import feup.cmov.finance.stock.Value;


import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;

public class ChartStockActivity extends Activity {

    public String getName() {
        return "Evolução dos ultimos 30 dias.";
    }
    public String getDesc() {
        return "Valor da acção durante 30dias";
    }
    private Stock stock;
    private GraphicalView mChart;
    private XYMultipleSeriesRenderer mRenderer;
    private CategorySeries mCurrentSeries;
    private XYMultipleSeriesDataset mDataset;
    protected Portfolio portfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_stock);
        portfolio = (Portfolio)getApplication();
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
    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);


        if (mChart == null) {
            initChart();
            //mChart = ChartFactory.getBarChartIntent(this, mDataset, mRenderer, 0.3f);
            //mChart = ChartFactory.getPieChartView(this, mCurrentSeries, renderer);
            //mChart = ChartFactory.getPieChartView(this, mCurrentSeries, renderer);
            layout.addView(mChart);
        } else {
            mChart.repaint();
        }

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

    protected XYMultipleSeriesRenderer buildBarRenderer(int nColors) {

        int interval = 360 / nColors;
        int start = Color.rgb(0,0,0);
        int end = Color.rgb(255,255,255);
        Random rand = new Random();
        //  int[] col = new int[] {};
        ArrayList<Integer> colors = new ArrayList();
        Log.d("Interval", ((Integer) interval).toString());
        for (int x = 0; x < nColors; x++) {
            int R;
            if(x == 0)
                R =rand.nextInt(end-start) + start;
            else
                R = colors.get(0);

            while (colors.contains(R)) {
                R = rand.nextInt(end-start) + start;
            }
            // col[x] =R;
            colors.add(R);
        }


        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        int length = colors.size();
        for (int i = 0; i < length; i++) {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors.get(i));
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }


    private void initChart() {


        mCurrentSeries = new CategorySeries("Portfolio");
        addData();
        mRenderer = buildBarRenderer(portfolio.getWalletSize()); // gera as cores para cada acção

        mRenderer.setChartTitle("");

        mRenderer.setPanEnabled(true);// Disable User Interaction
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setShowLegend(false);
        mRenderer.setInScroll(false);
        mRenderer.setStartAngle(180);
        mRenderer.setZoomRate(1);
        mRenderer.setLabelsTextSize(22);
        mRenderer.setOrientation(XYMultipleSeriesRenderer.Orientation.VERTICAL);
        mRenderer.setYLabels(10);

    }



    private void addData() {
        ArrayList<Value> history = stock.getHistory();
        Integer amount = stock.ammount;
        for(int i =0; i < history.size(); i++)
        {
            Value v= history.get(i);
            Double d = new Double(amount * v.getValue());
            mCurrentSeries.add(v.getDate().toString(), d);
        }
    }

    // Chart vals
    String chart_type;
    protected DefaultRenderer buildCategoryRenderer(int ncolors) {
        int interval = 360 / ncolors;
        int start = Color.rgb(0,0,0);
        int end = Color.rgb(255,255,255);
        Random rand = new Random();
        //  int[] col = new int[] {};
        ArrayList<Integer> colors = new ArrayList();
        Log.d("Interval", ((Integer) interval).toString());
        for (int x = 0; x < ncolors; x++) {
            int R;
            if(x == 0)
                R =rand.nextInt(end-start) + start;
            else
                R = colors.get(0);

            while (colors.contains(R)) {
                R = rand.nextInt(end-start) + start;
            }
            // col[x] =R;
            colors.add(R);
        }
        // colors = col;

        DefaultRenderer renderer = new DefaultRenderer();
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }
}

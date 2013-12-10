package feup.cmov.finance.chart;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

import feup.cmov.cmov_finance.R;
import feup.cmov.finance.connection.Network;
import feup.cmov.finance.connection.WebServiceCallRunnable;
import feup.cmov.finance.share.PortefolioActivity;
import feup.cmov.finance.stock.Portfolio;
import feup.cmov.finance.stock.Stock;
import feup.cmov.finance.stock.Value;
import feup.cmov.finance.util.Pair;

/**
 * Created by Joel on 24-11-2013.
 */
public class StockEvolutionActivity extends Activity {
    private static final int SERIES_NR = 2;

    private GraphicalView mChart;
    // Creating a dataset to hold each series
    private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

    protected Portfolio portfolio;


    HashMap<String, Pair<XYSeriesRenderer, XYSeries>> dataInGraph = new HashMap<String, Pair<XYSeriesRenderer, XYSeries>>();
    HashMap<String, Integer> isVisible = new HashMap<String, Integer>();
    private View activityLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_evolution_activity);

        portfolio = (Portfolio)getApplication();
        activityLayout = (LinearLayout) findViewById(R.id.stockEvolutionGraphLayout);
        TextView title = (TextView)findViewById(R.id.tv_title);
        title.setText("Portefolio");
        // populate stocks with history data
        // and then create chart
        final Handler h = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new RetriveData(h).run();
            }
        });
        thread.start();

       /* Intent intent = ChartFactory.getBarChartIntent(this, getTruitonBarDataset(), renderer, BarChart.Type.DEFAULT);
        startActivity(intent);*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.truiton_achart_engine, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(StockEvolutionActivity.this, PortefolioActivity.class);
                startActivity(intent);
                overridePendingTransition(R.animator.slide_in_right,R.animator.slide_out_left);
                return true;
        }
        return false;
    }

    private void createChart() {
        int[] x = new int[30];
        Arrays.fill(x, 0);

        //int[] values  = portfolio.getWalletValue();
        // Creating an  XYSeries for Income
        ArrayList<Stock> stocks = portfolio.getStocks();

        ArrayList<String> acronymList = new ArrayList<String>();
        Random rand = new Random();
        for(int i=0;i<+stocks.size();i++){
            XYSeries stocksValueSeries = new XYSeries(stocks.get(i).getAcronym());
            for(int j = 0; j < stocks.get(i).getHistory().size(); j++) {
                stocksValueSeries.add(x[j], stocks.get(i).getHistory().get(j).getValue());
            }
            // Adding Income Series to the dataset
            dataset.addSeries(stocksValueSeries);

            acronymList.add(stocks.get(i).getAcronym());
            //gridLabels.addView(but);


            // Creating XYSeriesRenderer to customize incomeSeries
            XYSeriesRenderer stocksValueRenderer = new XYSeriesRenderer();
            // http://www.colorhunter.com/palette/1016173

            stocksValueRenderer.setColor(stocks.get(i).color);
            stocksValueRenderer.setPointStyle(PointStyle.CIRCLE);
            stocksValueRenderer.setFillPoints(true);
            stocksValueRenderer.setLineWidth(3);
            stocksValueRenderer.setDisplayChartValues(true);

            multiRenderer.addSeriesRenderer(stocksValueRenderer);

          /*  HashMap<XYMultipleSeriesRenderer, XYMultipleSeriesDataset> pairHash = new HashMap<XYMultipleSeriesRenderer, XYMultipleSeriesDataset>();
            pairHash.put(multiRenderer, dataset);*/
            dataInGraph.put(stocks.get(i).getAcronym(), new Pair(stocksValueRenderer, stocksValueSeries));
            isVisible.put(stocks.get(i).getAcronym(), 1);
        }



        ImageView popupIcon = (ImageView) findViewById(R.id.popupIcon);
        //gridLabels.setAdapter(new ToggleButtonAdapter(this.getApplicationContext(), acronymList));
        popupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DialogFragment newFragment = new StocksSelectionDialogFragment();
                newFragment.show(getFragmentManager(), "stocks");
                //newFragment.show(Fragment.getSupportFragmentManager(), "missiles");
                //mChart.repaint();

            }
        });

       // multiRenderer.addSeriesRenderer(expenseRenderer);

    }

    // http://developer.android.com/guide/topics/ui/dialogs.html



    public class StocksSelectionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ArrayList mSelectedItems = new ArrayList();  // Where we track the selected items
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final String[] acronymsArray = isVisible.keySet().toArray(new String[0]);
            //final boolean[] selectedArray = isVisible.values().toArray(new Integer[0]);

            final boolean[] selectedArray = new boolean[acronymsArray.length];
            for(int i = 0; i < acronymsArray.length; i++) {
                selectedArray[i] = (isVisible.get(acronymsArray[i]) == 1);
            }

            // Set the dialog title
            builder.setTitle(R.string.pick_stocks)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(acronymsArray,
                            selectedArray,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the visible items
                                        isVisible.put(acronymsArray[which], 1);
                                        Log.d("Visibility change", acronymsArray[which] + " visible");
                                    } else {
                                        // Else, if the item is not checked
                                        isVisible.put(acronymsArray[which], 0);
                                        Log.d("Visibility change", acronymsArray[which] + " invisible");
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog

                            dataset = new XYMultipleSeriesDataset();
                            multiRenderer = new XYMultipleSeriesRenderer();
                            for (int i = 0; i < acronymsArray.length; i++) {
                                if (isVisible.get(acronymsArray[i]) == 1) {
                                    dataset.addSeries(dataInGraph.get(acronymsArray[i]).getValue());

                                    dataInGraph.get(acronymsArray[i]).getKey().setColor(portfolio.getStocksHashMap().get(acronymsArray[i]).color);
                                    dataInGraph.get(acronymsArray[i]).getKey().setPointStyle(PointStyle.CIRCLE);
                                    dataInGraph.get(acronymsArray[i]).getKey().setFillPoints(true);
                                    dataInGraph.get(acronymsArray[i]).getKey().setLineWidth(3);
                                    dataInGraph.get(acronymsArray[i]).getKey().setDisplayChartValues(true);

                                    multiRenderer.addSeriesRenderer(dataInGraph.get(acronymsArray[i]).getKey());
                                }

                            }
                            multiRenderer.setXLabels(BIND_AUTO_CREATE);
                            multiRenderer.setYTitle("Combined ammount in Euros");

                            multiRenderer.setBackgroundColor(Color.argb(0x00, 0x01, 0x01, 0x01));
                            multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
                            multiRenderer.setLabelsColor(Color.BLACK);
                            multiRenderer.setAxesColor(Color.BLACK);
                            multiRenderer.setXLabelsColor(Color.BLACK);


                            multiRenderer.setPanEnabled(false);// Disable User Interaction
                            multiRenderer.setShowLegend(true);
                            multiRenderer.setInScroll(false);
                            multiRenderer.setAntialiasing(true);
                            multiRenderer.setClickEnabled(false);

                            mChart = ChartFactory.getLineChartView(getBaseContext(), dataset, multiRenderer);
                            mChart.setBackgroundColor(Color.WHITE);
                            mChart.setZoomRate(1);

                            LinearLayout barChartLayout = (LinearLayout) findViewById(R.id.bar_chart);
                            barChartLayout.removeAllViews();
                            barChartLayout.addView(mChart);
                            Log.d("REPATIN", "SHOULD REPAINT THE GRAPHIC NOW!!");
                            mChart.repaint();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // does nothing!
                        }
                    });

            return builder.create();
        }
    }


    public class RetriveData extends WebServiceCallRunnable {
        public RetriveData(Handler h)
        {
            super(h);
        }

        @Override
        public void run() {
            ArrayList<Stock> stocks  = portfolio.getStocks();
            for(int j = 0; j< stocks.size(); j++) {
                if(stocks.get(j).getHistory().size()==0)
                {
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
                    String name = "&s=" +  stocks.get(j).acronym;
                    query = query  + initialmonth + initialday + initialyear + finalmonth + finalday + finalyear + name + "&g=d";
                    final String res = network.get(query);
                    String[] sStocks = res.split("\\r?\\n");
                    ArrayList<Value> history= new ArrayList<Value>();
                    for(int i=1; i < sStocks.length; i++)
                    {
                        //Date,Open,High,Low,Close,Volume,Adj Close
                        String[] t = sStocks[i].split(",");
                        Date date = Date.valueOf(t[0]);

                        Float f= Float.valueOf(t[4].trim()).floatValue();
                        Value value = new Value(f, date);
                        history.add(value);

                    }
                    stocks.get(j).setHistory(history);
                    Log.d("INFO", stocks.get(j).getAcronym() + " populated" + ", size of history" + stocks.get(j).getHistory().size());
                }
            }

            handler_.post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout status = (LinearLayout) findViewById(R.id.stock_evolution_status);
                    status.setVisibility(View.GONE);

                    LinearLayout barChartLayout = (LinearLayout) findViewById(R.id.bar_chart);
                    barChartLayout.setVisibility(View.VISIBLE);
                    if (mChart == null) {
                        createChart();

                        // Creating a XYMultipleSeriesRenderer to customize the whole chart
                        multiRenderer.setXLabels(BIND_AUTO_CREATE);
                        multiRenderer.setYTitle("Combined ammount in Euros");
                        multiRenderer.setBackgroundColor(Color.argb(0x00, 0x01, 0x01, 0x01));
                        multiRenderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
                        multiRenderer.setLabelsColor(Color.BLACK);
                        multiRenderer.setAxesColor(Color.BLACK);
                        multiRenderer.setXLabelsColor(Color.BLACK);


                        multiRenderer.setPanEnabled(false);// Disable User Interaction
                        multiRenderer.setShowLegend(true);
                        multiRenderer.setInScroll(false);
                        multiRenderer.setAntialiasing(true);
                        multiRenderer.setClickEnabled(false);


                        mChart = ChartFactory.getLineChartView(getBaseContext(), dataset, multiRenderer);
                        mChart.setZoomRate(1);



                        barChartLayout.addView(mChart);
                        mChart.repaint();
                    } else {
                        mChart.repaint();
                    }
                }
            });
        }
    }


}
package feup.cmov.finance.chart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;
import java.util.Random;

import feup.cmov.cmov_finance.R;
import feup.cmov.finance.stock.Portfolio;
import feup.cmov.finance.stock.Stock;

/**
 * Created by Joel on 22-11-2013.
 */
public class PortfolioChartStatisticsActivity extends Activity {
    private GraphicalView mChart;
   // int[] colors = new int[] { Color.RED, Color.YELLOW, Color.BLUE };
    private  DefaultRenderer renderer;
    private CategorySeries mCurrentSeries;
    protected Portfolio portfolio;

    // layout
    private int selectedPie = 0;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;


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

    private void initChart() {


        mCurrentSeries = new CategorySeries("Portfolio");
        addData();

        renderer = buildCategoryRenderer(portfolio.getWalletSize()); // gera as cores para cada acção

        renderer.setChartTitle("");

        renderer.setPanEnabled(false);// Disable User Interaction
        renderer.setLabelsColor(Color.BLACK);
        renderer.setShowLegend(false);
        //renderer.setLegendTextSize(20);
        renderer.setInScroll(false);
        renderer.setStartAngle(180);
        renderer.setAntialiasing(true);
        renderer.setClickEnabled(false);
        renderer.setSelectableBuffer(portfolio.getWalletSize());

        renderer.setLabelsTextSize(22);

    }

    private void addData() {
        ArrayList<Stock> stocks = portfolio.getStocks();
        for(int i = 0 ; i < stocks.size(); i++) {
            mCurrentSeries.add(stocks.get(i).getAcronym(), stocks.get(i).getTotalValue());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        portfolio = (Portfolio)getApplication();

        Intent intent = getIntent();
        chart_type = intent.getStringExtra("type"); //if it's a string you stored.
    }

    protected void onResume() {
        super.onResume();
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);


        if (mChart == null) {
            initChart();
           // mChart = ChartFactory.getCubeLineChartView(this, mDataset, mRenderer, 0.3f);
            mChart = ChartFactory.getPieChartView(this, mCurrentSeries, renderer);
            gestureDetector = new GestureDetector(this, new MyGestureDetector());
            mChart.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });


            mChart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            //renderer.getSeriesRendererAt(0).setHighlighted(true);

            mChart.repaint();
            setSelectedPie(selectedPie);


            layout.addView(mChart);
        } else {
            mChart.repaint();
        }

    }
    private void removePieHiglights() {
        for (int i = 0; i < portfolio.getWalletSize(); i++) {
            renderer.getSeriesRendererAt(i).setHighlighted(false);
            renderer.getSeriesRendererAt(i).setDisplayChartValuesDistance(BIND_ABOVE_CLIENT);
        }

    }
    private void setSelectedPie(int pie) {
        selectedPie = pie;
        renderer.getSeriesRendererAt(selectedPie).setHighlighted(true);
        renderer.getSeriesRendererAt(selectedPie).setStroke(BasicStroke.SOLID);
        mChart.repaint();

        Log.d("SELECTED PIE: ", selectedPie + " / " + portfolio.getStocks().get(pie).getAcronym());

        final TextView textStockName = (TextView) findViewById(R.id.stockName);
        textStockName.setText(portfolio.getStocks().get(selectedPie).getAcronym());
        textStockName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        final TextView textStockValue = (TextView) findViewById(R.id.stockValue);
        textStockValue.setText(((Float) portfolio.getStocks().get(selectedPie).getTotalValue()).toString() + " €");
        textStockName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


        final Button buttonCheckStats = (Button) findViewById(R.id.buttonCheckStats);
        buttonCheckStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Stock s = portfolio.getStocks().get(selectedPie);
                Intent intent = new Intent(PortfolioChartStatisticsActivity.this, ChartStockActivity.class);
                intent.putExtra("stock", s);
                startActivity(intent);
            }
        });
    }
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {

                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // LEFT SWIPE

                    renderer.getSeriesRendererAt(selectedPie).setHighlighted(false);
                    if(selectedPie < portfolio.getWalletSize() - 1)
                        selectedPie++;
                    else
                        selectedPie = 0;

                }  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                    renderer.getSeriesRendererAt(selectedPie).setHighlighted(false);
                    if(selectedPie > 0)
                        selectedPie--;
                    else
                        selectedPie = portfolio.getWalletSize() - 1;
                }
                setSelectedPie(selectedPie);
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

    }
}
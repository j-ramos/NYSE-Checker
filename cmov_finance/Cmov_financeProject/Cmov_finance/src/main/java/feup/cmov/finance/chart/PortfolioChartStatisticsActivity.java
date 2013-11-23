package feup.cmov.finance.chart;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
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

        renderer.setPanEnabled(true);// Disable User Interaction
        renderer.setLabelsColor(Color.BLACK);
        renderer.setShowLegend(false);
        //renderer.setLegendTextSize(20);
        renderer.setInScroll(false);
        renderer.setStartAngle(180);
        renderer.setZoomRate(1);

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
            layout.addView(mChart);
        } else {
            mChart.repaint();
        }

    }
}
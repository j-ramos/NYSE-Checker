package feup.cmov.finance.share;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import feup.cmov.cmov_finance.R;
import feup.cmov.finance.stock.Portfolio;
import feup.cmov.finance.stock.Stock;

public class SearchActivity extends Activity {
    private HashMap<String, Stock> stocks;
    private PortfolioAdapter adapter;
    private ArrayList<Stock> stockArray;
    private ListView listView;
    protected Portfolio portfolio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        portfolio=(Portfolio) getApplication();
        stocks = portfolio.getStocksHashMap();
        stockArray=new ArrayList<Stock>();
        handleIntent(getIntent());
        adapter = new PortfolioAdapter(this , R.layout.list_item, stockArray);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.empty));



    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for(String key : stocks.keySet())
            {
                if(key.contains(query.toUpperCase()))
                {
                    stockArray.add(stocks.get(key));
                }
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public class PortfolioAdapter extends ArrayAdapter<Stock> {

        private int layoutResourceId;
        private ArrayList<Stock> data;
        private Context context;

        public PortfolioAdapter(Context context, int layoutResourceId, ArrayList<Stock> data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final int pos= position;
            Stock s = data.get(position);
            if(row == null)
            {
                if(s.delete==false)
                {
                    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                    row = inflater.inflate(layoutResourceId, parent, false);
                }
                else{
                    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                    row=inflater.inflate(R.layout.list_item_delete, parent, false);
                }
            }
            else{
                if(s.delete==true)
                {
                    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                    row=inflater.inflate(R.layout.list_item_delete, parent, false);
                }
                else {
                    LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                    row = inflater.inflate(layoutResourceId, parent, false);
                }
            }
            if(s.delete==true)
                return row;

            ((TextView) row.findViewById(R.id.name)).setText(s.name);
            ((TextView) row.findViewById(R.id.acronym)).setText(s.acronym);
            if(s.value!=null)
            {
                TextView tvalue = ((TextView) row.findViewById(R.id.value));
                tvalue.setVisibility(View.VISIBLE);
                tvalue.setText(String.valueOf(s.value));
                TextView percentage =  (TextView)row.findViewById(R.id.percentagem);
                percentage.setVisibility(View.VISIBLE);
                percentage.setText(s.percentage);
                if(s.percentage.contains("+"))
                {
                    percentage.setTextColor(Color.GREEN);
                }
                else {
                    percentage.setTextColor(Color.RED);
                }
                (row.findViewById(R.id.progressBar)).setVisibility(View.GONE);
            }
            else{
                (row.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
                TextView tvalue = ((TextView) row.findViewById(R.id.value));
                tvalue.setVisibility(View.GONE);
                TextView percentage =  (TextView)row.findViewById(R.id.percentagem);
                percentage.setVisibility(View.GONE);
            }
            ((TextView) row.findViewById(R.id.amount)).setText(String.valueOf(s.ammount));
            row.findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenuListView p = new PopupMenuListView(pos);
                    p.CreatePopupMenu(view);
                }
            });

            return row;
        }

        public class PopupMenuListView implements PopupMenu.OnMenuItemClickListener {
            private int position;

            public PopupMenuListView(int position)
            {
                this.position=position;
            }
            public void CreatePopupMenu(View v) {

                PopupMenu mypopupmenu = new PopupMenu(context, v);
                mypopupmenu.setOnMenuItemClickListener(this);
                MenuInflater inflater = mypopupmenu.getMenuInflater();
                inflater.inflate(R.menu.popup_menu_list_item, mypopupmenu.getMenu());
                mypopupmenu.show();
            }

            @Override
            public boolean onMenuItemClick(MenuItem arg0) {
                Stock s = stockArray.get(position);
                switch (arg0.getItemId()) {

                    case R.id.sell:
                        Dialog dialog;
                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                        // Get the layout inflater
                        LayoutInflater inflater = SearchActivity.this.getLayoutInflater();

                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        final View view = inflater.inflate(R.layout.dialog, null);
                        builder.setView(view)
                                // Add action buttons
                                .setPositiveButton(R.string.sell, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        NumberPicker number = (NumberPicker)view.findViewById(R.id.value);
                                        int n = number.getValue();
                                        stockArray.get(position).subAmmount(n);
                                        if(stockArray.get(position).ammount==0)
                                        {
                                            Stock s=stockArray.get(position);
                                            stockArray.remove(position);
                                            portfolio.removeStoke(s);
                                        }
                                        adapter.notifyDataSetChanged();
                                        portfolio.saveData();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        final NumberPicker numberPicker=(NumberPicker)view.findViewById(R.id.value);
                        TextView title = (TextView)view.findViewById(R.id.title);
                        title.setText("Venda");
                        numberPicker.setMinValue(0);
                        numberPicker.setMaxValue(s.ammount);
                        dialog =  builder.create();
                        dialog.show();
                        return true;
                    case R.id.buy:
                        Dialog dialogBuy;
                        AlertDialog.Builder builderBuy = new AlertDialog.Builder(SearchActivity.this);
                        // Get the layout inflater
                        LayoutInflater inflaterBuy = SearchActivity.this.getLayoutInflater();

                        // Inflate and set the layout for the dialog
                        // Pass null as the parent view because its going in the dialog layout
                        final View viewBuy = inflaterBuy.inflate(R.layout.dialog, null);
                        builderBuy.setView(viewBuy)
                                // Add action buttons
                                .setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        NumberPicker number = (NumberPicker)viewBuy.findViewById(R.id.value);
                                        int n = number.getValue();
                                        adapter.getItem(position).addAmmount(n);
                                        adapter.notifyDataSetChanged();
                                        portfolio.saveData();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        final NumberPicker numberPickerBuy=(NumberPicker)viewBuy.findViewById(R.id.value);
                        TextView titleBuy = (TextView)viewBuy.findViewById(R.id.title);
                        titleBuy.setText("Compra");
                        numberPickerBuy.setMinValue(0);
                        numberPickerBuy.setMaxValue(1000);
                        dialogBuy =  builderBuy.create();
                        dialogBuy.show();
                        return true;
                    default:
                        return true;
                }
            }
        }
    }
}

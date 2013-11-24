package feup.cmov.finance.share;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import feup.cmov.cmov_finance.R;
import feup.cmov.finance.chart.ChartStockActivity;
import feup.cmov.finance.connection.Network;
import feup.cmov.finance.connection.WebServiceCallRunnable;
import feup.cmov.finance.stock.Portfolio;
import feup.cmov.finance.stock.Stock;

public class PortefolioActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks{
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private HashMap<String, Stock> stocks;
    private ListView listView;
    private PortfolioAdapter adapter;
    private Dialog dialog;
    private ArrayList<Stock> stockArray;

    protected Portfolio portfolio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portefolio);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        portfolio = (Portfolio) getApplication();
        /*portfolio.createStock("AAPL", 150);
        portfolio.createStock("IBM", 12);
        portfolio.createStock("DELL", 15);
        portfolio.createStock("CSCO", 120);
        portfolio.createStock("AMZN", 20);
        portfolio.createStock("GOOG", 25);*/
        stocks = portfolio.getStocksHashMap();
        listView = (ListView) findViewById(R.id.list);

        stockArray = new ArrayList<Stock>(stocks.values());
        adapter = new PortfolioAdapter(this , R.layout.list_item, stockArray);
        listView.setAdapter(adapter);
        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stock s = stockArray.get(position);
                Intent intent = new Intent(PortefolioActivity.this, ChartStockActivity.class);
                intent.putExtra("stock", s);
                startActivity(intent);
            }
        });
        refreshData(new Handler());
    }
    public void refreshData(Handler handler){
        final Handler h = handler;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                new CurrentValueStock(h).run();
            }
        });
        thread.start();
    }
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //TODO fazer menu
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.current_portfolio);
                break;
            case 2:
                mTitle = getString(R.string.portfolio_30_day);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    private static final String[] COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setSearchableInfo( searchManager.getSearchableInfo(getComponentName()));

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_new:
                addStock();
                return true;
            case R.id.action_refresh:
                Handler handler = getWindow().getDecorView().getHandler();
                refreshData(handler);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void addStock()
    {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(PortefolioActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = PortefolioActivity.this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_new, null);
        String[] array= getResources().getStringArray(R.array.array_acronym);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinnername);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item,
                array);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        RadioButton inputRadio = (RadioButton)view.findViewById(R.id.radio_text);
        RadioButton spinnerRadio = (RadioButton) view.findViewById(R.id.radio_sppiner);
        inputRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText text = (EditText) view.findViewById(R.id.textname);
                Spinner spinnerc = (Spinner) view.findViewById(R.id.spinnername);
                if (isChecked)
                {

                    text.setClickable(true);
                    text.setFocusable(true);
                    text.setEnabled(true);
                    text.setFocusableInTouchMode(true);
                    text.requestFocus();
                    spinnerc.setClickable(false);
                    spinnerc.setFocusable(false);
                }
                else
                {
                    text.setClickable(false);
                    text.setFocusable(false);
                    spinnerc.setClickable(true);
                    spinnerc.setFocusable(true);
                    spinnerc.requestFocus();
                }
            }
        });


        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        RadioGroup radioButtonGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
                        int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();
                        String value;
                        if (radioButtonID == R.id.radio_sppiner) {
                            Spinner spinners = (Spinner) view.findViewById(R.id.spinnername);
                            value = (String) spinners.getSelectedItem();
                        } else {
                            EditText texts = (EditText) view.findViewById(R.id.textname);
                            value = texts.getText().toString();
                        }
                        EditText quantidadet = (EditText) view.findViewById(R.id.quantidade);
                        String quantidade = quantidadet.getText().toString();
                        int quantidadeInt = Integer.parseInt(quantidade);

                        portfolio.createStock(value.toUpperCase(), quantidadeInt);
                        portfolio.saveData();
                        stocks = portfolio.getStocksHashMap();
                        adapter.add(new Stock(value.toUpperCase(), quantidadeInt));
                        adapter.notifyDataSetChanged();
                        refreshData(getWindow().getDecorView().getHandler());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog =  builder.create();
        dialog.show();
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
            if(row == null)
            {

                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }
            Stock s = data.get(position);

            ((TextView) row.findViewById(R.id.acronym)).setText(s.acronym);
            ((TextView) row.findViewById(R.id.value)).setText(String.valueOf(s.value));
            ((TextView) row.findViewById(R.id.amount)).setText(String.valueOf(s.ammount));
            row.findViewById(R.id.action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenuListaView p = new PopupMenuListaView(pos);
                    p.CreatePopupMenu(view);
                }
            });

            return row;
        }

        public class PopupMenuListaView implements PopupMenu.OnMenuItemClickListener {
            private int position;

            public PopupMenuListaView(int position)
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(PortefolioActivity.this);
                        // Get the layout inflater
                        LayoutInflater inflater = PortefolioActivity.this.getLayoutInflater();

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
                        AlertDialog.Builder builderBuy = new AlertDialog.Builder(PortefolioActivity.this);
                        // Get the layout inflater
                        LayoutInflater inflaterBuy = PortefolioActivity.this.getLayoutInflater();

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
                                        stockArray.get(position).subAmmount(n);
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



    public class CurrentValueStock extends WebServiceCallRunnable{
        public CurrentValueStock(Handler h)
        {
            super(h);
        }

        @Override
        public void run() {
            if(stocks.size()==0)
                return;
            Network network = new Network();
            String query = "http://finance.yahoo.com/d/quotes?f=sl1d1t1v&s=";
            String args ="";
            for (String key : stocks.keySet()) {

                args  += ","+ stocks.get(key).acronym;
            }
            args = args.substring(1);
            String res = network.get(query + args);
            parseDataValue(res);
            handler_.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }


    }

    public void parseDataValue(String res) {
        String[] sStocks = res.split("\\r?\\n");
        for(int i = 0 ; i < sStocks.length; i++)
        {
            String t[] = sStocks[i].split(",");
            String skey =  t[0];
            String svalue = t[1];
            Float f= Float.valueOf(svalue.trim()).floatValue();
            skey = skey.substring(1, skey.length()-1);
            Stock s = stocks.get(skey);
            s.value = f;
        }
    }
}

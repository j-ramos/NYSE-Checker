package feup.cmov.finance.share;

import android.app.Activity;
;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Resources;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import feup.cmov.cmov_finance.R;
import feup.cmov.finance.stock.Stock;

public class PortefolioActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private Stock[] stocks;
    private ListView listView;
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
        stocks = new Stock[5];
        for(int i=0; i < 5; i++)
        {
            String name = "coisas " + i;
            Stock s = new Stock(name,  i);
            s.value = new Float(i*5);
            stocks[i] = s;
        }
        listView = (ListView) findViewById(R.id.list);
        PortefolioAdapter adapter = new PortefolioAdapter(this , R.layout.list_item, stocks);
        listView.setAdapter(adapter);
        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(context, "aqui", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //TODO fazer menu
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }
    private static final String[] COUNTRIES = new String[] { "Belgium",
            "France", "France_", "Italy", "Germany", "Spain" };

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


            LayoutInflater inflator = (LayoutInflater) this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflator.inflate(R.layout.actionbar, null);
            ActionBar actionBar = getActionBar();
            actionBar.setCustomView(v);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, COUNTRIES);
            AutoCompleteTextView textView = (AutoCompleteTextView) v
                    .findViewById(R.id.editText1);
            textView.setAdapter(adapter);

            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class PortefolioAdapter extends ArrayAdapter<Stock> {

        private int layoutResourceId;
        private Stock[] data;
        private Context context;

        public PortefolioAdapter(Context context, int layoutResourceId, Stock[] data) {
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
            Stock s = data[position];

            ((TextView) row.findViewById(R.id.acronym)).setText(s.acronym);
            ((TextView) row.findViewById(R.id.value)).setText(String.valueOf(s.value));
            ((TextView) row.findViewById(R.id.amount)).setText(String.valueOf(s.amount));
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

                switch (arg0.getItemId()) {

                    case R.id.buy:
                        Toast.makeText(context, "buy", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.sell:
                        Toast.makeText(context, "sell", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return true;
                }
            }
        }
    }

}

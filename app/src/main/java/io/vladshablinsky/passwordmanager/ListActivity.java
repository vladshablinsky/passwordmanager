package io.vladshablinsky.passwordmanager;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
//import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends ActionBarActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private ListView listViewEntries;
    private TextView textEmptyListEntries;

    private SearchView searchView;
    private SearchManager searchManager;
    private MenuItem searchItem;

    // adapter
    private List<Entry> listEntries;
    private EntryDAO entryDAO;
    private long sheetId;
    private ListEntriesAdapter adapter;

    public static final int REQUEST_CODE_ADD_ENTRY = 40;
    public static final String EXTRA_ADDED_ENTRY = "extra_key_added_entry";
    public static final String EXTRA_SELECTED_SHEET_ID = "extra_key_selected_sheet_id";

    private void initViews() {
        // add listeners here and other things.
        this.listViewEntries = (ListView) findViewById(R.id.listViewEntries);
        this.textEmptyListEntries = (TextView) findViewById(R.id.textViewEntries);
        // same for empty text
        // add listeners to the listView itself
        this.listViewEntries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Entry clickedEntry = adapter.getItem(position);
                EntryDialog entryDialog = EntryDialog.newInstance(clickedEntry);
                entryDialog.show(getFragmentManager(), "abacaba");
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        initViews();

        entryDAO = new EntryDAO(this);
        Intent intent = getIntent();

        if (intent != null) {
            this.sheetId = intent.getLongExtra(EXTRA_SELECTED_SHEET_ID, -1);
        }

        if (sheetId != -1) {
            listEntries = entryDAO.getEntriesOfSheet(sheetId);
            if (listEntries != null && !listEntries.isEmpty()) {
                // TODO implement adaptor for passwords
                adapter = new ListEntriesAdapter(this, listEntries);
                listViewEntries.setAdapter(adapter);

                textEmptyListEntries.setVisibility(View.GONE);
                listViewEntries.setVisibility(View.VISIBLE);
            }
            else {
                textEmptyListEntries.setVisibility(View.VISIBLE);
                listViewEntries.setVisibility(View.GONE);
            }
        }



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.requestFocus();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_add_entry:
                Intent intent = new Intent(this, AddEntryActivity.class);
                intent.putExtra(AddEntryActivity.EXTRA_SELECTED_SHEET_ID, sheetId);
                startActivityForResult(intent, REQUEST_CODE_ADD_ENTRY);
                break;
        }


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_ENTRY) {
            if (resultCode == RESULT_OK) {
                if (listEntries == null) {
                    listEntries = new ArrayList<Entry>();
                }

                if (entryDAO == null) {
                    entryDAO = new EntryDAO(this);
                }
                listEntries = entryDAO.getEntriesOfSheet(sheetId);

                if (adapter == null) {
                    adapter = new ListEntriesAdapter(this, listEntries);
                    listViewEntries.setAdapter(adapter);
                } else {
                    adapter.setOriginalItems(listEntries);
                }
                if(listViewEntries.getVisibility() != View.VISIBLE) {
                    textEmptyListEntries.setVisibility(View.GONE);
                    listViewEntries.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public EntryDAO getEntryDAO() {
        return entryDAO;
    }

    public List<Entry> getListEntries() {
        return listEntries;
    }

    public TextView getTextEmptyListEntries() {
        return textEmptyListEntries;
    }

    public ListView getListViewEntries() {
        return listViewEntries;
    }

    public ListEntriesAdapter getAdapter() {
        return adapter;
    }

    @Override
    public boolean onClose() {
        adapter.filterData("");
        adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filterData(query);
        adapter.notifyDataSetChanged();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filterData(newText);
        adapter.notifyDataSetChanged();
        return false;
    }
}

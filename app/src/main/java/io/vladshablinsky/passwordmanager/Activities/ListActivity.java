package io.vladshablinsky.passwordmanager.Activities;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.vladshablinsky.passwordmanager.Entities.Entry;
import io.vladshablinsky.passwordmanager.Database.EntryDAO;
import io.vladshablinsky.passwordmanager.Dialogs.EntryDialog;
import io.vladshablinsky.passwordmanager.Adapters.ListEntriesAdapter;
import io.vladshablinsky.passwordmanager.R;


public class ListActivity extends ActionBarActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private ListView listViewEntries;
    private TextView textEmptyListEntries;

    private SearchView searchView;
    private SearchManager searchManager;
    private MenuItem searchItem;

    private List<Entry> listEntries;
    private EntryDAO entryDAO;
    private long sheetId;
    private String sheetPass;
    private ListEntriesAdapter adapter;

    public static final int REQUEST_CODE_ADD_ENTRY = 40;
    public static final String EXTRA_ADDED_ENTRY = "extra_key_added_entry";
    public static final String EXTRA_SELECTED_SHEET_ID = "extra_key_selected_sheet_id";
    public static final String EXTRA_SHEET_MASTER_PASS = "extra_sheet_master_pass";

    private void initViews() {
        this.listViewEntries = (ListView) findViewById(R.id.listViewEntries);
        this.textEmptyListEntries = (TextView) findViewById(R.id.textViewEntries);
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

        Intent intent = getIntent();

        if (intent != null) {
            this.sheetId = intent.getLongExtra(EXTRA_SELECTED_SHEET_ID, -1);
            this.sheetPass = intent.getStringExtra(EXTRA_SHEET_MASTER_PASS);
        }

        entryDAO = new EntryDAO(this, sheetPass);

        if (sheetId != -1) {
            listEntries = entryDAO.getEntriesOfSheet(sheetId);
            if (listEntries != null && !listEntries.isEmpty()) {
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
                intent.putExtra(AddEntryActivity.EXTRA_SELECTED_MASTER_PASS, sheetPass);
                startActivityForResult(intent, REQUEST_CODE_ADD_ENTRY);
                break;
            case R.id.action_export_sheet:
                StringBuilder sb = new StringBuilder();
                if (listEntries.isEmpty()) {
                    Toast.makeText(this, "Nothing to export.", Toast.LENGTH_LONG).show();
                    break;
                }
                for (Entry entry: listEntries) {
                    sb.append("Entry name: " + entry.getName() + "\n" +
                        "Entry password: " + entry.getPass() + "\n\n");
                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
        }

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
                    entryDAO = new EntryDAO(this, sheetPass);
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
        if (adapter != null) {
            adapter.filterData("");
            adapter.notifyDataSetChanged();
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (adapter != null) {
            adapter.filterData(query);
            adapter.notifyDataSetChanged();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter != null) {
            adapter.filterData(newText);
            adapter.notifyDataSetChanged();
        }
        return false;
    }
}

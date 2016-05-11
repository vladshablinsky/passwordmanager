package io.vladshablinsky.passwordmanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class ListActivity extends ActionBarActivity {

    private ListView listViewEntries;
    private TextView textEmptyListEntries;

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

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

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

                    if(listViewEntries.getVisibility() != View.VISIBLE) {
                        textEmptyListEntries.setVisibility(View.GONE);
                        listViewEntries.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    adapter.setItems(listEntries);
                    adapter.notifyDataSetChanged();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

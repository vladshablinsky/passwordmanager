package io.vladshablinsky.passwordmanager.Activities;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import io.vladshablinsky.passwordmanager.Adapters.ListSheetsAdapter;
import io.vladshablinsky.passwordmanager.Dialogs.ProxySheetEntryDialog;
import io.vladshablinsky.passwordmanager.R;
import io.vladshablinsky.passwordmanager.Entities.Sheet;
import io.vladshablinsky.passwordmanager.Database.SheetDAO;


public class MainActivity extends ActionBarActivity
        implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private ListView listViewSheets;
    private TextView textEmptyListSheets;
    private SearchView searchView;
    private SearchManager searchManager;
    private MenuItem searchItem;

    // adapter
    private ListSheetsAdapter adapter;
    private List<Sheet> listSheets;
    private SheetDAO sheetDAO;

    public static final int REQUEST_CODE_ADD_SHEET = 40;
    public static final String EXTRA_ADDED_SHEET = "extra_key_added_sheet";

    private void initViews() {
        System.out.println("IN INIT VIEWS");
        this.listViewSheets = (ListView) findViewById(R.id.listViewSheets);
        this.textEmptyListSheets = (TextView) findViewById(R.id.textViewSheets);
        this.listViewSheets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO ADD PROXY HERE
                Sheet clickedSheet = adapter.getItem(position);
                ProxySheetEntryDialog proxyDialog = ProxySheetEntryDialog.newInstance(clickedSheet);
                proxyDialog.show(getFragmentManager(), "abacaba");
            }
        });
        this.listViewSheets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Sheet clickedSheet = adapter.getItem(position);
                showDeleteDialogConfirmation(clickedSheet);
                return true;
            }
        });
    }

    private void showDeleteDialogConfirmation(final Sheet clickedSheet) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder.setMessage("Are you sure you want to delete the \""+clickedSheet.getName()+"\" sheet ?");

        // Set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the company and refresh the list
                if(sheetDAO != null) {
                    sheetDAO.deleteSheet(clickedSheet);
                    listSheets.remove(clickedSheet);

                    // Refresh the listView
                    if(listSheets.isEmpty()) {
                        listViewSheets.setVisibility(View.GONE);
                        textEmptyListSheets.setVisibility(View.VISIBLE);
                    }
                    adapter.deleteItem(clickedSheet);
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Sheet was deleted", Toast.LENGTH_SHORT).show();
            }
        });

        // Set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // Show alert
        alertDialog.show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sheetDAO = new SheetDAO(this);

        initViews();

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        listSheets = sheetDAO.getAllSheets();
        if ((listSheets != null) && !listSheets.isEmpty()) {
            adapter = new ListSheetsAdapter(this, listSheets);
            listViewSheets.setAdapter(adapter);
            if (!adapter.getItems().isEmpty()) {
                textEmptyListSheets.setVisibility(View.GONE);
                listViewSheets.setVisibility(View.VISIBLE);
            }
        } else {
            textEmptyListSheets.setVisibility(View.VISIBLE);
            listViewSheets.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.action_add_sheet:
                Intent intent = new Intent(this, AddSheetActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_SHEET);
                break;
            case R.id.action_settings:
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage(getResources().getString(R.string.about_string))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ADD_SHEET && resultCode == RESULT_OK) {
            if (data != null) {
                Sheet createdSheet = (Sheet) data.getSerializableExtra(EXTRA_ADDED_SHEET);
                if (createdSheet != null) {
                    if (listSheets == null) {
                        listSheets = new ArrayList<>();
                    }
                    listSheets.add(createdSheet);

                    if (adapter == null) {
                        adapter = new ListSheetsAdapter(this, listSheets);
                        listViewSheets.setAdapter(adapter);
                    }
                    else {
                        adapter.setOriginalItems(listSheets);
                    }
                    adapter.notifyDataSetChanged();

                    if(listSheets.isEmpty()) {
                        listViewSheets.setVisibility(View.GONE);
                        textEmptyListSheets.setVisibility(View.VISIBLE);
                    } else {
                        listViewSheets.setVisibility(View.VISIBLE);
                        textEmptyListSheets.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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

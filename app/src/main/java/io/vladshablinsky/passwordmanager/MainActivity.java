package io.vladshablinsky.passwordmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private ListView listViewSheets;
    private TextView textEmptyListSheets;

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
                Sheet clickedSheet = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra(ListActivity.EXTRA_SELECTED_SHEET_ID, clickedSheet.getId());
                startActivity(intent);
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

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete the company and refresh the list
                if(sheetDAO != null) {
                    sheetDAO.deleteSheet(clickedSheet);
                    listSheets.remove(clickedSheet);

                    //refresh the listView
                    if(listSheets.isEmpty()) {
                        listViewSheets.setVisibility(View.GONE);
                        textEmptyListSheets.setVisibility(View.VISIBLE);
                    }
                    adapter.setItems(listSheets);
                    adapter.notifyDataSetChanged();
                }

                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Sheet was deleted", Toast.LENGTH_SHORT).show();
            }
        });

        // set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sheetDAO = new SheetDAO(this);

        initViews();
        System.out.println("AFTER INIT VIEWS");

        // fill the listView

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
        }

        //noinspection SimplifiableIfStatement
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
                        if(listViewSheets.getVisibility() != View.VISIBLE) {
                            listViewSheets.setVisibility(View.VISIBLE);
                            textEmptyListSheets.setVisibility(View.GONE);
                        }

                        adapter = new ListSheetsAdapter(this, listSheets);
                        listViewSheets.setAdapter(adapter);
                    }
                    else {
                        adapter.setItems(listSheets);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

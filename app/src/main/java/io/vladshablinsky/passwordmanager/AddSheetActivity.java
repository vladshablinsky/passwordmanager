package io.vladshablinsky.passwordmanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddSheetActivity extends ActionBarActivity {

    private EditText editSheetName;
    private EditText editSheetDescription;
    private EditText editSheetPass;
    private EditText editSheetPassV;
    private Button buttonCreateSheet;

    private SheetDAO sheetDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sheet);

        this.editSheetName = (EditText) findViewById(R.id.editSheetName);
        this.editSheetDescription = (EditText) findViewById(R.id.editSheetDescription);
        this.editSheetPass = (EditText) findViewById(R.id.editPass);
        this.editSheetPassV = (EditText) findViewById(R.id.editPassV);
        this.buttonCreateSheet = (Button) findViewById(R.id.buttonCreateSheet);

        this.sheetDAO = new SheetDAO(this);

        this.buttonCreateSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable sheetName = editSheetName.getText();
                Editable sheetDescription = editSheetDescription.getText();
                Editable sheetPass = editSheetPass.getText();
                Editable sheetPassV = editSheetPassV.getText();

                // TODO description
                if (
                        !TextUtils.isEmpty(sheetName) &&
                        !TextUtils.isEmpty(sheetPass) &&
                        !TextUtils.isEmpty(sheetPassV) &&
                        !TextUtils.isEmpty(sheetDescription) &&
                        sheetPassV.toString().equals(sheetPass.toString())
                        ) {
                    Sheet createdSheet = sheetDAO.createSheet(
                         sheetName.toString(),
                         sheetPass.toString(),
                         sheetDescription.toString()
                    );

                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.EXTRA_ADDED_SHEET, createdSheet);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (TextUtils.isEmpty(sheetName) ||
                            TextUtils.isEmpty(sheetPass) ||
                            TextUtils.isEmpty(sheetPassV)) {
                        Toast.makeText(AddSheetActivity.this, "Check that the fields are not empty", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AddSheetActivity.this, "Check that the passwords match", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sheetDAO.close();
    }
}

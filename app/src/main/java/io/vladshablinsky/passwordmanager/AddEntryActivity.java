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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class AddEntryActivity extends ActionBarActivity {

    private EditText editEntryName;
    private EditText editEntryDescription;
    private EditText editEntryPass;
    private Button buttonAddEntry;
    private ImageView imageRandom;
    private SeekBar seekBar;

    private EntryDAO entryDAO;

    private long entrySheetId;
    private String sheetPass;

    public static final String EXTRA_SELECTED_SHEET_ID = "extra_key_selected_entry_sheet_id";
    public static final String EXTRA_SELECTED_MASTER_PASS = "extra_selected_sheet";

    // private Entry selectedEntry;

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnmABCDEFGHIJKLMNOPRSTUVWXYZ";

    protected static String getRandomString(final int sizeOfRandomString)
    {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for(int i = 0; i < sizeOfRandomString; ++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        final Intent intent = getIntent();
        if (intent != null) {
            this.entrySheetId = intent.getLongExtra(EXTRA_SELECTED_SHEET_ID, -1);
            this.sheetPass = intent.getStringExtra(EXTRA_SELECTED_MASTER_PASS);
        }

        entryDAO = new EntryDAO(this, sheetPass);

        this.editEntryName = (EditText) findViewById(R.id.editEntryName);
        this.editEntryDescription = (EditText) findViewById(R.id.editEntryDescription);
        this.editEntryPass = (EditText) findViewById(R.id.editEntryPass);
        this.buttonAddEntry = (Button) findViewById(R.id.addEntryButton);
        this.buttonAddEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable entryName = editEntryName.getText();
                Editable entryDescription = editEntryDescription.getText();
                Editable entryPass = editEntryPass.getText();

                if (!TextUtils.isEmpty(entryName) &&
                        !TextUtils.isEmpty(entryDescription) &&
                        !TextUtils.isEmpty(entryPass) &&
                        entrySheetId != -1) {
                    // TODO ADD PASSWORD
                    Entry createdEntry = entryDAO.createEntry(
                            entryName.toString(),
                            entryPass.toString(),
                            entrySheetId
                    );
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddEntryActivity.this, "Fields must not be empty", Toast.LENGTH_LONG).show();
                }
            }
        });
        this.imageRandom = (ImageView) findViewById(R.id.imageRandom);
        this.seekBar = (SeekBar) findViewById(R.id.seekBar);
        this.imageRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = 5;
                if (seekBar != null) {
                    length += seekBar.getProgress();
                }
                editEntryPass.setText(AddEntryActivity.getRandomString(length), TextView.BufferType.EDITABLE);
            }
        });
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editEntryPass.setText(AddEntryActivity.getRandomString(progress + 5), TextView.BufferType.EDITABLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        entryDAO.close();
    }
}

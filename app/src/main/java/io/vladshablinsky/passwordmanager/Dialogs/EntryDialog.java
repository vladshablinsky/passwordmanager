package io.vladshablinsky.passwordmanager.Dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import io.vladshablinsky.passwordmanager.Activities.AddEntryActivity;
import io.vladshablinsky.passwordmanager.Activities.ListActivity;
import io.vladshablinsky.passwordmanager.Encryption.Helper;
import io.vladshablinsky.passwordmanager.Entities.Entry;
import io.vladshablinsky.passwordmanager.Database.EntryDAO;
import io.vladshablinsky.passwordmanager.R;

/**
 * Created by vlad on 5/12/16.
 */
public class EntryDialog extends DialogFragment {

    private EditText editEntryText;
    private Button cancelButton;
    private Button updateButton;
    private Button deleteButton;
    private TextView textEntryDialog;
    private boolean deleteAlreadyClicked = false;
    private Entry currentEntry;
    private int resultCode;
    private ImageView randomDialog;
    private SeekBar randomSeekBar;

    public static final int CODE_CANCEL = 0;
    public static final int CODE_UPDATE = 1;
    public static final int CODE_DELETE = 2;
    private long lastTimestamp = 0;

    public EntryDialog() {

    }


    public static EntryDialog newInstance(Serializable currentEntry) {
        EntryDialog f = new EntryDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("currentEntry", currentEntry);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentEntry = (Entry) getArguments().getSerializable("currentEntry");
        deleteAlreadyClicked = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_entry, container);
        editEntryText = (EditText) view.findViewById(R.id.editEntryText);
        textEntryDialog = (TextView) view.findViewById(R.id.textEntryDialog);
        cancelButton = (Button) view.findViewById(R.id.buttonCancel);
        updateButton = (Button) view.findViewById(R.id.buttonUpdate);
        deleteButton = (Button) view.findViewById(R.id.buttonDelete);
        textEntryDialog.setText("Change password for " + currentEntry.getName());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultCode = CODE_CANCEL;
                EntryDialog.this.dismiss();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable entryPass = editEntryText.getText();
                if (!TextUtils.isEmpty(entryPass)) {
                    Entry newEntry = new Entry();
                    newEntry.setName(currentEntry.getName());
                    newEntry.setId(currentEntry.getId());
                    newEntry.setSheet(currentEntry.getSheet());
                    newEntry.setSheetId(currentEntry.getSheetId());
                    newEntry.setPass(entryPass.toString());
                    newEntry.setDescription(currentEntry.getDescription());

                    ListActivity callingActivity = (ListActivity) EntryDialog.this.getActivity();
                    EntryDAO entryDAO = callingActivity.getEntryDAO();

                    if (entryDAO != null) {
                        callingActivity.getListEntries().set(
                                callingActivity.getListEntries().indexOf(currentEntry),
                                newEntry
                        );
                        entryDAO.modifyEntry(currentEntry, newEntry);
                    }

                    callingActivity.getAdapter().updateItem(currentEntry, newEntry);
                    callingActivity.getAdapter().notifyDataSetChanged();
                    EntryDialog.this.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Password field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTimestamp = System.currentTimeMillis() / 1000;
                if (currentTimestamp - lastTimestamp < 2) {
                    ListActivity callingActivity = (ListActivity) EntryDialog.this.getActivity();
                    EntryDAO entryDAO = callingActivity.getEntryDAO();

                    if(entryDAO != null) {
                        entryDAO.deleteEntry(currentEntry);
                        callingActivity.getListEntries().remove(currentEntry);

                        //refresh the listView
                        if(callingActivity.getListEntries().isEmpty()) {
                            callingActivity.getListViewEntries().setVisibility(View.GONE);
                            callingActivity.getTextEmptyListEntries().setVisibility(View.VISIBLE);
                        }
                        callingActivity.getAdapter().deleteItem(currentEntry);
                        callingActivity.getAdapter().notifyDataSetChanged();
                    }
                    EntryDialog.this.dismiss();
                } else {
                    Toast.makeText(EntryDialog.this.getActivity(), "Tap again to delete", Toast.LENGTH_SHORT).show();
                }
                lastTimestamp = currentTimestamp;
            }
        });

        this.randomDialog = (ImageView) view.findViewById(R.id.dialogRandom);
        this.randomSeekBar = (SeekBar) view.findViewById(R.id.seekBar2);
        this.randomDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = 5;
                if (randomSeekBar != null) {
                    length += randomSeekBar.getProgress();
                }
                editEntryText.setText(Helper.getRandomString(length), TextView.BufferType.EDITABLE);
            }
        });
        this.randomSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editEntryText.setText(Helper.getRandomString(progress + 5), TextView.BufferType.EDITABLE);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return view;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Entry getCurrentEntry() {
        return currentEntry;
    }
}

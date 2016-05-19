package io.vladshablinsky.passwordmanager.Dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

import io.vladshablinsky.passwordmanager.Activities.ListActivity;
import io.vladshablinsky.passwordmanager.Activities.MainActivity;
import io.vladshablinsky.passwordmanager.Entities.Sheet;
import io.vladshablinsky.passwordmanager.R;

/**
 * Created by vlad on 5/13/16.
 */
public class ProxySheetEntryDialog extends DialogFragment {

    private EditText editMasterPass;
    private Button buttonOK;
    private Button buttonCancel;
    private Sheet currentSheet;

    public ProxySheetEntryDialog() {

    }

    public static ProxySheetEntryDialog newInstance(Sheet currentSheet) {
        ProxySheetEntryDialog f = new ProxySheetEntryDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putSerializable("currentSheet", currentSheet);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentSheet = (Sheet) getArguments().getSerializable("currentSheet");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_proxy_sheet_entry, container);
        editMasterPass = (EditText) view.findViewById(R.id.editMasterPass);
        buttonOK = (Button) view.findViewById(R.id.buttonOK);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable masterPass = editMasterPass.getText();

                if (!TextUtils.isEmpty(masterPass)) {
                    String sha1Hash = Hashing.sha1().hashString(masterPass.toString(), Charset.defaultCharset()).toString();
                    if (sha1Hash.equals(currentSheet.getPass())) {
                        MainActivity callingActivity = (MainActivity) getActivity();
                        Intent intent = new Intent(callingActivity, ListActivity.class);
                        intent.putExtra(ListActivity.EXTRA_SELECTED_SHEET_ID, currentSheet.getId());
                        intent.putExtra(ListActivity.EXTRA_SHEET_MASTER_PASS, masterPass.toString());
                        dismiss();
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Password incorrect", Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Password field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

}

package io.vladshablinsky.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.vladshablinsky.passwordmanager.R;
import io.vladshablinsky.passwordmanager.Sheet;

/**
 * Created by vlad on 5/11/16.
 */
public class ListSheetsAdapter extends BaseAdapter {

    private List<Sheet> items;
    private LayoutInflater inflater;

    public ListSheetsAdapter(Context context, List<Sheet> listSheets) {
        this.setItems(listSheets);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (getItems() != null && !getItems().isEmpty()) {
            return getItems().size();
        }
        return 0;
    }

    @Override
    public Sheet getItem(int position) {
        if (getItems() != null && !getItems().isEmpty()) {
            return getItems().get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (getItems() != null && !getItems().isEmpty()) {
            return getItems().get(position).getId();
        }
        // what the heck?
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = inflater.inflate(R.layout.list_item_sheet, parent, false);
            holder = new ViewHolder();
            holder.sheetName = (TextView) v.findViewById(R.id.firstLineSheet);
            holder.description = (TextView) v.findViewById(R.id.secondLineSheet);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Sheet currentItem = getItem(position);
        if (currentItem != null) {
            holder.sheetName.setText(currentItem.getName());
            holder.description.setText(currentItem.getPass());
        }
        return v;
    }

    public List<Sheet> getItems() {
        return items;
    }

    public void setItems(List<Sheet> items) {
        this.items = items;
    }

    class ViewHolder {
        TextView sheetName;
        TextView description;
        // TextView sheetPass;
    }
}

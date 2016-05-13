package io.vladshablinsky.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.vladshablinsky.passwordmanager.R;
import io.vladshablinsky.passwordmanager.Sheet;

/**
 * Created by vlad on 5/11/16.
 */
public class ListSheetsAdapter extends BaseAdapter {

    private List<Sheet> items;
    private List<Sheet> originalItems;
    private LayoutInflater inflater;
    private String lastFilter = "";

    public ListSheetsAdapter(Context context, List<Sheet> listSheets) {
        this.originalItems = listSheets;
        this.items = new ArrayList<>();
        this.items.addAll(listSheets);
        this.inflater = LayoutInflater.from(context);
    }

    public void setOriginalItems(List<Sheet> originalItems) {
        this.originalItems = originalItems;
        this.items.clear();
        this.items.addAll(originalItems);
        filterData(lastFilter);
    }

    public void deleteItem(Sheet item) {
        originalItems.remove(item);
        items.remove(item);
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

    public void filterData(String query) {
        query = query.toLowerCase();
        lastFilter = query;
        items.clear();

        if (query.isEmpty()) {
            items.addAll(originalItems);
        } else {
            for (Sheet curSheet: originalItems) {
                if (curSheet.getName().toLowerCase().contains(query)) {
                    items.add(curSheet);
                }
            }
        }
    }

    class ViewHolder {
        TextView sheetName;
        TextView description;
        // TextView sheetPass;
    }
}

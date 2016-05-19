package io.vladshablinsky.passwordmanager.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.vladshablinsky.passwordmanager.Entities.Entry;
import io.vladshablinsky.passwordmanager.R;

/**
 * Created by vlad on 5/11/16.
 */
public class ListEntriesAdapter extends BaseAdapter {

    private static final String TAG = ListEntriesAdapter.class.getName();

    private List<Entry> items;
    private List<Entry> originalItems;
    private LayoutInflater inflater;
    private String lastFilter = "";

    public ListEntriesAdapter(Context context, List<Entry> listEntries) {
        this.originalItems = listEntries;
        this.items = new ArrayList<>();
        this.items.addAll(listEntries);
        this.inflater = LayoutInflater.from(context);
    }

    public List<Entry> getItems() {
        return items;
    }

    public void setOriginalItems(List<Entry> originalItems) {
        this.originalItems = originalItems;
        this.items.clear();
        this.items.addAll(originalItems);
        filterData(lastFilter);
    }

    public void setItems(List<Entry> items) {
        this.items = items;
    }

    public void updateItem(Entry item, Entry newItem) {
        int foundIndex = originalItems.indexOf(item);
        if (foundIndex != -1) {
            originalItems.set(foundIndex, newItem);
        }
        foundIndex = items.indexOf(item);
        if (foundIndex != -1) {
            items.set(foundIndex, newItem);
        }
    }

    public void deleteItem(Entry item) {
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
    public Entry getItem(int position) {
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;

        if (v == null) {
            v = inflater.inflate(R.layout.list_item_entry, parent, false);
            holder = new ViewHolder();
            holder.entryName = (TextView) v.findViewById(R.id.firstLineEntry);
            holder.entryDescription = (TextView) v.findViewById(R.id.secondLineEntry);
            holder.entryPassword = (TextView) v.findViewById(R.id.entryPass);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        // fill row data
        Entry currentItem = getItem(position);
        if (currentItem != null) {
            holder.entryName.setText(currentItem.getName());
            holder.entryDescription.setText(currentItem.getDescription());
            // System.out.println("PASSWORD OF CURRENT ITEM IS " + currentItem.getPass());
            holder.entryPassword.setText(currentItem.getPass());
        }
        return v;
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        lastFilter = query;
        items.clear();

        if (query.isEmpty()) {
            items.addAll(originalItems);
        } else {
            for (Entry curEntry: originalItems) {
                if (curEntry.getName().toLowerCase().contains(query)) {
                    items.add(curEntry);
                }
            }
        }
    }

    class ViewHolder {
        TextView entryName;
        TextView entryDescription;
        TextView entryPassword;
    }
}

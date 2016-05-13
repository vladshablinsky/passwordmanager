package io.vladshablinsky.passwordmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by vlad on 5/11/16.
 */
public class ListEntriesAdapter extends BaseAdapter {

    private List<Entry> items;
    private LayoutInflater inflater;

    public ListEntriesAdapter(Context context, List<Entry> listEntries) {
        this.setItems(listEntries);
        this.inflater = LayoutInflater.from(context);
    }

    public List<Entry> getItems() {
        return items;
    }

    public void setItems(List<Entry> items) {
        this.items = items;
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
            // ADD DESCRIPTION
            //holder.entryDescription.setText(currentItem);
            holder.entryPassword.setText(currentItem.getPass());
        }
        return v;
    }

    class ViewHolder {
        TextView entryName;
        TextView entryDescription;
        TextView entryPassword;
    }
}

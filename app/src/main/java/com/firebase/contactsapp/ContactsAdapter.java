package com.firebase.contactsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KSTL on 24-04-2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {


    Context mContext;
    List<Contact> contactsList, filterList;

    public ContactsAdapter(Context mContext, List<Contact> contactsList) {
        this.mContext = mContext;
        this.contactsList = contactsList;
        this.filterList = new ArrayList<Contact>();

        this.filterList.addAll(this.contactsList);
    }


    @SuppressLint("NewApi")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        viewGroup.setClipToPadding(false);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_item_view, viewGroup, false);
        view.setOutlineProvider(ViewOutlineProvider.BOUNDS);
        view.setElevation(30);
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Contact contact = filterList.get(position);
        holder.contact_name.setText(contact.getName());
        holder.contact_number.setText(contact.getMobile());
    }

    @Override
    public int getItemCount() {
        //return eventsList.size();
        return (null != filterList ? filterList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView contact_name, contact_number;

        public ViewHolder(View view) {
            super(view);
            contact_name = (TextView) view.findViewById(R.id.contact_name);
            contact_number = (TextView) view.findViewById(R.id.contact_number);
        }
    }

    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {

                    filterList.addAll(contactsList);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Contact item : contactsList) {
                        if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getMobile().contains(text.toLowerCase())) {
                            // Adding Matched items
                            filterList.add(item);
                        }
                    }
                }

                // Set on UI Thread
                ((MainActivity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }
}

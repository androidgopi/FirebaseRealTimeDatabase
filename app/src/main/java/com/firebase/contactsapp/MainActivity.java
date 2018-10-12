package com.firebase.contactsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    @BindView(R.id.search_view)
    SearchView search_view;

    @BindView(R.id.search_parent)
    EditText search_parent;

    @BindView(R.id.contact_listview)
    RecyclerView contact_listview;

    @BindView(R.id.add_contact)
    FloatingActionButton add_contact;
    private ContactsAdapter ContactsAdapter_adapter;
    private ProgressDialog progressDialog;
    private DatabaseReference fbReference;
    List<Contact> contactList = new ArrayList<>();
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupSearchView();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        count = 0;
        getContacts();
    }

    @Override
    protected void onResume() {
        if(count != 0) {
            contactList.clear();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            getContacts();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        count = 1;
    }



    private void getContacts() {

        fbReference = FirebaseDatabase.getInstance().getReference("Contacts");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.hasChildren()){

                    Map<String, String> details = (Map<String, String>) dataSnapshot.getValue();

                    Contact mModel = new Contact();
                    mModel.setName(details.get("Name"));
                    mModel.setMobile(details.get("Mobile"));
                    mModel.setCity(details.get("City"));

                    contactList.add(mModel);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        fbReference.addChildEventListener(childEventListener);



        fbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ContactsAdapter_adapter = new ContactsAdapter(MainActivity.this, contactList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                contact_listview.setLayoutManager(layoutManager);
                contact_listview.setNestedScrollingEnabled(false);
                contact_listview.setAdapter(ContactsAdapter_adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.add_contact)
    public void performClick(View view){
        switch (view.getId()){
            case R.id.add_contact:
                Intent saveOontactIntent = new Intent(MainActivity.this, SaveNewContactActivity.class);
                startActivity(saveOontactIntent);
                break;
        }
    }

    private void setupSearchView() {
        search_view.setIconifiedByDefault(false);
        search_view.setOnQueryTextListener(this);
        search_view.setFocusableInTouchMode(true);
        search_view.setSubmitButtonEnabled(false);
        search_view.setQueryHint("Search Contact");
        search_parent.requestFocus();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (ContactsAdapter_adapter != null) {
            ContactsAdapter_adapter.filter(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (ContactsAdapter_adapter != null) {
            ContactsAdapter_adapter.filter(query);
        }
        return true;
    }
}

package com.firebase.contactsapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SaveNewContactActivity extends AppCompatActivity
{
    @BindView(R.id.name_editText)
    EditText name_editText;

    @BindView(R.id.mobile_editText)
    EditText mobile_editText;

    @BindView(R.id.email_editText)
    EditText email_editText;

    @BindView(R.id.city_editText)
    EditText city_editText;

    @BindView(R.id.save_contact_btn)
    Button save_contact_btn;
    private DatabaseReference fbReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_contact);
        ButterKnife.bind(this);
        fbReference = FirebaseDatabase.getInstance().getReference();
    }

    @OnClick(R.id.save_contact_btn)
    public void performClick(View view){
        switch (view.getId()){
            case R.id.save_contact_btn:
                if(!TextUtils.isEmpty(name_editText.getText().toString()) && !TextUtils.isEmpty(mobile_editText.getText().toString())
                 && !TextUtils.isEmpty(email_editText.getText().toString()) && !TextUtils.isEmpty(city_editText.getText().toString())){
                    saveContact();
                }else{
                    Toast.makeText(SaveNewContactActivity.this, "All fields required", Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private void saveContact() {

        Map<String, String> caontactValues = new HashMap<>();
        caontactValues.put("Mobile", mobile_editText.getText().toString());
        caontactValues.put("Name", name_editText.getText().toString());
        caontactValues.put("Email", email_editText.getText().toString());
        caontactValues.put("City", city_editText.getText().toString());

        fbReference.child("Contacts").child(mobile_editText.getText().toString()).setValue(caontactValues);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mobile_editText.setText("");
                name_editText.setText("");
                email_editText.setText("");
                city_editText.setText("");
            }
        }, 2000);

    }
}

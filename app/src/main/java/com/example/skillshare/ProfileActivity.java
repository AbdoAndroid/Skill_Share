package com.example.skillshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    TextView name;
    EditText phone;
    ImageView edit_iv;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefDonor=database.getReference().child("user");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);

        name = findViewById(R.id.profile_title);
        phone = findViewById(R.id.et_phone);
        name.setText(sharedPreferences.getString("name", ""));
        phone.setText(sharedPreferences.getString("phone", ""));

        edit_iv = findViewById(R.id.edit_iv);
        edit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),EditPasswordActivity.class));
            }
        });
    }

    public void confirm_OnClick(View view) {
        User user=new User(sharedPreferences.getString("id",""),name.getText().toString(),
                phone.getText().toString(),sharedPreferences.getString("password",""));
        dataRefDonor.child(user.getId()).setValue(user);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("name",user.getFullName());
        edit.putString("phone",user.getMobileNum());
        edit.commit();
        finish();
    }
}

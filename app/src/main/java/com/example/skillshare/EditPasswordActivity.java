package com.example.skillshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPasswordActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefDonor=database.getReference().child("user");

    SharedPreferences sharedPreferences;
    EditText et_old,et_new,et_renew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        sharedPreferences=getSharedPreferences("user",MODE_PRIVATE);

        et_old =findViewById(R.id.et_old);
        et_new=findViewById(R.id.et_new);
        et_renew=findViewById(R.id.et_re_new);
    }


    public void confirm_OnClick(View view) {
        String savedPassword = sharedPreferences.getString("password", "");
        if (et_old.getText().toString().equals(savedPassword)){
            if (et_new.getText().toString().length()<6){
                Toast.makeText(getApplicationContext(),"The password is too short, try to make a longer one for your security",
                        Toast.LENGTH_LONG).show();
            }
            // checks if the 2 passwords are the same
            else if (!et_new.getText().toString().trim().equals( et_renew.getText().toString().trim())){
                Toast.makeText(getApplicationContext(),("The two passwords you have entered are not the same"),Toast.LENGTH_LONG).show();
            }else{
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("password",et_new.getText().toString().trim());
                edit.commit();
                applyOnDB();
                finish();
            }
        }else {
            Toast.makeText(getApplicationContext(),("The old password you have entered is wrong"),Toast.LENGTH_LONG).show();
        }
    }
    void  applyOnDB(){
            User user=new User(sharedPreferences.getString("id",""),sharedPreferences.getString("name",""),
                    sharedPreferences.getString("phone",""),sharedPreferences.getString("password",""));
            dataRefDonor.child(user.getId()).setValue(user);
    }
}
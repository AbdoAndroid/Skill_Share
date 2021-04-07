 package com.example.skillshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etPassword,et_phoneNum;
    Button btnLogin;

    //shared preferences to save the session info
    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefDonor=database.getReference().child("user");
    Query query_userExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//getting the data inside file "user" from shared pref which contains the session info
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        String userID = sharedPreferences.getString("id", "none");
        if (!userID.equals("none")) {
            sendUserToHome();
        }

        //binding views
        btnLogin = findViewById(R.id.login_btn);
        et_phoneNum = findViewById(R.id.et_phonenum);
        etPassword = findViewById(R.id.et_pswrd);
        //button login click event -- the login process and validation
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query_userExist=  dataRefDonor.orderByChild("mobileNum").equalTo(String.valueOf(et_phoneNum.getText()));

                query_userExist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            //the number hasn't been registered
                            Toast.makeText(getApplicationContext(),"This number hasn't registered   ",Toast.LENGTH_LONG).show();

                        } else{//the phone number already exists, checking on the password
                            try {
                                Map<String, Map> root = (Map) dataSnapshot.getValue();
                                Collection<Map> donations=  root.values();
                                //Log.i("donations",donations.toString());
                                Map<String, String> child=new HashMap<>();
                                for (Map<String,String> item:donations) {
                                    child =item;
                                    break;
                                }

                                if (child.get("password").equals(etPassword.getText().toString())){
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putString("id",child.get("id"));
                                    edit.putString("name",child.get("fullName"));
                                    edit.putString("phone",child.get("mobileNum"));
                                    edit.putString("location",child.get("location"));
                                    edit.putString("password",child.get("password"));
                                    edit.commit();

                                    sendUserToHome();
                                    //Toast.makeText(getApplicationContext(),"Correct Password",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Wrong Password ",Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception ex){
                                Log.d("logIn",ex.getMessage());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }


    private void sendUserToHome() {
        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }


    public void createNewAccount(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //finish();
    }
}

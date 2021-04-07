package com.example.skillshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    EditText et_fullName, et_phoneNum,et_Pswrd,et_re_pswrd;
    TextView tvLogin;
    Button btnRegister;
    //instance of database reference
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefUseer =database.getReference().child("user");
    Query query_existBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
        //linking views objects with the views on the layout
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.btnAlreadyHaveAcc);
        et_phoneNum = findViewById(R.id.et_phonenum);
        et_Pswrd = findViewById(R.id.et_pswrd);
        et_fullName = findViewById(R.id.et_fullname);
        et_re_pswrd = findViewById(R.id.et_d_re_pswrd);

        //register button event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking if the name has been entered
                if (et_fullName.getText().toString().length()<3){
                    Toast.makeText(getApplicationContext(),"Please enter your name",Toast.LENGTH_LONG).show();
                }
                //checks if there is a mobile value has entered correctly
                else if (et_phoneNum.getText().toString().length()<11&&et_phoneNum.getText().toString().length()>14){
                    Toast.makeText(getApplicationContext(),"Please enter a right mobile",Toast.LENGTH_LONG).show();
                }
                //checking if the password is short
                else if (et_Pswrd.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(),"The password is too short, try to make a longer one for your security",Toast.LENGTH_LONG).show();
                }
                // checks if the 2 passwords are the same
                else if (!et_Pswrd.getText().toString().trim().equals( et_re_pswrd.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(),("password1 " + et_Pswrd.getText() +" password 2 "+et_re_pswrd.getText()),Toast.LENGTH_LONG).show();
                }

                //the the values are complete
                // lets check if the user has registered before
                else{
                    //checking if the mobile number has signed before
                    query_existBefore=  dataRefUseer.orderByChild("mobileNum").equalTo(String.valueOf(et_phoneNum.getText()));
                    query_existBefore.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()) {
                                //the number hasn't been registered before so we will create a new account
                                createNewAcc();
                            } else{
                                //the phone number have been registered before
                                Toast.makeText(getApplicationContext(),"This numbered has registered before",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ifExistBefore", databaseError.getMessage());
                        }
                    });
                }
            }
        });


        //click listeners for the bottom textViews
        tvLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


    }
    void createNewAcc(){
        try {
            String id = dataRefUseer.push().getKey();
            User user=new User(id,et_fullName.getText().toString(),et_phoneNum.getText().toString(),et_Pswrd.getText().toString());
            dataRefUseer.child(id).setValue(user);
            Toast.makeText(getApplicationContext(),"Added Successfully, you can login now",Toast.LENGTH_LONG).show();
            goToLoginPage(null);
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }


    }


    public void goToLoginPage(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //finish();
    }
}
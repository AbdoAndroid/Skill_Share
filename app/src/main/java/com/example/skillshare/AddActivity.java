package com.example.skillshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddActivity extends AppCompatActivity {
    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database posts to add the process inside
    DatabaseReference dataRefPost = database.getReference().child("posts");

    SharedPreferences sharedPreferences;
    Post post;

    // declaring Views
    EditText et_time, et_desc;

    String _location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent=getIntent();
        _location=intent.getExtras().getString("location");

        //binding views
        et_desc=findViewById(R.id.et_desc);
        et_time=findViewById(R.id.et_time);

        sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE);

    }

    public void post_OnClick(View view) {
        post=new Post(dataRefPost.push().getKey(),sharedPreferences.getString("id","none"),_location,
                et_time.getText().toString(),et_desc.getText().toString());
        //injecting the post into the database
        dataRefPost.child(post.getId()).setValue(post);
        Toast.makeText(this, "Post Added Successfully", Toast.LENGTH_SHORT).show();
        et_desc.setText("");
        et_time.setText("");
    }
}
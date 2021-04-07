package com.example.skillshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity {


    ListView listView;
    Switch nearbySwitch;
    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database posts to add the process inside
    DatabaseReference dataRefPosts=database.getReference().child("posts");
    //pref that saves the current user info
    SharedPreferences sharedPreferences;
    //the List of my donations
    List<Post> _posts;
    List<Post> _nearbyPosts;
    PostAdapter adapter;


    Animation rotateOpenAnimation;
    Animation rotateCloseAnimation;
    Animation fromBottomAnimation;
    Animation toBottomAnimation;

    private Button mLogoutBtn;
    FloatingActionButton btnMenu,btnAdd,btnProfile, btnLogout;


    FusedLocationProviderClient mFusedLocationClient;
    String _location;


    boolean isClicked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        rotateOpenAnimation=AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateCloseAnimation=AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        fromBottomAnimation=AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toBottomAnimation=AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);
        btnAdd=findViewById(R.id.btn_add);
        btnMenu=findViewById(R.id.btn_menu);
        btnProfile=findViewById(R.id.btn_profile);
        btnLogout =findViewById(R.id.btn_logout);
        listView=findViewById(R.id.lv_posts);

        //getting location from the bottom functions
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

        dataRefPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    _posts=new ArrayList<>();
                    Map<String, Map> data = (Map) snapshot.getValue();
                    Collection<Map> posts=  data.values();
                    //Log.i("posts",data.toString());
                    _nearbyPosts=new ArrayList<>();
                    Location current=new Location("current");
                    current.setLatitude(Double.parseDouble(_location.split(",")[0]));
                    current.setLongitude(Double.parseDouble(_location.split(",")[1]));
                    String[] location_a;
                    Location aLoc;
                    if (posts.size()>0)
                        for (Map<String,String> item:posts) {
                            Post post=new Post(item.get("id"), item.get("user"), item.get("location"),
                                    item.get("time"), item.get("description"));
                                _posts.add(post);
                            location_a=post.getLocation().split(",");
                            aLoc=new Location("b");
                            aLoc.setLongitude(Double.parseDouble(location_a[1]));
                            aLoc.setLatitude(Double.parseDouble(location_a[0]));
                            if (current.distanceTo(aLoc)<5000){
                                _nearbyPosts.add(post);
                            }
                        }
                    //Log.i("posts", _posts.toString());
                    //Toast.makeText(HomeActivity.this, ""+_posts.size(), Toast.LENGTH_SHORT).show();
                    adapter=new PostAdapter(HomeActivity.this,_posts);
                    listView.setAdapter(adapter);
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nearbySwitch =findViewById(R.id.swich_near);
        nearbySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nearbySwitch.isEnabled()){
                    adapter=new PostAdapter(HomeActivity.this,_nearbyPosts);
                    listView.setAdapter(adapter);
                }else {
                    adapter=new PostAdapter(HomeActivity.this,_posts);
                    listView.setAdapter(adapter);
                }
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility();
                setAnimation();
                setClickable();
                isClicked=!isClicked;
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent=new Intent(getBaseContext(),AddActivity.class);
                addIntent.putExtra("location",_location);
                startActivity(addIntent);
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),ProfileActivity.class));
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("id","none");
                edit.apply();
                sendUserToLogin();            }
        });

    }

    private void setClickable() {
        if (!isClicked){
            btnLogout.setClickable(true);
            btnProfile.setClickable(true);
            btnAdd.setClickable(true);
        }else{
            btnLogout.setClickable(false);
            btnProfile.setClickable(false);
            btnAdd.setClickable(false);
        }
    }

    private void setAnimation() {
        if (!isClicked){
            btnMenu.startAnimation(rotateOpenAnimation);
            btnAdd.startAnimation(fromBottomAnimation);
            btnProfile.startAnimation(fromBottomAnimation);
            btnLogout.startAnimation(fromBottomAnimation);
        }else{
            btnMenu.startAnimation(rotateCloseAnimation);
            btnAdd.startAnimation(toBottomAnimation);
            btnProfile.startAnimation(toBottomAnimation);
            btnLogout.startAnimation(toBottomAnimation);
        }
    }

    private void setVisibility() {
        if (!isClicked){
            btnLogout.setVisibility(View.VISIBLE);
            btnProfile.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.VISIBLE);
        }else{
            btnLogout.setVisibility(View.INVISIBLE);
            btnProfile.setVisibility(View.INVISIBLE);
            btnAdd.setVisibility(View.INVISIBLE);
        }
    }


    private void sendUserToLogin() {
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        /*loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);*/
        startActivity(loginIntent);
        finish();
    }












    //all the below functions are for getting location
    // have no time to comment each
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_COARSE_LOCATION,ACCESS_FINE_LOCATION}, 1);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    _location=location.getLatitude()+","+location.getLongitude();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            _location=mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
        }
    };






}

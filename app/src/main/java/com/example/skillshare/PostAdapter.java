package com.example.skillshare;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostAdapter extends BaseAdapter {
    List<Post> posts;
    Context context;
    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database posts to add the process inside
    DatabaseReference dataRefUser=database.getReference().child("user");
    Query queryGetUser;
    public PostAdapter(Context context, List<Post> donations) {
        this.posts =donations;
        this.context=context;
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Post getItem(int position) {
        return posts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.item_row,parent,false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        Post post = getItem(position);
        holder.tvCity.setText(getCityFromCord(post.getLocation()));
        String userId=post.getUser();
        final String[] phoneNumber = new String[1];
        queryGetUser=dataRefUser.orderByChild("id").equalTo(userId);
        final ViewHolder finalHolder = holder;
        queryGetUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Map> root = (Map) snapshot.getValue();
                Collection<Map> users=  root.values();
                for (final Map<String,String> item:users) {
                    finalHolder.tvName.setText(item.get("fullName"));
                    finalHolder.ivCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.get("mobileNum")));
                            context.startActivity(intent);
                        }
                    });
                    //phoneNumber[0] =item.get("mobileNum");
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        holder.tvTime.setText(post.getTime());
        holder.tvDesc.setText(post.getDescription());
        holder.ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return convertView;

    }
    String getCityFromCord(String coords){
        String[] location_a=coords.split(",");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(location_a[0]),
                    Double.parseDouble(location_a[1]), 1);
            String address = addresses.get(0).getAddressLine(0);
            String[] listAddress=address.split(",");
            String cityName=listAddress[listAddress.length-3];
            // -1 for the country , -2 for the governorate, -3 city , and so on
            Log.i("locationHere",cityName);
            return cityName;
        } catch (IOException e) {
            Log.i("locationHere",e.getMessage());
        }
        return  "";
    }


}

class  ViewHolder{
    TextView tvCity, tvDesc,tvName,tvTime;
    ImageView ivCall;
    public ViewHolder(View view) {
        tvCity = view.findViewById(R.id.tv_city);
        tvDesc =view.findViewById(R.id.tv_desc);
        tvName=view.findViewById(R.id.tv_user_name);
        tvTime=view.findViewById(R.id.tv_time);
        ivCall=view.findViewById(R.id.iv_call);
    }

}

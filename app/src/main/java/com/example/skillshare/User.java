package com.example.skillshare;

public class User {
    String id;
    String fullName;
    String mobileNum;
    String password;

    //constructor for authentication
    public User(String id, String fullName, String mobileNum, String password) {
        this.id=id;
        this.fullName = fullName;
        this.mobileNum = mobileNum;
        this.password = password;
    }


    //getters for all fields
    //setter for the location because it can't be initialed in a constructor

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public String getPassword() {
        return password;
    }




    //functions

    //

}

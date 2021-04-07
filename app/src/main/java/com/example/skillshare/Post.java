package com.example.skillshare;

public class Post {
    String id, user, location, time,description;

    public Post(String id, String user, String location, String time, String description) {
        this.id = id;
        this.user = user;
        this.location = location;
        this.time = time;
        this.description=description;
    }


    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

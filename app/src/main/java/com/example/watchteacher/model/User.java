package com.example.watchteacher.model;

public class User {

    //all users have:
    private int id;
    private String name;
    private String email;
    private String phone;
    private int user_type;
    private int managerId;


    //for teachers only
    private double lat;
    private double lon;


    //manager register result
    public User(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    //teacher register result
    public User(int id, String name, String email, String phone, int managerId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.managerId = managerId;
    }

    //for getting teachers list (current lat and lon)
    public User(String name, int id, String phone, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}

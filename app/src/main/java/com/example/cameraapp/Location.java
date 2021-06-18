package com.example.cameraapp;

public class Location {

    double lat;
    double lng;
    String location;

    public Location(double lat, double lon, String place) {
        this.lat = lat;
        this.lng = lon;
        this.location = place;
    }

}
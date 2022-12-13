package com.example.maps;

import com.google.android.gms.maps.model.LatLng;

public class MyItem implements ClusterItem, com.google.maps.android.clustering.ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;

    public MyItem(double lat, double lng, String title, String snippet) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
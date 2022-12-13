package com.example.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * ClusterItem represents a marker on the map.
 */
public interface ClusterItem {

    /**
     * The position of this marker. This must always return the same value.
     */
    @NonNull LatLng getPosition();

    /**
     * The title of this marker.
     */
    @Nullable String getTitle();

    /**
     * The description of this marker.
     */
    @Nullable String getSnippet();
}
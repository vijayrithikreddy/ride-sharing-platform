package com.rideshare.rideservice.util;

import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.LatLng;

import java.util.List;

public class PolylineUtils {

    public static List<LatLng> decode(String encodedPolyline) {

        List<LatLng> points =
                PolylineEncoding.decode(encodedPolyline);

        return points;
    }
}
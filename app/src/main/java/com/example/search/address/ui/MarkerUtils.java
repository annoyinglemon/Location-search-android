package com.example.search.address.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.example.search.address.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

class MarkerUtils {

    static BitmapDescriptor createDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    static PolygonOptions createStarForCoordinates(Context context, LatLng latLng) {
        double x = 0.00005d;
        double lat = latLng.latitude;
        double lon = latLng.longitude;

        LatLng point1 = new LatLng(lat + (x * 1.5d), lon);
        LatLng point2 = new LatLng(lat + (x * 0.5d), lon - (x * 0.55d));
        LatLng point3 = new LatLng(lat + (x * 0.5d), lon - (x * 2d));
        LatLng point4 = new LatLng(lat - (x * 0.17d), lon - (x * 0.75d));
        LatLng point5 = new LatLng(lat - (x * 1.5d), lon - (x * 1.4d));
        LatLng point6 = new LatLng(lat - (x * 0.55d), lon);
        LatLng point7 = new LatLng(lat - (x * 1.5d), lon + (x * 1.4d));
        LatLng point8 = new LatLng(lat - (x * 0.17d), lon + (x * 0.75d));
        LatLng point9 = new LatLng(lat + (x * 0.5d), lon + (x * 2d));
        LatLng point10 = new LatLng(lat + (x * 0.5d), lon + (x * 0.55d));

        return new PolygonOptions().add(point1, point2, point3, point4, point5,
                point6, point7, point8, point9, point10)
                .strokeColor(ContextCompat.getColor(context, R.color.colorStar))
                .fillColor(ContextCompat.getColor(context, R.color.colorStar));
    }


}

package com.nabeel.navigateorder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.nabeel.navigateorder.adapter.OrderAdapter;
import com.nabeel.navigateorder.model.Order;
import com.nabeel.navigateorder.service.OrderListner;
import com.nabeel.navigateorder.service.OrderServiceClass;
import com.nabeel.navigateorder.utils.GpsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements OrderListner {

    OrderServiceClass service;

    List<Order> orders=new ArrayList<Order>();
    private OrderAdapter orderAdapter;
    private RecyclerView recyclerViewOrder;

    private GoogleApiClient googleApiClient;

    boolean isGPS=false;
    private GoogleMap mMap;
    Button btnNavigate;
    ProgressBar loading;
    Boolean isSorted=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewOrder=findViewById(R.id.recyclerViewOrder);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        orderAdapter = new OrderAdapter(orders, this,"admin",geocoder);
        recyclerViewOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewOrder.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOrder.setAdapter(orderAdapter);
        btnNavigate=findViewById(R.id.btnNavigate);
        loading=findViewById(R.id.loading);

        //Here Iam use a logic first click to sort and second click to navigate
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orders.size()>0)
                {
                    if (!isSorted) {
                        Collections.sort(orders);
                        orderAdapter.notifyDataSetChanged();
                        isSorted=true;
                    }
                    else {
                        startActivity(new Intent(MainActivity.this,MapsActivity.class)
                                .putExtra("from","main")
                                .putExtra("data",new Gson().toJson(orders))
                        );
                    }
                }
            }
        });



        service=new OrderServiceClass(this);
        service.getOrders();
        showLocationPermission();
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
                if (isGPSEnable)
                {
                    Log.i("GPS","Enable----------------------------");
                }
                else
                {
                    Log.i("GPS","Disable----------------------------");
                }
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
        else
        {
            if (requestCode == Constants.GPS_REQUEST) {
                finish();
            }

        }
    }

    final int PERMISSION_REQUEST_COARSE_LOCATION=100;

    private void showLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permission");
                builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsProviderEnabled, isNetworkProviderEnabled;
            isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGpsProviderEnabled && !isNetworkProviderEnabled) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permission");
                builder.setMessage("The app needs location permissions. Please grant this permission to continue using the features of the app.");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("App", "coarse location permission granted");
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        }
    }


    @Override
    public void getOrderResponse(List<Order> body) {
        loading.setVisibility(View.GONE);
        btnNavigate.setVisibility(View.VISIBLE);
        orders.clear();
        orders.addAll(body);
        orderAdapter.notifyDataSetChanged();


    }

    @Override
    public void onFailure(String s) {
        loading.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}

package com.nabeel.navigateorder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nabeel.navigateorder.model.Order;
import com.nabeel.navigateorder.utils.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.fragment.app.FragmentActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TextView txtName,txtCreatedAt;
    Button btnNavigate;
    CircularImageView imgProPic;
    Order order;

    List<Order> orderList=new ArrayList<Order>();
    int orderPosition=0;
    String from="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        txtName=findViewById(R.id.txtName);
        txtCreatedAt=findViewById(R.id.txtCreatedAt);
        imgProPic=findViewById(R.id.imgProPic);
        btnNavigate=findViewById(R.id.btnNavigate);

        Intent intent=getIntent();
        if (intent.hasExtra("order"))
        {
            order=new Gson().fromJson(intent.getStringExtra("order"),Order.class);
        }

        if (intent.hasExtra("from"))
        {
            from=intent.getStringExtra("from");
        }
        if (from!=null)
        if (from.equals("adapter"))
        {
            btnNavigate.setVisibility(View.GONE);
        }
        if (intent.hasExtra("data"))
        {
            Type orderListType = new TypeToken<ArrayList<Order>>(){}.getType();
            orderList=new Gson().fromJson(intent.getStringExtra("data"),orderListType);
            if (orderList.size()>0)
                order=orderList.get(0);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment!=null)
        mapFragment.getMapAsync(this);



        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderPosition++;
                if (orderList.size()>orderPosition)
                {
                    order=orderList.get(orderPosition);
                    showOtpConfirmation(order);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Navigation Completed",Toast.LENGTH_SHORT).show();
                    btnNavigate.setVisibility(View.GONE);
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        showOtpConfirmation(order);
    }


    public String getDate(String date)
    {
        if (date!=null)
            try{

                SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date newDate=spf.parse(date);
                spf= new SimpleDateFormat("d MMM yyyy hh:mm a");
                date = spf.format(newDate);
                return date;

            } catch (ParseException e){
                try {
                    SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date newDate=spf.parse(date);
                    spf= new SimpleDateFormat("d MMM yyyy hh:mm a");
                    date = spf.format(newDate);
                    return date;
                }
                catch (ParseException e1)
                {
                    e1.printStackTrace();
                    return date;
                }
            }
        else
            return "";
    }

    AlertDialog dialog;
    public void showOtpConfirmation(final Order order) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder mBuilder= new AlertDialog.Builder(this);
        View mView=inflater.inflate(R.layout.otp_confirmation_dialog,null);
        mBuilder.setView(mView);
        TextView txtName1=mView.findViewById(R.id.txtName);
        CircularImageView proPic1=mView.findViewById(R.id.imgProPic);
        final EditText edtOtp=mView.findViewById(R.id.edtOtp);
        Button btnConfirm=mView.findViewById(R.id.btnConfirm);

        txtName.setText(order.getName());
        txtCreatedAt.setText(getDate(order.getCreatedAt()));
        Picasso.with(getApplicationContext()).load(order.getAvatar()).placeholder(R.drawable.default_pro_pic).into(imgProPic);

        txtName1.setText(order.getName());
        Picasso.with(getApplicationContext()).load(order.getAvatar()).placeholder(R.drawable.default_pro_pic).into(proPic1);



        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp=edtOtp.getText().toString().trim();
               if (otp.isEmpty())
               {
                   edtOtp.setError("Required");
               }
               else{
                   if (otp.equals(order.getOtp()+"")){
                       Toast.makeText(getApplicationContext(),"Correct Otp",Toast.LENGTH_SHORT).show();
                   }
                   else {
                       Toast.makeText(getApplicationContext(),"Incorrect Otp",Toast.LENGTH_SHORT).show();
                   }
                   setUi(order);
                   dialog.dismiss();

               }
            }
        });


        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });


        dialog.setOnKeyListener(new Dialog.OnKeyListener()
        {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    //dialog.dismiss();

                }
                return true;
            }
        });
        dialog.show();

    }

    private void setUi(Order order) {
        LatLng orderLocation = new LatLng(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitute()));
        //mMap.addMarker(new MarkerOptions().position(mylocation).snippet("TEXT BELLOW TITLE").title("TITLE")).showInfoWindow();


        mMap.clear();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitute()), 1);
            if (addresses.size()>0)
            {
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);

                if (cityName!=null)
                {
                    mMap.addMarker(new MarkerOptions().position(orderLocation).title(cityName));
                }
                else if (stateName!=null)
                {
                    mMap.addMarker(new MarkerOptions().position(orderLocation).title(stateName));
                }
                else if (countryName!=null)
                {
                    mMap.addMarker(new MarkerOptions().position(orderLocation).title(countryName));
                }
                else {
                    mMap.addMarker(new MarkerOptions().position(orderLocation).title("Order Location"));
                }

            }
            else {
                mMap.addMarker(new MarkerOptions().position(orderLocation).title("Order Location"));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(orderLocation));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

package com.nabeel.navigateorder.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nabeel.navigateorder.MapsActivity;
import com.nabeel.navigateorder.R;
import com.nabeel.navigateorder.model.Order;
import com.nabeel.navigateorder.utils.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private List<Order> orderList;
    Context context;
    String from;
    Geocoder geocoder;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView imgProPic;
        public LinearLayout parentLayout;
        public TextView txtName,txtLocation,txtCreatedAt;

        public MyViewHolder(View view) {
            super(view);

            imgProPic=view.findViewById(R.id.imgProPic);
            txtName=view.findViewById(R.id.txtName);
            txtLocation=view.findViewById(R.id.txtLocation);
            txtCreatedAt=view.findViewById(R.id.txtTime);
            parentLayout=view.findViewById(R.id.parentLayout);

        }
    }


    public OrderAdapter(List<Order> orderList, Context context, String from, Geocoder geocoder) {
        this.orderList = orderList;
        this.context=context;
        this.from=from;
        this.geocoder=geocoder;
    }

    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);

        return new OrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrderAdapter.MyViewHolder holder, final int position) {

        final Order order=orderList.get(position);
        holder.txtName.setText(order.getName());
        holder.txtCreatedAt.setText(getDate(order.getCreatedAt()));
        try {
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(Double.parseDouble(order.getLatitude()), Double.parseDouble(order.getLongitute()), 1);
            if (addresses.size()>0) {
                String cityName = addresses.get(0).getAddressLine(0);
                String stateName = addresses.get(0).getAddressLine(1);
                String countryName = addresses.get(0).getAddressLine(2);
                if (cityName!=null)
                    holder.txtLocation.setText(cityName);
                else if (stateName!=null)
                    holder.txtLocation.setText(stateName);
                else if (countryName!=null)
                    holder.txtLocation.setText(countryName);
            }
            else {
                holder.txtLocation.setText("Lat:"+order.getLatitude() + "\n Log:" + order.getLongitute());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (order.getAvatar()!=null&&!order.getAvatar().isEmpty())
            Picasso.with(context).load(order.getAvatar()).placeholder(R.drawable.default_pro_pic).fit().into(holder.imgProPic);


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, MapsActivity.class)
                        .putExtra("from","adapter")
                        .putExtra("otp",order.getOtp())
                        .putExtra("order",new Gson().toJson(order)));
            }
        });

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

    // Function to remove duplicates from an ArrayList
    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> list)
    {

        // Create a new ArrayList
        ArrayList<T> newList = new ArrayList<T>();

        // Traverse through the first list
        for (T element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
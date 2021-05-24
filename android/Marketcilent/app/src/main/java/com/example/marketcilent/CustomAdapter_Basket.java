package com.example.marketcilent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.marketcilent.model.Basket;
import com.example.marketcilent.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter_Basket extends ArrayAdapter<Basket> {




    List<Basket> baskets;
    int custom_layout_id;

    public CustomAdapter_Basket(@NonNull Context context, int resource, @NonNull List<Basket> objects) {
        super(context, resource, objects);
        baskets = objects;
        custom_layout_id = resource;

    }

    @Override
    public int getCount() {
        return baskets.size();
    }

    @SuppressLint("CheckResult")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            // getting reference to the main layout and
            // initializing
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(custom_layout_id, null);
        }


        ImageView imageView = v.findViewById(R.id.imageViewbasketItem);
        TextView productName = v.findViewById(R.id.basketProductName);
        TextView detail = v.findViewById(R.id.basketDetail);
        TextView quantity = v.findViewById(R.id.basketQuantityName);
        TextView price = v.findViewById(R.id.basketProductPrice);


        // get the item using the  position param
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_baseline_extension_24);

        Basket basket= baskets.get(position);

        Uri imageuri = Uri.parse(basket.getProductUri());

        Glide.with(imageView.getContext()).setDefaultRequestOptions(requestOptions)
                .load(imageuri)
                .into(imageView);
        // imageView.setImageResource();
        productName.setText(basket.getProductName());
        detail.setText(basket.getProductDetail());
        price.setText(basket.getProductPrice());
        quantity.setText(basket.getPquantity());



        return v;
    }
}
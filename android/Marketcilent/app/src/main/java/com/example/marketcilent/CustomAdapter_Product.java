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
import java.util.List;
import com.example.marketcilent.model.Product;


public class CustomAdapter_Product extends ArrayAdapter<Product>{
    List<Product> products;
    int custom_layout_id;

    public CustomAdapter_Product(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        products = objects;
        custom_layout_id = resource;
    }
    @Override
    public int getCount() {
        return products.size();
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

        // initializing the imageview and textview and
        // setting data
        ImageView imageView = v.findViewById(R.id.imageView);
        TextView textView = v.findViewById(R.id.textView);

        // get the item using the  position param
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_baseline_extension_24);
        Product product = products.get(position);
        Uri imageuri = Uri.parse(product.getUri());

        Glide.with(imageView.getContext()).setDefaultRequestOptions(requestOptions)
                .load(imageuri)
                .into(imageView);

        textView.setText(product.getProductName());
        return v;
    }

}











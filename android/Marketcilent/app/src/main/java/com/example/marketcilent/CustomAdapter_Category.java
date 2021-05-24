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
import com.example.marketcilent.model.Category;
import java.util.List;

public class CustomAdapter_Category extends ArrayAdapter<Category> {

    List<Category> categories;
    int custom_layout_id;

    public CustomAdapter_Category(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        categories = objects;
        custom_layout_id = resource;
    }

    @Override
    public int getCount() {
        return categories.size();
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
        requestOptions.placeholder(R.drawable.ic_baseline_category_24);
        Category category = categories.get(position);
        Uri imageuri = Uri.parse(category.getUri());

        Glide.with(imageView.getContext()).setDefaultRequestOptions(requestOptions)
                .load(imageuri)
                .into(imageView);
        // imageView.setImageResource();
        textView.setText(category.getName());
        return v;
    }
}
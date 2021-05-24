package com.example.marketcilent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.marketcilent.model.Category;
import com.example.marketcilent.model.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsList extends AppCompatActivity {

    List<String> uid;
    String cuid;
    List<Product> products ;
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);
        uid = new ArrayList<>();
        products = new ArrayList<Product>();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            cuid = extras.getString("cuid");
            // and get whatever type user account id is
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product").child(cuid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                uid.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    products.add(product);
                    uid.add(ds.getKey());
                }

                gridView = findViewById(R.id.grid_view_products);
                CustomAdapter_Product customAdapter = new CustomAdapter_Product(ProductsList.this, R.layout.gridview_item, products);
                gridView.setAdapter(customAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ProductsList.this,ProductDetail.class);
                        intent.putExtra("cuid",cuid);
                        intent.putExtra("uid",uid.get(position));
                        startActivity(intent);

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
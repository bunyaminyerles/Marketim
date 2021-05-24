package com.example.marketcilent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.marketcilent.model.Basket;
import com.example.marketcilent.model.Client;
import com.example.marketcilent.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener {
    String cuid, uid;
    Product product;
    ImageView imageView;
    TextView productName,productDetail,productPrice,productQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        imageView = findViewById(R.id.productimageView);
        productName = findViewById(R.id.productName);
        productDetail = findViewById(R.id.productDetail);
        productPrice = findViewById(R.id.productPrice);
        productQuantity = findViewById(R.id.productQuantity);
        Basket basket;

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            cuid = extras.getString("cuid");
            uid = extras.getString("uid");
            // and get whatever type user account id is
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product").child(cuid).child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
                if (product != null) {



                    // get the item using the  position param
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.ic_baseline_extension_24);
                    Uri imageuri = Uri.parse(product.getUri());

                    Glide.with(imageView.getContext()).setDefaultRequestOptions(requestOptions)
                            .load(imageuri)
                            .into(imageView);

                    productName.setText(product.getProductName());
                    productDetail.setText(product.getUnitDetail());
                    productPrice.setText(product.getPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int i =Integer.parseInt((String) productQuantity.getText());
        switch (v.getId()){
            case R.id.minus:
                if (i>1){
                    i--;
                    String b=String.valueOf(i);
                    productQuantity.setText(b);}
                break;
            case R.id.plus:
                if (i<5){
                    i++;
                    String a=String.valueOf(i);
                    productQuantity.setText(a);}
                break;
            case R.id.addTocart:
                basket_add();
                break;
        }
    }

    public void basket_add(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        Basket basket = new Basket(productQuantity.getText().toString(),productName.getText().toString(),productDetail.getText().toString(),productPrice.getText().toString(),product.getUri());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Basket").child(user.getUid()).child(uid);
        reference.setValue(basket).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProductDetail.this, "Sepete Eklendi", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetail.this, "Sepete Eklenemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
package com.example.marketmanaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.marketmanaging.model.Category;
import com.example.marketmanaging.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class ProductRemove extends AppCompatActivity {

    List<String> cuid;
    List<String> clist;
    String categoryUid;
    List<String> uid;
    List<String> list;
    String selectedUid;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar progressBar;
    ListView clistView,listView;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter1;
    int position1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ürün Sil");
        setContentView(R.layout.activity_product_remove);


        clistView = findViewById(R.id.productRemoveCategoryList);
        listView = findViewById(R.id.productRemoveProductList);

        progressBar = findViewById(R.id.progressBar3);
        spinnerFull();


    }


    public void spinnerFull() {

        clist = new ArrayList<String>();
        cuid = new ArrayList<String>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                cuid.clear();
                clist.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);
                    cuid.add(ds.getKey());
                    clist.add(category.getName());

                }

                adapter = new ArrayAdapter<String>(ProductRemove.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, clist);
                clistView.setAdapter(adapter);
                clistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // TODO Auto-generated method stub
                        categoryUid = cuid.get(position);
                        position1 = position;
                        String value = adapter.getItem(position);
                        Toast.makeText(getApplicationContext(), value, LENGTH_SHORT).show();
                        productFull();


                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void productFull ( ){
        list = new ArrayList<String>();
        uid = new ArrayList<String>();
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Product")
                .child(categoryUid);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                uid.clear();
                list.clear();
                for (DataSnapshot ds1 : snapshot1.getChildren()) {
                    Product product = ds1.getValue(Product.class);
                    uid.add(ds1.getKey());
                    list.add(product.getProductName()+" "+product.getUnitDetail());
                }
                adapter1 = new ArrayAdapter<String>(ProductRemove.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, list);
                listView.setAdapter(adapter1);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // TODO Auto-generated method stub
                        selectedUid = uid.get(position);
                        position1 = position;
                        String value = adapter1.getItem(position);
                        Toast.makeText(getApplicationContext(), value, LENGTH_SHORT).show();
                        removeFunction();


                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeFunction() {
        View view = LayoutInflater.from(ProductRemove.this).inflate(R.layout.dialog_product_remove, null);
        Button buttonYes = view.findViewById(R.id.yesP);
        Button buttonNo = view.findViewById(R.id.noP);
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductRemove.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();
                StorageReference ref
                        = storageReference
                        .child("Product").child(selectedUid);

                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product").child(categoryUid).child(selectedUid);
                        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                spinnerFull();
                                productFull();
                                Toast.makeText(ProductRemove.this, "Ürün Silindi ", LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(ProductRemove.this, "Hata " + e.getMessage(), LENGTH_SHORT).show();
                            }
                        });


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialog.dismiss();
                        Toast.makeText(ProductRemove.this, "Hata " + exception.getMessage(), LENGTH_SHORT).show();
                    }
                });
            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}

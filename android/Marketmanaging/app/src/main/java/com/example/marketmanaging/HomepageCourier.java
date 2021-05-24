package com.example.marketmanaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.marketmanaging.model.Category;
import com.example.marketmanaging.model.Courier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.widget.Toast.LENGTH_SHORT;

public class HomepageCourier extends AppCompatActivity {

    Map<String, List<String>> clientProduct;
    List<String> productList;
    List<String> clientList;

    ListView listView;
    ArrayAdapter<String> adapter;
    String check;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_courier);
        productList = new ArrayList<>();
        clientList = new ArrayList<>();
        clientProduct = new HashMap<String, List<String>>();
        listView = findViewById(R.id.listViewClientOrder);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clientList.clear();
                productList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    clientList.add(ds.getKey());
                    productList.clear();
                    for (DataSnapshot dss : ds.getChildren()) {
                        if (dss.child("state").getValue().equals("Kurye Bekleniyor")) {
                            productList.add(dss.getKey());
                            check = "yes";
                        }else {
                            check = "no";
                        }

                    }
                        if (check=="yes"){
                    clientProduct.put(ds.getKey(), productList);}
                        else{
                            clientList.remove(clientList.size()-1);
                        }
                }


                adapter = new ArrayAdapter<String>(HomepageCourier.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, clientList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        clientList.get(position);
                        List<String> aa = clientProduct.get(clientList.get(position));
                        System.out.println(aa);
                        for (String str : aa) {
                            System.out.println(str);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders")
                                    .child(clientList.get(position)).child(str).child("state");
                            reference.setValue("Hazırlanıyor");

                            Intent intent = new Intent(HomepageCourier.this, CourierOrder.class);
                            intent.putExtra("cuid",clientList.get(position));
                            intent.putStringArrayListExtra("plist", (ArrayList<String>) productList);
                            startActivity(intent);



                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottommenuc, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.signOut:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomepageCourier.this, MainActivity.class));
        finish();
    }
}
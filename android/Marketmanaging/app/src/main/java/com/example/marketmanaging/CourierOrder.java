package com.example.marketmanaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.marketmanaging.model.Basket;
import com.example.marketmanaging.model.Client;
import com.example.marketmanaging.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CourierOrder extends AppCompatActivity {

    String cuid;
    List<String> plist;
    TextView clientName, clientAdress;
    List<String> list;
    ArrayAdapter<String> adapter, adapter1;
    ListView listView, listView1;
    List<String> stateList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_order);
        list = new ArrayList<>();
        clientName = findViewById(R.id.clientName);
        clientAdress = findViewById(R.id.clientAdress);
        listView = findViewById(R.id.listViewClientOrder);
        listView1 = findViewById(R.id.orderState);
        stateList = new ArrayList<>();
        stateList.add("Yolda");
        stateList.add("Teslim Edildi");
        stateList.add("İptal edildi");

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            cuid = extras.getString("cuid");
            plist = extras.getStringArrayList("plist");
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Client").child(cuid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Client client = snapshot.getValue(Client.class);
                clientName.setText(client.getName());
                clientAdress.setText(client.getAdress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        for (String str : plist) {
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Orders").child(cuid).child(str).child("basket");
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Basket basket = snapshot.getValue(Basket.class);
                    String string = basket.getProductName() + "\t" + basket.getProductDetail() + "\t" + basket.getPquantity() + " adet";
                    list.add(string);
                    adapter = new ArrayAdapter<String>(CourierOrder.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, list);
                    listView.setAdapter(adapter);
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        adapter1 = new ArrayAdapter<String>(CourierOrder.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, stateList);
        listView1.setAdapter(adapter1);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String state = stateList.get(position);
                switch (state) {
                    case "Yolda":
                        for (String str : plist) {
                            DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Orders").child(cuid)
                                    .child(str).child("state");
                            reference2.setValue("Yolda");
                        }
                        break;
                    case "Teslim Edildi":
                        for (String str : plist) {

                            DatabaseReference reference3 = FirebaseDatabase.getInstance().getReference("Orders").child(cuid)
                                    .child(str);
                            reference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Order order = snapshot.getValue(Order.class);
                                    order.setState("Teslim Edildi");
                                    DatabaseReference reference4 = FirebaseDatabase.getInstance().getReference("PastOrders").child(cuid)
                                            .child(str);
                                    reference4.setValue(order);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            reference3.removeValue();
                        }
                        break;
                    case "İptal edildi":
                        for (String str : plist) {

                            DatabaseReference reference5 = FirebaseDatabase.getInstance().getReference("Orders").child(cuid)
                                    .child(str);
                            reference5.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Order order = snapshot.getValue(Order.class);
                                    order.setState("İptal edildi");
                                    DatabaseReference reference6 = FirebaseDatabase.getInstance().getReference("PastOrders").child(cuid)
                                            .child(str);
                                    reference6.setValue(order);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            reference5.removeValue();
                        }
                        break;
                }
            }
        });
    }
}
package com.example.marketcilent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketcilent.R;
import com.example.marketcilent.model.Basket;
import com.example.marketcilent.model.Order;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class BasketActivity extends AppCompatActivity implements View.OnClickListener {

    List<Basket> baskets;
    GridView gridView;

    List<String> uid;
    String selectedUid;
    int position1;
    public static int total;
    TextView orderTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        uid = new ArrayList<String>();
        baskets = new ArrayList<>();
        baskets.clear();
        spinnerFull();
        orderTotal = findViewById(R.id.total);


    }

    public void spinnerFull() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Basket").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uid.clear();
                baskets.clear();
                total = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Basket basket = ds.getValue(Basket.class);
                    baskets.add(basket);
                    total += Integer.parseInt(basket.getPquantity()) * Integer.parseInt(basket.getProductPrice());
                    uid.add(ds.getKey());
                }
                orderTotal.setText(String.valueOf(total));
                gridView = findViewById(R.id.grid_view_basket);
                CustomAdapter_Basket customAdapter = new CustomAdapter_Basket(BasketActivity.this, R.layout.gridview_basket_item, baskets);
                gridView.setAdapter(customAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedUid = uid.get(position);
                        position1 = position;
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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        View view = LayoutInflater.from(BasketActivity.this).inflate(R.layout.dialog_basket, null);
        Button buttonYes = view.findViewById(R.id.yes);
        Button buttonNo = view.findViewById(R.id.no);
        AlertDialog.Builder builder = new AlertDialog.Builder(BasketActivity.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Basket/" + user.getUid() + "/" + selectedUid);
                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        spinnerFull();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(BasketActivity.this, "Hata " + e.getMessage(), LENGTH_SHORT).show();
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


    @Override
    public void onClick(View v) {

       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
         DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Orders").child(user.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String str = (String) ds.child("state").getValue();
                    if(str.isEmpty()){
                        for (int i = 0; i < uid.size(); i++) {
                            Order order = new Order(baskets.get(i), "Kurye Bekleniyor");

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Orders").child(user.getUid()).child(uid.get(i));
                            reference.setValue(order);

                            reference = FirebaseDatabase.getInstance().getReference("Basket").child(user.getUid());
                            reference.removeValue();
                            spinnerFull();
                            Toast.makeText(BasketActivity.this, "Sipriş verildi sipariş tutarı: " + total + "₺", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(BasketActivity.this,"Aktif bir siparişiniz var",Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });



    }

}



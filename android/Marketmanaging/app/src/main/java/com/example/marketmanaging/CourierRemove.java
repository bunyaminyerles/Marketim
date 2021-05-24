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
import com.example.marketmanaging.model.Courier;
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


public class CourierRemove extends AppCompatActivity {

    List<String> uid;
    List<String> list;
    String selectedUid;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressBar progressBar;
    ListView listView;
    ArrayAdapter<String> adapter;
    int position1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_remove);
        setTitle("Kurye Sil");

        listView = findViewById(R.id.listViewforCourier);
        progressBar = findViewById(R.id.progressBarCourierRemove);
        spinnerFull();


    }


    public void spinnerFull() {

        list = new ArrayList<String>();
        uid = new ArrayList<String>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Courier");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                uid.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Courier courier = ds.getValue(Courier.class);
                    uid.add(ds.getKey());
                    list.add(courier.getName());
                }
                adapter = new ArrayAdapter<String>(CourierRemove.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // TODO Auto-generated method stub
                        selectedUid = uid.get(position);
                        position1 = position;
                        removeFunction();
                        String value = adapter.getItem(position);
                        Toast.makeText(getApplicationContext(), value, LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeFunction() {
        View view = LayoutInflater.from(CourierRemove.this).inflate(R.layout.dialog_courier_remove, null);
        Button buttonYes = view.findViewById(R.id.yes);
        Button buttonNo = view.findViewById(R.id.no);
        AlertDialog.Builder builder = new AlertDialog.Builder(CourierRemove.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonYes.setOnClickListener(new View.OnClickListener() {

            
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Courier/" + selectedUid);
                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        spinnerFull();
                        dialog.dismiss();
                        Toast.makeText(CourierRemove.this, "Kurye Silindi ", LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(CourierRemove.this, "Hata " + e.getMessage(), LENGTH_SHORT).show();
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
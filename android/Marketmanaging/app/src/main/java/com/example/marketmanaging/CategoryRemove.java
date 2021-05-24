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

public class CategoryRemove extends AppCompatActivity {

    List<Category> categories;
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
        setContentView(R.layout.activity_category_remove);
        setTitle("Kategori Sil");

        listView = findViewById(R.id.listViewforCategory);
        progressBar = findViewById(R.id.progressBar2);
        categories = new ArrayList<Category>();
        spinnerFull();



    }


    public void spinnerFull() {

        list = new ArrayList<String>();
        uid = new ArrayList<String>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                list.clear();
                uid.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);
                    categories.add(category);
                    uid.add(ds.getKey());
                    list.add(category.getName());
                }
                adapter = new ArrayAdapter<String>(CategoryRemove.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // TODO Auto-generated method stub
                        selectedUid = uid.get(position);
                        position1 = position;
                        removeFunction();
                        String value=adapter.getItem(position);
                        Toast.makeText(getApplicationContext(),value, LENGTH_SHORT).show();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void removeFunction(){
        View view = LayoutInflater.from(CategoryRemove.this).inflate(R.layout.dialog_category_remove, null);
        Button buttonYes = view.findViewById(R.id.yes);
        Button buttonNo = view.findViewById(R.id.no);
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryRemove.this);
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
                        .child(
                                "Category/"
                                        + selectedUid);

                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category/"+selectedUid);
                        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                spinnerFull();

                                Toast.makeText(CategoryRemove.this, "Kategori Silindi ",LENGTH_SHORT).show();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product/"+selectedUid);
                                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        Toast.makeText(CategoryRemove.this, "Ürünler Silindi ",LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(CategoryRemove.this, "Hata " + e.getMessage(),LENGTH_SHORT).show();
                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialog.dismiss();
                        Toast.makeText(CategoryRemove.this, "Hata " + exception.getMessage(),LENGTH_SHORT).show();
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
package com.example.marketcilent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketcilent.model.Basket;
import com.example.marketcilent.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment {


    List<Product> products;
    GridView gridView;
    List<String> uid;
    List<String> cuid;
    List<String> list;
    String selectedUid;
    String selectedCuid;
    String selecttedList;
    int position1;
    EditText searchText;
    ViewGroup viewGroup;
    Button button;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.frag_search, container, false);
        products = new ArrayList<>();
        uid = new ArrayList<>();
        cuid=new ArrayList<>();
        list = new ArrayList<>();
        searchText = viewGroup.findViewById(R.id.search_bar);
        button = viewGroup.findViewById(R.id.search_button);
        categoryFull();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productFull();
            }
        });
        return viewGroup;
    }


    public void categoryFull() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uid.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    uid.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void productFull() {
        for (int i=0; i < uid.size(); i++) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product").child(uid.get(0));
            Query query = reference.orderByChild("productName").startAt(searchText.getText().toString()).endAt(searchText.getText().toString()+"\uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cuid.clear();
                    products.clear();
                    list.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        products.add(ds.getValue(Product.class));
                        cuid.add(snapshot.getKey());
                        list.add(ds.getKey());
                    }
                    gridView = viewGroup.findViewById(R.id.gridview_search);
                    CustomAdapter_Product customAdapter = new CustomAdapter_Product(getContext(), R.layout.gridview_item, products);
                    gridView.setAdapter(customAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getContext(),ProductDetail.class);
                            intent.putExtra("cuid",cuid.get(position));
                            intent.putExtra("uid",list.get(position));
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
}



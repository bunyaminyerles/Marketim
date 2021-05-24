package com.example.marketmanaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketmanaging.model.Category;
import com.example.marketmanaging.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;

public class ProductAdd extends AppCompatActivity {

    List<String> uid;
    List<String> list;
    String selectedUid;
    Product product;
    TextView categoryNamee;
    EditText productNamee, productPricee,productUnitDetaill;
    UUID uuid;
    private Button btnSelect, btnUpload;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    String categoryName,productName,productPrice,productUnitDetail;
    ProgressBar progressBar;
    ListView listView;
    ArrayAdapter<String> adapter;
    int position1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Ürün Ekle");
        setContentView(R.layout.activity_product_add);
        database = FirebaseDatabase.getInstance();

        btnSelect = findViewById(R.id.btnChooseProduct);
        btnUpload = findViewById(R.id.btnUploadProduct);
        imageView = findViewById(R.id.imgViewProduct);
        categoryNamee = findViewById(R.id.productAddCategoryName);
        productNamee = findViewById(R.id.productName);
        productPricee= findViewById(R.id.productPrice);
        productUnitDetaill = findViewById(R.id.productUnitDetail);
        listView = findViewById(R.id.listViewforProduct);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        // on pressing btnUpload uploadImage() is called
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryName = categoryNamee.getText().toString().trim();
                productName = productNamee.getText().toString().trim();
                productPrice = productPricee.getText().toString().trim();
                productUnitDetail = productUnitDetaill.getText().toString().trim();

                if (categoryName.isEmpty()) {
                    Toast.makeText(ProductAdd.this,"Kategori seçimi yapınız",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (productName.isEmpty()) {
                    productNamee.setError("Boş bırakılamaz");
                    productNamee.requestFocus();
                    return;
                }
                if (productPrice.isEmpty()) {
                    productPricee.setError("Boş bırakılamaz");
                    productPricee.requestFocus();
                    return;
                }
                if (productUnitDetail.isEmpty()) {
                    productUnitDetaill.setError("Boş bırakılamaz");
                    productUnitDetaill.requestFocus();
                    return;
                }
                if (filePath == null) {
                    Toast.makeText(ProductAdd.this, "Resim seçimi yapmadınız", Toast.LENGTH_LONG).show();
                    return;
                }

                uploadImage();

            }
        });
        spinnerFull();

    }

    private void SelectImage() {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    public void uploadImage() {


        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(ProductAdd.this);
            progressDialog.setTitle("Ekleniyor...");
            progressDialog.show();
            uuid = UUID.randomUUID();
            StorageReference ref
                    = storageReference
                    .child(
                            "Product/"
                                    + uuid.toString());


            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    uploadCategory(categoryName,productName,productPrice,productUnitDetail,uri.toString());
                                    progressDialog.dismiss();
                                    categoryNamee.setText("");
                                    productNamee.setText("");
                                    productPricee.setText("");
                                    productUnitDetaill.setText("");
                                    imageView.setImageResource(0);
                                }
                            });

                        }

                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ProductAdd.this,
                                            "Hata " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Resim Yüklendi"
                                                    + (int) progress + "%");
                                }
                            });

        }

    }

    public void uploadCategory(String categoryUid,String productName,String productPrice ,String productUnitDetail ,String url) {
        DatabaseReference myRef = database.getReference("Product/" +selectedUid+"/"+ uuid.toString());
        product = new Product(categoryUid,productName,productPrice,productUnitDetail, url);
        myRef.setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProductAdd.this, "Ürün Eklendi", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductAdd.this, "Ürün Eklenemedi", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void spinnerFull() {

        list = new ArrayList<String>();
        uid = new ArrayList<String>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Category");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                uid.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);
                    uid.add(ds.getKey());
                    list.add(category.getName());
                }
                adapter = new ArrayAdapter<String>(ProductAdd.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, list);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // TODO Auto-generated method stub
                        selectedUid = uid.get(position);
                        position1 = position;
                        categoryNamee.setText(list.get(position));
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
}
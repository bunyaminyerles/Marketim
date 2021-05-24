package com.example.marketmanaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.marketmanaging.model.Courier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class CourierAdd extends AppCompatActivity {


    private FirebaseAuth mAuth;
    EditText nameEdtTxt, emailEdtTxt, passwordEdtTxt;
    Button signUp;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courier_add);
        mAuth = FirebaseAuth.getInstance();
        setTitle("Kurye Ekle");

        emailEdtTxt = findViewById(R.id.courierEmail);
        passwordEdtTxt = findViewById(R.id.courierPsw);
        nameEdtTxt = findViewById(R.id.courierName);
        signUp = findViewById(R.id.courierSıgnup);
        progressBar = findViewById(R.id.progressBarSignUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCourier();
            }
        });


    }

    public void registerCourier(){
        String name = nameEdtTxt.getText().toString().trim();
        String email = emailEdtTxt.getText().toString().trim();
        String password = passwordEdtTxt.getText().toString().trim();

        if (name.isEmpty()) {
            nameEdtTxt.setError("Ad soyad boş bırakılamaz!");
            nameEdtTxt.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailEdtTxt.setError("E-posta boş bırakılamaz!");
            emailEdtTxt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEdtTxt.setError("Lütfen geçerli bir e-posta adresi giriniz!");
            emailEdtTxt.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordEdtTxt.setError("Şifre boş bırakılamaz!");
            passwordEdtTxt.requestFocus();
            return;
        }
        if (password.length() < 6) {
            passwordEdtTxt.setError("Şifre en az 6 karakterden oluşmalıdır!");
            passwordEdtTxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Courier courier = new Courier(name);
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            mAuth.getCurrentUser().updateProfile(request);

                            FirebaseDatabase.getInstance().getReference("Courier")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(courier).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                        Toast.makeText(CourierAdd.this, "Kayıt başarılı kurye E-postasını doğrulamalı", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        emailEdtTxt.setText("");
                                        passwordEdtTxt.setText("");
                                        nameEdtTxt.setText("");
                                    } else {
                                        Toast.makeText(CourierAdd.this, "Kayıt yapılamadı!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(CourierAdd.this, "Kurye daha önceden kayıt edilmiş!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CourierAdd.this, "Kayıt yapılamadı!", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}
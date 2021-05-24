package com.example.marketcilent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.marketcilent.model.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class Register_Page extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText nameEdtTxt, emailEdtTxt, passwordEdtTxt, adressEdtTxt;
    Button signUp;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        setTitle("Üye Ol");

        nameEdtTxt = findViewById(R.id.nameRegister);
        emailEdtTxt = findViewById(R.id.emailRegister);
        passwordEdtTxt = findViewById(R.id.pswRegister);
        adressEdtTxt = findViewById(R.id.adressRegister);
        signUp = findViewById(R.id.passwordReset);
        progressBar = findViewById(R.id.progressBarSingUp);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public void registerUser() {
        String name = nameEdtTxt.getText().toString().trim();
        String email = emailEdtTxt.getText().toString().trim();
        String password = passwordEdtTxt.getText().toString().trim();
        String adress = adressEdtTxt.getText().toString().trim();

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

        if (adress.isEmpty()) {
            adressEdtTxt.setError("Adres boş bırakılamaz!");
            adressEdtTxt.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Client client = new Client(name, email, adress);
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                            mAuth.getCurrentUser().updateProfile(request);

                            FirebaseDatabase.getInstance().getReference("Client")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(client).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                        Toast.makeText(Register_Page.this, "Kayıt başarılı E-postanızı doğrulayın", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        Intent intent = new Intent(Register_Page.this, MainActivity.class);
                                        intent.putExtra("email", email);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Register_Page.this, "Kayıt yapılamadı!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Register_Page.this, "Kullanıcı daha önceden kayıt edilmiş!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Register_Page.this, "Kayıt yapılamadı!", Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}
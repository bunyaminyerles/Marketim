package com.example.marketmanaging;


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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Password_Reset_Page extends AppCompatActivity {

    EditText emailEdtTxt;
    Button send;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        setTitle("Şifre Sıfırlama");

        setContentView(R.layout.activity_password_reset_page);
        send = findViewById(R.id.passwordReset);
        emailEdtTxt = findViewById(R.id.emailRegister);
        progressBar = findViewById(R.id.progressBarReset);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordReset();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            emailEdtTxt.setText(bundle.getString("email"));
        }
    }

    public void passwordReset(){
        String email = emailEdtTxt.getText().toString().trim();
        if (email.isEmpty()){
            emailEdtTxt.setError("E-posta boş bırakılamaz!");
            emailEdtTxt.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdtTxt.setError("Lütfen geçerli bir e-posta adresi giriniz!");
            emailEdtTxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);



        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Password_Reset_Page.this,"Şifre sıfırlama bağlantısı e-postana gönderildi.",Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    intent = new Intent(Password_Reset_Page.this,MainActivity.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Password_Reset_Page.this,"Böyle bir E-posta kayıtlı değil!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
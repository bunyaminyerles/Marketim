package com.example.marketcilent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketcilent.model.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailEdtTxt, passwordEdtTxt;
    Intent intent;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    Client client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        setTitle(" ");

        emailEdtTxt = findViewById(R.id.editTextEmail);
        passwordEdtTxt = findViewById(R.id.editTextPsw);
        progressBar = findViewById(R.id.progressBarSignIn);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null) {
            emailEdtTxt.setText(bundle.getString("email"));
        }

    }




    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.signUp:
                intent = new Intent(MainActivity.this, Register_Page.class);
                startActivity(intent);
                break;
            case R.id.pswReset:
                intent = new Intent(MainActivity.this, Password_Reset_Page.class);
                intent.putExtra("email", emailEdtTxt.getText().toString());
                startActivity(intent);
                break;
            case R.id.signIn:
                userLogIn();
                break;
            default:
                break;
        }
    }

    public void userLogIn(){
        String email = emailEdtTxt.getText().toString().trim();
        String password = passwordEdtTxt.getText().toString();

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
        if (password.isEmpty()){
            passwordEdtTxt.setError("Şifre boş bırakılamaz!");
            passwordEdtTxt.requestFocus();
            return;
        }
        if (password.length()<6) {
            passwordEdtTxt.setError("Şifre en az 6 karakterden oluşmalıdır!");
            passwordEdtTxt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Client");
                    reference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            client = snapshot.getValue(Client.class);
                            if (client!=null){
                                if (user.isEmailVerified()) {
                                    intent = new Intent(MainActivity.this, Home_Page.class);
                                    startActivity(intent);
                                    progressBar.setVisibility(View.GONE);
                                    finish();
                                }
                                else {
                                    user.sendEmailVerification();
                                    emailEdtTxt.setError("E-posta doğrulanmamış. Gelen kutunu kontol et!");
                                    emailEdtTxt.requestFocus();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                            else {
                                passwordEdtTxt.setError("E-posta veya şifre hatalı!");
                                passwordEdtTxt.setText("");
                                passwordEdtTxt.requestFocus();
                                progressBar.setVisibility(View.GONE);
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MainActivity.this,"Hata",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    passwordEdtTxt.setError("E-posta veya şifre hatalı!");
                    passwordEdtTxt.setText("");
                    passwordEdtTxt.requestFocus();
                    progressBar.setVisibility(View.GONE);
                }
        }
        });
    }
}
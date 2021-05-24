package com.example.marketcilent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home_Page extends AppCompatActivity implements View.OnClickListener {
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home__page);


        getSupportFragmentManager().beginTransaction().replace(R.id.HomepageFragment,new FragmentHomePage()).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomN);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment seciliFragment = null;

                // Hangi menüye tıklanmışsa onu tespit ediyoruz.
                switch (menuItem.getItemId()){
                    case R.id.homepage:
                        seciliFragment = new FragmentHomePage();
                        break;
                    case R.id.search_bar:
                        seciliFragment = new FragmentSearch();
                        break;
                    case R.id.settings:
                        seciliFragment = new FragmentProfile();
                        break;
                }
                // Tespit ettiğimiz fragment'i yayınlıyoruz.
                getSupportFragmentManager().beginTransaction().replace(R.id.HomepageFragment,seciliFragment).commit();

                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.basketMenu:
                Intent intent = new Intent(Home_Page.this,BasketActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Home_Page.this,MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.exit:
                logout();
                break;
            case R.id.pswEdit:
                changePasswordDialog();
                break;
            case R.id.pastOrders:
                intent= new Intent(Home_Page.this,PastOrders.class);
                startActivity(intent);
                break;
            case R.id.Orders:
                intent = new Intent(Home_Page.this,OrderActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void changePasswordDialog() {
        View view = LayoutInflater.from(Home_Page.this).inflate(R.layout.dialog_change_password, null);
        EditText oldpsw = view.findViewById(R.id.oldPsw);
        EditText newpsw = view.findViewById(R.id.newPsw);
        Button buttonupdate = view.findViewById(R.id.updatePsw);
        ProgressBar progressBar = view.findViewById(R.id.pswProgressBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(Home_Page.this);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        buttonupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldpsw.getText().toString().trim();
                String newPassword = newpsw.getText().toString().trim();

                if (oldPassword.isEmpty()) {
                    oldpsw.setError("Eski şifre boş bırakılamaz");
                    oldpsw.requestFocus();
                    return;
                }
                if (oldPassword.length()<6) {
                    oldpsw.setError("Şifre en az 6 karakterden oluşmalıdır!");
                    oldpsw.requestFocus();
                    return;
                }
                if (newPassword.isEmpty()) {
                    newpsw.setError("Yeni şifre boş bırakılamaz");
                    newpsw.requestFocus();
                    return;
                }
                if (newPassword.length()<6) {
                    newpsw.setError("Şifre en az 6 karakterden oluşmalıdır!");
                    newpsw.requestFocus();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);

                user.reauthenticate(authCredential)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                user.updatePassword(newPassword)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressBar.setVisibility(View.GONE);
                                                dialog.dismiss();
                                                Toast.makeText(Home_Page.this,"Şifre Güncellendi",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        dialog.dismiss();
                                        Toast.makeText(Home_Page.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                        Toast.makeText(Home_Page.this,"Hatalı eski şifre",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}
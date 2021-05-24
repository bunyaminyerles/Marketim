package com.example.marketmanaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomepageManager extends AppCompatActivity implements View.OnClickListener {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_manager);
        getSupportFragmentManager().beginTransaction().replace(R.id.HomepageFragment,new FragmentCategory()).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomN);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment seciliFragment = null;

                switch (menuItem.getItemId()){
                    case R.id.category:
                        seciliFragment = new FragmentCategory();
                        break;
                    case R.id.settings:
                        seciliFragment = new FragmentSettings();
                        break;
                    case R.id.product:
                        seciliFragment = new FragmentProduct();
                        break;
                    case R.id.courier:
                        seciliFragment = new FragmentCourier();
                        break;
                    default:
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.HomepageFragment,seciliFragment).commit();

                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pswEdit:
                changePasswordDialog();
                break;
            case R.id.exit:
                logout();
                break;
            case R.id.categoryRemove:
                 intent = new Intent(HomepageManager.this,CategoryRemove.class);
                startActivity(intent);
                break;
            case R.id.categoryAdd:
                 intent = new Intent(HomepageManager.this,CategoryAdd.class);
                startActivity(intent);
                break;
            case R.id.productAdd:
                intent = new Intent(HomepageManager.this,ProductAdd.class);
                startActivity(intent);
                break;
            case R.id.productRemove:
                intent = new Intent(HomepageManager.this,ProductRemove.class);
                startActivity(intent);
                break;
            case R.id.courierAdd:
                intent = new Intent(HomepageManager.this,CourierAdd.class);
                startActivity(intent);
                break;
            case R.id.courierRemove:
                intent = new Intent(HomepageManager.this,CourierRemove.class);
                startActivity(intent);
                break;
        }
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomepageManager.this,MainActivity.class));
        finish();
    }

    public void changePasswordDialog() {
        View view = LayoutInflater.from(HomepageManager.this).inflate(R.layout.dialog_change_password, null);
        EditText oldpsw = view.findViewById(R.id.oldPsw);
        EditText newpsw = view.findViewById(R.id.newPsw);
        Button buttonupdate = view.findViewById(R.id.updatePsw);

        AlertDialog.Builder builder = new AlertDialog.Builder(HomepageManager.this);
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
                                                dialog.dismiss();
                                                Toast.makeText(HomepageManager.this,"Şifre Güncellendi",Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(HomepageManager.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(HomepageManager.this,"Hatalı eski şifre",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}
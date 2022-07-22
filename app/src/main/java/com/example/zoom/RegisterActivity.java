package com.example.zoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText namaEt,EmailEt,PassEt,konfEt;
    private Button RegisterEt;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        RegisterEt = findViewById(R.id.RegisterEt);
        namaEt = findViewById(R.id.namaEt);
        EmailEt = findViewById(R.id.EmailEt);
        PassEt = findViewById(R.id.PassEt);
        konfEt = findViewById(R.id.konfEt);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.app_name);
        progressDialog.setIcon(R.drawable.logo_depan);
        progressDialog.setCanceledOnTouchOutside(false);

        RegisterEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputData();
            }
        });
    }
    String nama,email,password,konfPass;
    private void inputData() {
        nama = namaEt.getText().toString().trim();
        email = EmailEt.getText().toString().trim();
        password = PassEt.getText().toString().trim();
        konfPass = konfEt.getText().toString().trim();
        if (TextUtils.isEmpty(nama)){
            Toast.makeText(RegisterActivity.this, "Nama Tidak Boleh Kosong!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this, "Email Tidak Boleh Kosong!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(RegisterActivity.this, "Email Tidak Sesuai Ketentuan.. Periksa Kembali!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Password Tidak Boleh Kosong!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (TextUtils.isEmpty(konfPass)){
            Toast.makeText(RegisterActivity.this, "Konfirmasi Password Tidak Boleh Kosong!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (!password.equals(konfPass)){
            Toast.makeText(RegisterActivity.this, "Konfirmasi Password Tidak Sesuai!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            progressDialog.setMessage("Membuat Akun...");
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    saveFirebase();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //failed creating account
                    Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveFirebase() {
        progressDialog.setMessage("Menyimpan Akun Info...");
        progressDialog.show();
        final String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", mAuth.getUid());
        hashMap.put("timestamp", timestamp);
        hashMap.put("nama", nama);
        hashMap.put("email", email);
        hashMap.put("password", password);
        hashMap.put("Konfirmasi Password", konfPass);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.child(mAuth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
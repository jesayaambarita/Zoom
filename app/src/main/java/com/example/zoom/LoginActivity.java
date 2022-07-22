package com.example.zoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private Button createBtn,loginBtn;
    private EditText emailEt,passEt;
    private FirebaseAuth mAuth;
    public static final String TAG = "TAG";
    private FirebaseFirestore fstore;
    private FirebaseDatabase firebaseDatabase;
    private ProgressDialog progressDialog;
    private TextView txt_lupa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createBtn = findViewById(R.id.createBtn);
        loginBtn = findViewById(R.id.loginBtn);
        emailEt = findViewById(R.id.emailEt);
        passEt = findViewById(R.id.passEt);
        fstore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.app_name);
        progressDialog.setIcon(R.drawable.logo_depan);
        progressDialog.setCanceledOnTouchOutside(false);
        txt_lupa = findViewById(R.id.txt_lupa);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEt.getText().toString().trim();
                String password = passEt.getText().toString().trim();
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this, "Email Tidak Boleh Kosong!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(LoginActivity.this, "Email Tidak Sesuai Ketentuan.. Periksa Kembali!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Password Tidak Boleh Kosong!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    progressDialog.setMessage("Loading....");
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                makeMeOnline();
                            }else{
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Gagal Untuk Login!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        txt_lupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetEmail = new EditText(view.getContext());
                AlertDialog.Builder passwordReset = new AlertDialog.Builder(view.getContext());
                passwordReset.setIcon(R.drawable.logo_depan);
                passwordReset.setTitle("Reset Password");
                passwordReset.setMessage("Masukkan Email Anda Yang Aktif Untuk Mendapatkan Link Reset Password");
                passwordReset.setView(resetEmail);

                passwordReset.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog.setMessage("Email Sedang Dikirim...");
                        progressDialog.show();
                        String mail = resetEmail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Reset Password Sudah Dikirim Ke Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Periksa Kembali Email Anda", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordReset.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                passwordReset.create().show();
            }
        });
    }
    private void makeMeOnline() {
        //after logging in, make user online
        progressDialog.setMessage("Cek User....");
        progressDialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online", "true");

        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User");
        ref.child(mAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //update succesfuly
                        startActivity(new Intent(LoginActivity.this, DashboarActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed success
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
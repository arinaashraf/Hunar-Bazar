package com.example.hunarbazar7.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hunarbazar7.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEdit, passwordEdit;
    Button registerBtn;
    FirebaseAuth mAuth;
    RadioButton radioBuyer, radioSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
        radioBuyer = findViewById(R.id.radioBuyer);
        radioSeller = findViewById(R.id.radioSeller);

        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(v -> {
            String email = emailEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!radioBuyer.isChecked() && !radioSeller.isChecked()) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            String role = radioBuyer.isChecked() ? "buyer" : "seller";

                            Map<String, Object> userData = new HashMap<>();
                            userData.put("email", email);
                            userData.put("role", role);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(uid)
                                    .setValue(userData)
                                    .addOnCompleteListener(uploadTask -> {
                                        Toast.makeText(this, "Registered!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, RoleCheckActivity.class));
                                        finish();
                                    });

                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}

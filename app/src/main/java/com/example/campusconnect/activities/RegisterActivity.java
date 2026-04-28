package com.example.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusconnect.R;
import com.example.campusconnect.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword;
    Spinner spFiliere, spRole;
    Button btnRegister;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spFiliere = findViewById(R.id.spFiliere);
        spRole = findViewById(R.id.spRole);
        btnRegister = findViewById(R.id.btnRegister);

        // Configuration des Spinners
        setupSpinners();

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void setupSpinners() {
        // Roles
        String[] roles = {getString(R.string.role_student), getString(R.string.role_admin)};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(roleAdapter);

        // Filieres
        String[] filieres = {getString(R.string.filiere_it), getString(R.string.filiere_business), getString(R.string.filiere_engineering)};
        ArrayAdapter<String> filiereAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filieres);
        filiereAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiliere.setAdapter(filiereAdapter);
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String filiere = spFiliere.getSelectedItem().toString();
        String role = spRole.getSelectedItem().toString();

        // ✅ Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase register
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    String uid = mAuth.getCurrentUser().getUid();

                    // Création user
                    User user = new User(uid, name, email, role, filiere, "", "");

                    // 🔥 Firestore
                    db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(this, HomeActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erreur Firestore", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

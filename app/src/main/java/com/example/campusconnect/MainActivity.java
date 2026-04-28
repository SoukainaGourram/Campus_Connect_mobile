package com.example.campusconnect;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.campusconnect.activities.HomeActivity;
import com.example.campusconnect.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Vérifier si l'utilisateur est connecté
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Utilisateur connecté -> Home
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // Pas connecté -> Login
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
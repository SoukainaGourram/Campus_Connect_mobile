package com.example.campusconnect.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.campusconnect.LoginActivity;
import com.example.campusconnect.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private ImageView ivProfile;
    private TextView tvName, tvRole, tvEmail, tvFiliere, tvAnnee;
    private MaterialButton btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ivProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvProfileName);
        tvRole = view.findViewById(R.id.tvProfileRole);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvFiliere = view.findViewById(R.id.tvProfileFiliere);
        tvAnnee = view.findViewById(R.id.tvProfileAnnee);
        btnLogout = view.findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserProfile();

        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserProfile() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String email = documentSnapshot.getString("email");
                        String role = documentSnapshot.getString("role");
                        String filiere = documentSnapshot.getString("filiere");
                        String annee = documentSnapshot.getString("annee");
                        String photoUrl = documentSnapshot.getString("photoUrl");

                        tvName.setText(name != null ? name : "Utilisateur");
                        tvEmail.setText(email != null ? email : "-");
                        tvRole.setText(role != null ? role.toUpperCase() : "ÉTUDIANT");
                        tvFiliere.setText(filiere != null ? filiere : "Non spécifiée");
                        tvAnnee.setText(annee != null ? annee : "Non spécifiée");

                        if (photoUrl != null && !photoUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(photoUrl)
                                    .circleCrop()
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .into(ivProfile);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erreur de chargement du profil", Toast.LENGTH_SHORT).show();
                });
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.AnnouncementAdapter;
import com.example.campusconnect.models.Announcement;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private TextView tvEmpty;

    private AnnouncementAdapter adapter;
    private List<Announcement> announcementList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String currentUserRole = "";
    private String currentUserName = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerAnnouncements);
        fabAdd       = view.findViewById(R.id.fabAddAnnouncement);
        tvEmpty      = view.findViewById(R.id.tvEmpty);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AnnouncementAdapter(announcementList);
        recyclerView.setAdapter(adapter);

        loadCurrentUserProfile();

        return view;
    }

    private void loadCurrentUserProfile() {
        String uid = mAuth.getCurrentUser().getUid();

        // db.collection("users").document(uid) :
        // accède au document de l'utilisateur dans Firestore
        // .get() : lecture unique (pas temps réel, c'est suffisant ici)
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lire le champ "role" du document
                        currentUserRole = documentSnapshot.getString("role");
                        if (currentUserRole == null) currentUserRole = "student";

                        // Lire le nom pour l'afficher comme auteur
                        currentUserName = documentSnapshot.getString("name");
                        if (currentUserName == null) currentUserName = "Utilisateur";

                        // ── Afficher le FAB si admin ou professeur ──
                        // Les étudiants ne peuvent PAS publier d'annonces
                        if (currentUserRole.equals("admin") || currentUserRole.equals("professor")) {
                            fabAdd.setVisibility(View.VISIBLE);
                            fabAdd.setOnClickListener(v -> showAddAnnouncementDialog());
                        }

                        // Maintenant on peut charger les annonces
                        loadAnnouncements();
                    }
                })
                .addOnFailureListener(e -> {
                    loadAnnouncements();
                });
    }

    private void loadAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {

                    if (error != null || snapshots == null) {
                        Toast.makeText(getContext(), "Erreur de chargement", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    announcementList.clear();

                    for (var doc : snapshots.getDocuments()) {
                        Announcement a = doc.toObject(Announcement.class);
                        if (a != null) {
                            a.setId(doc.getId());
                            announcementList.add(a);
                        }
                    }

                    adapter.updateList(new ArrayList<>(announcementList));

                    if (announcementList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showAddAnnouncementDialog() {

        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_announcement, null);

        TextInputEditText etTitle   = dialogView.findViewById(R.id.etTitle);
        TextInputEditText etContent = dialogView.findViewById(R.id.etContent);
        Spinner spCategory          = dialogView.findViewById(R.id.spCategory);
        Switch switchUrgent         = dialogView.findViewById(R.id.switchUrgent);

        String[] categories = {"Général", "Examens", "Événements", "Administratif", "Urgent"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(catAdapter);

        // Construire et afficher la boîte de dialogue
        new AlertDialog.Builder(getContext())
                .setView(dialogView)
                // Bouton "Publier" → sauvegarde dans Firestore
                .setPositiveButton("Publier", (dialog, which) -> {
                    String title    = etTitle.getText().toString().trim();
                    String content  = etContent.getText().toString().trim();
                    String category = spCategory.getSelectedItem().toString();
                    boolean isUrgent = switchUrgent.isChecked();

                    // Vérifier que les champs obligatoires sont remplis
                    if (title.isEmpty() || content.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Titre et contenu sont obligatoires",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sauvegarder l'annonce dans Firestore
                    publishAnnouncement(title, content, category, isUrgent);
                })
                // Bouton "Annuler" → ferme la boîte sans rien faire
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void publishAnnouncement(String title, String content,
                                     String category, boolean isUrgent) {

        Announcement newAnnouncement = new Announcement(
                null,               // id = null, Firestore génère automatiquement
                title,
                content,
                category,
                currentUserName,    // auteur = nom de l'utilisateur connecté
                Timestamp.now(),    // date = maintenant
                isUrgent
        );

        db.collection("announcements")
                .add(newAnnouncement)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(),
                            "Annonce publiée ✓",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "Erreur de publication : " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.GroupAdapter;
import com.example.campusconnect.models.Group;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    private RecyclerView recyclerGroups;
    private FloatingActionButton fabCreateGroup;
    private TextView tvNoGroups;

    private GroupAdapter adapter;
    private List<Group> groupList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String myUid       = "";
    private String myName      = "";
    private String myRole      = "";
    private String myFiliere   = "";

    public GroupsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        recyclerGroups  = view.findViewById(R.id.recyclerGroups);
        fabCreateGroup  = view.findViewById(R.id.fabCreateGroup);
        tvNoGroups      = view.findViewById(R.id.tvNoGroups);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        recyclerGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroupAdapter(groupList, myUid, new GroupAdapter.OnGroupClickListener() {
            @Override
            public void onJoinClick(Group group, boolean isAlreadyMember) {
                if (isAlreadyMember) {
                    confirmLeaveGroup(group);
                } else {
                    joinGroup(group);
                }
            }

            // Clic "Ouvrir" → naviguer vers le contenu du groupe
            @Override
            public void onOpenClick(Group group) {
                openGroupContent(group);
            }
        });
        recyclerGroups.setAdapter(adapter);

        // Charger le profil puis les groupes
        loadMyProfile();

        return view;
    }

    // ── Étape 1 : charger le profil ──
    private void loadMyProfile() {
        db.collection("users").document(myUid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        myName    = doc.getString("name");    if (myName == null) myName = "";
                        myRole    = doc.getString("role");    if (myRole == null) myRole = "student";
                        myFiliere = doc.getString("filiere"); if (myFiliere == null) myFiliere = "";
                    }

                    // Afficher le FAB seulement pour les profs
                    if (myRole.equals("professor") || myRole.equals("admin")) {
                        fabCreateGroup.setVisibility(View.VISIBLE);
                        fabCreateGroup.setOnClickListener(v -> showCreateGroupDialog());
                    }

                    listenGroups();
                });
    }

    // ── Étape 2 : écouter les groupes en temps réel ──
    private void listenGroups() {
        db.collection("groups")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    groupList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        Group g = doc.toObject(Group.class);
                        if (g != null) {
                            g.setId(doc.getId());
                            groupList.add(g);
                        }
                    }

                    adapter.updateList(new ArrayList<>(groupList));

                    if (groupList.isEmpty()) {
                        tvNoGroups.setVisibility(View.VISIBLE);
                        recyclerGroups.setVisibility(View.GONE);
                    } else {
                        tvNoGroups.setVisibility(View.GONE);
                        recyclerGroups.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void joinGroup(Group group) {
        db.collection("groups").document(group.getId())
                .update("members", FieldValue.arrayUnion(myUid))
                .addOnSuccessListener(v ->
                        Toast.makeText(getContext(), "Groupe rejoint ✓", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // ── Confirmer avant de quitter un groupe ──
    private void confirmLeaveGroup(Group group) {
        new AlertDialog.Builder(getContext())
                .setTitle("Quitter le groupe ?")
                .setMessage("Tu ne verras plus le contenu de " + group.getName())
                .setPositiveButton("Quitter", (d, w) -> leaveGroup(group))
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void leaveGroup(Group group) {
        db.collection("groups").document(group.getId())
                .update("members", FieldValue.arrayRemove(myUid))
                .addOnSuccessListener(v ->
                        Toast.makeText(getContext(), "Groupe quitté", Toast.LENGTH_SHORT).show());
    }

    private void openGroupContent(Group group) {
        GroupDetailFragment detailFragment = GroupDetailFragment.newInstance(
                group.getId(), group.getName(), myUid, myName, myRole);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null) // permet de revenir en arrière
                .commit();
    }

    // ── Dialog création de groupe (profs seulement) ──
    private void showCreateGroupDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(android.R.layout.simple_list_item_2, null);

        // Dialog simple avec EditTexts
        EditText etName        = new EditText(getContext());
        etName.setHint("Nom du groupe (ex: Algo S3 - Mr Bennani)");

        EditText etDescription = new EditText(getContext());
        etDescription.setHint("Description (optionnel)");

        // Container vertical pour les champs
        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 24, 48, 0);
        layout.addView(etName);
        layout.addView(etDescription);

        new AlertDialog.Builder(getContext())
                .setTitle("Créer un groupe")
                .setView(layout)
                .setPositiveButton("Créer", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String desc = etDescription.getText().toString().trim();
                    if (name.isEmpty()) {
                        Toast.makeText(getContext(), "Le nom est obligatoire", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    createGroup(name, desc);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    // ── Créer le groupe dans Firestore ──
    private void createGroup(String name, String description) {
        // Le prof créateur est automatiquement le premier membre
        List<String> members = new ArrayList<>();
        members.add(myUid);

        Group newGroup = new Group(
                null, name, description, myFiliere,
                myUid, myName, Timestamp.now(), members
        );

        db.collection("groups").add(newGroup)
                .addOnSuccessListener(ref ->
                        Toast.makeText(getContext(), "Groupe créé ✓", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erreur création", Toast.LENGTH_SHORT).show());
    }
}
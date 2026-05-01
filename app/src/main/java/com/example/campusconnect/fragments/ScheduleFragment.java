package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.ScheduleAdapter;
import com.example.campusconnect.models.ScheduleItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private TabLayout tabLayoutDays;
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private List<ScheduleItem> allScheduleItems = new ArrayList<>();
    private List<ScheduleItem> filteredItems = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userFiliere = "";

    private final String[] days = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"};

    public ScheduleFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        tabLayoutDays = view.findViewById(R.id.tabLayoutDays);
        recyclerView = view.findViewById(R.id.recyclerSchedule);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupTabs();
        setupRecyclerView();
        loadUserFiliereAndSchedule();

        return view;
    }

    private void setupTabs() {
        for (String day : days) {
            tabLayoutDays.addTab(tabLayoutDays.newTab().setText(day));
        }

        tabLayoutDays.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterScheduleByDay(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(filteredItems);
        recyclerView.setAdapter(adapter);
    }

    private void loadUserFiliereAndSchedule() {
        String uid = mAuth.getUid();
        if (uid == null) return;

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    userFiliere = documentSnapshot.getString("filiere");
                    if (userFiliere != null) {
                        fetchScheduleItems();
                    }
                });
    }

    private void fetchScheduleItems() {
        db.collection("schedules")
                .whereEqualTo("filiere", userFiliere)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;

                    allScheduleItems.clear();
                    for (var doc : value.getDocuments()) {
                        ScheduleItem item = doc.toObject(ScheduleItem.class);
                        if (item != null) allScheduleItems.add(item);
                    }
                    
                    // Filtrer par défaut sur le jour sélectionné (souvent Lundi)
                    int selectedTabPos = tabLayoutDays.getSelectedTabPosition();
                    if (selectedTabPos != -1) {
                        filterScheduleByDay(days[selectedTabPos]);
                    }
                });
    }

    private void filterScheduleByDay(String day) {
        filteredItems.clear();
        for (ScheduleItem item : allScheduleItems) {
            if (item.getDayOfWeek().equalsIgnoreCase(day)) {
                filteredItems.add(item);
            }
        }
        // Tri par heure de début (optionnel mais recommandé)
        filteredItems.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
        adapter.updateList(new ArrayList<>(filteredItems));
    }
}
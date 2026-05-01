package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.MessageAdapter;
import com.example.campusconnect.models.Message;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class GlobalChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private ImageButton btnSend;

    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUserName = "Utilisateur";

    public GlobalChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_global_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerGlobalChat);
        etMessage    = view.findViewById(R.id.etGlobalMessage);
        btnSend      = view.findViewById(R.id.btnSendGlobal);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // RecyclerView commence par le bas comme WhatsApp
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true);
        recyclerView.setLayoutManager(lm);
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        loadUserName();
        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void loadUserName() {
        String uid = mAuth.getUid();
        if (uid == null) return;
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String n = doc.getString("name");
                        currentUserName = n != null ? n : "Utilisateur";
                    }
                    listenMessages();
                });
    }

    private void listenMessages() {
        // Écoute la collection "messages" en temps réel
        // orderBy ASCENDING : les anciens messages en haut
        db.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    messageList.clear();
                    for (var doc : value.getDocuments()) {
                        Message msg = doc.toObject(Message.class);
                        if (msg != null) messageList.add(msg);
                    }
                    adapter.updateList(new ArrayList<>(messageList));
                    if (!messageList.isEmpty())
                        recyclerView.smoothScrollToPosition(messageList.size() - 1);
                });
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Message msg = new Message(mAuth.getUid(), currentUserName, content, Timestamp.now());
        db.collection("messages").add(msg)
                .addOnSuccessListener(ref -> etMessage.setText(""))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erreur d'envoi", Toast.LENGTH_SHORT).show());
    }
}
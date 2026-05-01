package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.MessageAdapter;
import com.example.campusconnect.adapters.ProfessorAdapter;
import com.example.campusconnect.models.Message;
import com.example.campusconnect.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class PrivateChatFragment extends Fragment {

    // Vues des 2 états
    private LinearLayout layoutProfList, layoutPrivateChat;
    private RecyclerView recyclerProfessors, recyclerPrivateChat;
    private TextView tvChatWithName;
    private EditText etPrivateMessage;
    private ImageButton btnSendPrivate, btnBackToList;

    // Adapters
    private ProfessorAdapter professorAdapter;
    private MessageAdapter messageAdapter;

    private List<User> professorList     = new ArrayList<>();
    private List<Message> privateMessages = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String myUid          = "";
    private String myName         = "";
    private String selectedProfUid = "";  // uid du prof sélectionné
    private String currentChatId  = "";   // id de la conversation privée

    public PrivateChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_private_chat, container, false);

        // ── Relier les vues ──
        layoutProfList    = view.findViewById(R.id.layoutProfList);
        layoutPrivateChat = view.findViewById(R.id.layoutPrivateChat);
        recyclerProfessors = view.findViewById(R.id.recyclerProfessors);
        recyclerPrivateChat = view.findViewById(R.id.recyclerPrivateChat);
        tvChatWithName    = view.findViewById(R.id.tvChatWithName);
        etPrivateMessage  = view.findViewById(R.id.etPrivateMessage);
        btnSendPrivate    = view.findViewById(R.id.btnSendPrivate);
        btnBackToList     = view.findViewById(R.id.btnBackToList);

        db    = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myUid = mAuth.getUid();

        // ── Setup RecyclerView liste des profs ──
        recyclerProfessors.setLayoutManager(new LinearLayoutManager(getContext()));
        professorAdapter = new ProfessorAdapter(professorList, this::openPrivateChat);
        recyclerProfessors.setAdapter(professorAdapter);

        // ── Setup RecyclerView messages privés ──
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setStackFromEnd(true); // messages récents en bas
        recyclerPrivateChat.setLayoutManager(lm);
        messageAdapter = new MessageAdapter(privateMessages);
        recyclerPrivateChat.setAdapter(messageAdapter);

        // ── Bouton retour : afficher la liste des profs ──
        btnBackToList.setOnClickListener(v -> showProfList());

        // ── Bouton envoyer message privé ──
        btnSendPrivate.setOnClickListener(v -> sendPrivateMessage());

        // ── Charger les infos de l'utilisateur connecté puis les profs ──
        loadMyInfoThenProfessors();

        return view;
    }

    // ── Étape 1 : charger nom de l'utilisateur connecté ──
    private void loadMyInfoThenProfessors() {
        db.collection("users").document(myUid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        myName = doc.getString("name");
                        if (myName == null) myName = "Utilisateur";
                    }
                    loadProfessors();
                });
    }

    // ── Étape 2 : charger tous les utilisateurs avec role="professor" ──
    private void loadProfessors() {
        db.collection("users")
                .whereEqualTo("role", "professor")
                .get()
                .addOnSuccessListener(snapshots -> {
                    professorList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        User prof = doc.toObject(User.class);
                        if (prof != null) {
                            prof.setUid(doc.getId());
                            professorList.add(prof);
                        }
                    }
                    professorAdapter.updateList(new ArrayList<>(professorList));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erreur chargement profs", Toast.LENGTH_SHORT).show());
    }

    // ── Étape 3 : ouvrir la conversation privée avec un prof ──
    private void openPrivateChat(User professor) {
        selectedProfUid = professor.getUid();
        tvChatWithName.setText(professor.getName());

        // chatId = combinaison des 2 uids triés alphabétiquement
        // → garantit que les 2 utilisateurs ont le même chatId
        // ex: uid_A="abc", uid_B="xyz" → chatId="abc_xyz"
        if (myUid.compareTo(selectedProfUid) < 0) {
            currentChatId = myUid + "_" + selectedProfUid;
        } else {
            currentChatId = selectedProfUid + "_" + myUid;
        }

        // Afficher l'écran de conversation
        showPrivateChat();

        // Écouter les messages de cette conversation en temps réel
        listenPrivateMessages();
    }

    // ── Écoute les messages de la conversation privée en temps réel ──
    private void listenPrivateMessages() {
        // Chemin Firestore : private_chats/{chatId}/messages
        db.collection("private_chats")
                .document(currentChatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    privateMessages.clear();
                    for (var doc : value.getDocuments()) {
                        Message msg = doc.toObject(Message.class);
                        if (msg != null) privateMessages.add(msg);
                    }
                    messageAdapter.updateList(new ArrayList<>(privateMessages));
                    if (!privateMessages.isEmpty())
                        recyclerPrivateChat.smoothScrollToPosition(privateMessages.size() - 1);
                });
    }

    // ── Envoyer un message privé ──
    private void sendPrivateMessage() {
        String content = etPrivateMessage.getText().toString().trim();
        if (content.isEmpty()) return;

        Message msg = new Message(myUid, myName, content, Timestamp.now());

        // Sauvegarder sous : private_chats/{chatId}/messages/{autoId}
        db.collection("private_chats")
                .document(currentChatId)
                .collection("messages")
                .add(msg)
                .addOnSuccessListener(ref -> etPrivateMessage.setText(""))
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Erreur d'envoi", Toast.LENGTH_SHORT).show());
    }

    // ── Afficher la liste des profs (État 1) ──
    private void showProfList() {
        layoutProfList.setVisibility(View.VISIBLE);
        layoutPrivateChat.setVisibility(View.GONE);
    }

    // ── Afficher la conversation privée (État 2) ──
    private void showPrivateChat() {
        layoutProfList.setVisibility(View.GONE);
        layoutPrivateChat.setVisibility(View.VISIBLE);
    }
}
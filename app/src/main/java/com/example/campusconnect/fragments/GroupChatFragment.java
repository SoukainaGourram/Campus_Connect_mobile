package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.MessageAdapter;
import com.example.campusconnect.models.Message;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class GroupChatFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private static final String ARG_MY_UID = "my_uid";
    private static final String ARG_MY_NAME = "my_name";
    private static final String ARG_MY_ROLE = "my_role";

    private String groupId;
    private String myUid;
    private String myName;
    private String myRole;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList = new ArrayList<>();
    private EditText etMessage;
    private ImageButton btnSend;

    private FirebaseFirestore db;

    public static GroupChatFragment newInstance(String groupId, String myUid, String myName, String myRole) {
        GroupChatFragment fragment = new GroupChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        args.putString(ARG_MY_UID, myUid);
        args.putString(ARG_MY_NAME, myName);
        args.putString(ARG_MY_ROLE, myRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUP_ID);
            myUid = getArguments().getString(ARG_MY_UID);
            myName = getArguments().getString(ARG_MY_NAME);
            myRole = getArguments().getString(ARG_MY_ROLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        recyclerView = view.findViewById(R.id.recyclerGroupChat);
        etMessage = view.findViewById(R.id.etGroupMessage);
        btnSend = view.findViewById(R.id.btnSendGroupMessage);

        db = FirebaseFirestore.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);

        listenMessages();

        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void listenMessages() {
        db.collection("groups").document(groupId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    messageList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        Message m = doc.toObject(Message.class);
                        if (m != null) messageList.add(m);
                    }
                    adapter.updateList(new ArrayList<>(messageList));
                    if (!messageList.isEmpty()) {
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        Message msg = new Message(myUid, myName, text, Timestamp.now());

        db.collection("groups").document(groupId).collection("messages")
                .add(msg)
                .addOnSuccessListener(ref -> etMessage.setText(""))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Erreur d'envoi", Toast.LENGTH_SHORT).show());
    }
}
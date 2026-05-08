package com.example.campusconnect.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.adapters.DocumentAdapter;
import com.example.campusconnect.models.Document;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class GroupDocsFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private static final String ARG_MY_ROLE = "my_role";

    private String groupId;
    private String myRole;

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<Document> documentList = new ArrayList<>();
    private FloatingActionButton fabUpload;
    private TextView tvNoDocs;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    public static GroupDocsFragment newInstance(String groupId, String myRole) {
        GroupDocsFragment fragment = new GroupDocsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        args.putString(ARG_MY_ROLE, myRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUP_ID);
            myRole = getArguments().getString(ARG_MY_ROLE);
        }

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        uploadFile(result.getData().getData());
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_docs, container, false);

        recyclerView = view.findViewById(R.id.recyclerGroupDocs);
        fabUpload = view.findViewById(R.id.fabUploadDoc);
        tvNoDocs = view.findViewById(R.id.tvNoDocs);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DocumentAdapter(documentList, this::downloadFile);
        recyclerView.setAdapter(adapter);

        if ("professor".equals(myRole) || "admin".equals(myRole)) {
            fabUpload.setVisibility(View.VISIBLE);
            fabUpload.setOnClickListener(v -> openFilePicker());
        }

        listenDocuments();

        return view;
    }

    private void listenDocuments() {
        db.collection("groups").document(groupId).collection("documents")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    documentList.clear();
                    for (var doc : snapshots.getDocuments()) {
                        Document d = doc.toObject(Document.class);
                        if (d != null) documentList.add(d);
                    }
                    adapter.updateList(new ArrayList<>(documentList));
                    tvNoDocs.setVisibility(documentList.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(Intent.createChooser(intent, "Sélectionner un PDF"));
    }

    private void uploadFile(Uri fileUri) {
        if (fileUri == null) return;

        String fileName = getFileName(fileUri);
        StorageReference fileRef = storage.getReference().child("groups/" + groupId + "/docs/" + System.currentTimeMillis() + "_" + fileName);

        Toast.makeText(getContext(), "Upload en cours...", Toast.LENGTH_SHORT).show();

        fileRef.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveDocumentToFirestore(fileName, uri.toString());
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Échec de l'upload", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveDocumentToFirestore(String name, String url) {
        String userName = mAuth.getCurrentUser().getDisplayName();
        if (userName == null || userName.isEmpty()) userName = "Professeur";

        Document doc = new Document(name, url, "PDF", userName, Timestamp.now());

        db.collection("groups").document(groupId).collection("documents")
                .add(doc)
                .addOnSuccessListener(ref -> Toast.makeText(getContext(), "Document partagé ✓", Toast.LENGTH_SHORT).show());
    }

    private void downloadFile(Document document) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(document.getDownloadUrl()));
        startActivity(intent);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
}
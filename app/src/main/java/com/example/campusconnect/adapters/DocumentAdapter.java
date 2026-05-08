package com.example.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.models.Document;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.ViewHolder> {

    private List<Document> documents;
    private final OnDocumentClickListener listener;

    public interface OnDocumentClickListener {
        void onDownloadClick(Document document);
    }

    public DocumentAdapter(List<Document> documents, OnDocumentClickListener listener) {
        this.documents = documents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_document, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Document doc = documents.get(position);
        holder.tvName.setText(doc.getName());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        String date = doc.getTimestamp() != null ? sdf.format(doc.getTimestamp().toDate()) : "";
        holder.tvInfo.setText("Par " + doc.getUploadedBy() + " • " + date);

        holder.btnDownload.setOnClickListener(v -> listener.onDownloadClick(doc));
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public void updateList(List<Document> newList) {
        this.documents = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;
        ImageButton btnDownload;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvDocName);
            tvInfo = v.findViewById(R.id.tvDocInfo);
            btnDownload = v.findViewById(R.id.btnDownloadDoc);
        }
    }
}
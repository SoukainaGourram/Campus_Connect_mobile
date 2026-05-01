package com.example.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT     = 1;
    private static final int TYPE_RECEIVED = 2;

    private List<Message> messages;
    private final String myUid;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
        // On récupère l'uid une seule fois ici, pas à chaque bind
        this.myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // ── Détermine le type de bulle pour chaque message ──
    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getSenderId().equals(myUid)
                ? TYPE_SENT : TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SENT) {
            View v = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentHolder(v);
        } else {
            View v = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message msg = messages.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        String time = msg.getTimestamp() != null
                ? sdf.format(msg.getTimestamp().toDate()) : "";

        if (holder instanceof SentHolder) {
            SentHolder h = (SentHolder) holder;
            h.tvContent.setText(msg.getContent());
            h.tvTime.setText(time);
        } else {
            ReceivedHolder h = (ReceivedHolder) holder;
            h.tvSender.setText(msg.getSenderName());
            h.tvContent.setText(msg.getContent());
            h.tvTime.setText(time);
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    public void updateList(List<Message> newList) {
        this.messages = newList;
        notifyDataSetChanged();
    }

    // ── ViewHolder bulle verte (messages envoyés) ──
    static class SentHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;
        SentHolder(View v) {
            super(v);
            tvContent = v.findViewById(R.id.tvMessageContent);
            tvTime    = v.findViewById(R.id.tvMessageTime);
        }
    }

    // ── ViewHolder bulle blanche (messages reçus) ──
    static class ReceivedHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvContent, tvTime;
        ReceivedHolder(View v) {
            super(v);
            tvSender  = v.findViewById(R.id.tvSenderName);
            tvContent = v.findViewById(R.id.tvMessageContent);
            tvTime    = v.findViewById(R.id.tvMessageTime);
        }
    }
}
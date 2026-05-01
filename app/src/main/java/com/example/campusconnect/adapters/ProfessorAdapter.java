package com.example.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.models.User;

import java.util.List;

public class ProfessorAdapter extends RecyclerView.Adapter<ProfessorAdapter.ViewHolder> {

    private List<User> professors;

    // Interface : permet à PrivateChatFragment de savoir quel prof a été cliqué
    public interface OnProfClickListener {
        void onProfClick(User professor);
    }

    private final OnProfClickListener listener;

    public ProfessorAdapter(List<User> professors, OnProfClickListener listener) {
        this.professors = professors;
        this.listener   = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_professor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User prof = professors.get(position);

        holder.tvName.setText(prof.getName());
        holder.tvFiliere.setText("Filière : " + prof.getFiliere());

        // Initiales : prend la première lettre du nom
        // ex: "Mohamed Bennani" → "MB"
        String initials = "";
        if (prof.getName() != null && !prof.getName().isEmpty()) {
            String[] parts = prof.getName().trim().split(" ");
            initials += parts[0].charAt(0);
            if (parts.length > 1) initials += parts[parts.length - 1].charAt(0);
        }
        holder.tvInitials.setText(initials.toUpperCase());

        // Clic sur le bouton → ouvrir la conversation avec ce prof
        holder.btnMessage.setOnClickListener(v -> listener.onProfClick(prof));
        // Clic sur toute la carte aussi
        holder.itemView.setOnClickListener(v -> listener.onProfClick(prof));
    }

    @Override
    public int getItemCount() { return professors.size(); }

    public void updateList(List<User> newList) {
        this.professors = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvFiliere, tvInitials;
        ImageButton btnMessage;

        ViewHolder(View v) {
            super(v);
            tvName     = v.findViewById(R.id.tvProfName);
            tvFiliere  = v.findViewById(R.id.tvProfFiliere);
            tvInitials = v.findViewById(R.id.tvProfInitials);
            btnMessage = v.findViewById(R.id.btnMessageProf);
        }
    }
}
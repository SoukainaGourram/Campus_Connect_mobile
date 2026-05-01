package com.example.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.models.Group;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    private List<Group> groups;
    private final String myUid;

    public interface OnGroupClickListener {
        void onJoinClick(Group group, boolean isAlreadyMember);
        void onOpenClick(Group group);
    }

    private final OnGroupClickListener listener;

    public GroupAdapter(List<Group> groups, String myUid, OnGroupClickListener listener) {
        this.groups   = groups;
        this.myUid    = myUid;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups.get(position);

        holder.tvName.setText(group.getName());
        holder.tvCreator.setText("Par " + group.getCreatorName());
        holder.tvFiliere.setText("🎓 " + group.getFiliere());

        // Nombre de membres
        int count = group.getMembers() != null ? group.getMembers().size() : 0;
        holder.tvMemberCount.setText(count + " membre(s)");

        // Description
        if (group.getDescription() != null && !group.getDescription().isEmpty()) {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(group.getDescription());
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        // Vérifier si l'utilisateur est déjà membre
        boolean isMember = group.getMembers() != null && group.getMembers().contains(myUid);

        if (isMember) {
            holder.btnJoin.setText("✓ Membre — Ouvrir");
            holder.btnJoin.setEnabled(true);
            // Clic → ouvrir le groupe
            holder.btnJoin.setOnClickListener(v -> listener.onOpenClick(group));
            // Clic long → quitter
            holder.btnJoin.setOnLongClickListener(v -> {
                listener.onJoinClick(group, true);
                return true;
            });
        } else {
            holder.btnJoin.setText("Rejoindre");
            holder.btnJoin.setEnabled(true);
            holder.btnJoin.setOnClickListener(v -> listener.onJoinClick(group, false));
        }
    }

    @Override
    public int getItemCount() { return groups.size(); }

    public void updateList(List<Group> newList) {
        this.groups = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCreator, tvFiliere, tvMemberCount, tvDescription;
        MaterialButton btnJoin;

        ViewHolder(View v) {
            super(v);
            tvName        = v.findViewById(R.id.tvGroupName);
            tvCreator     = v.findViewById(R.id.tvGroupCreator);
            tvFiliere     = v.findViewById(R.id.tvGroupFiliere);
            tvMemberCount = v.findViewById(R.id.tvMemberCount);
            tvDescription = v.findViewById(R.id.tvGroupDescription);
            btnJoin       = v.findViewById(R.id.btnJoinGroup);
        }
    }
}
package com.example.campusconnect.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.models.Announcement;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private List<Announcement> announcementList;

    public AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.tvTitle.setText(announcement.getTitle());
        holder.tvContent.setText(announcement.getContent());
        holder.tvCategory.setText(announcement.getCategory());
        holder.tvAuthor.setText("Par " + announcement.getAuthorName());

        if (announcement.getTimestamp() != null) {
            long timeInMillis = announcement.getTimestamp().toDate().getTime();
            String timeAgo = DateUtils.getRelativeTimeSpanString(timeInMillis,
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
            holder.tvDate.setText(timeAgo);
        }

        if (announcement.isUrgent()) {
            holder.tvUrgent.setVisibility(View.VISIBLE);
        } else {
            holder.tvUrgent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public void updateList(ArrayList<Announcement> newList) {
        this.announcementList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvCategory, tvAuthor, tvDate, tvUrgent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvUrgent = itemView.findViewById(R.id.tvUrgent);
        }
    }
}
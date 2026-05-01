package com.example.campusconnect.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusconnect.R;
import com.example.campusconnect.models.ScheduleItem;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleItem> scheduleItems;

    public ScheduleAdapter(List<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleItem item = scheduleItems.get(position);
        holder.tvSubject.setText(item.getSubject());
        holder.tvProfessor.setText(item.getProfessor());
        holder.tvRoom.setText(item.getRoom());
        holder.tvStartTime.setText(item.getStartTime());
        holder.tvEndTime.setText(item.getEndTime());
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    public void updateList(List<ScheduleItem> newList) {
        this.scheduleItems = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvProfessor, tvRoom, tvStartTime, tvEndTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvProfessor = itemView.findViewById(R.id.tvProfessor);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvStartTime = itemView.findViewById(R.id.tvStartTime);
            tvEndTime = itemView.findViewById(R.id.tvEndTime);
        }
    }
}
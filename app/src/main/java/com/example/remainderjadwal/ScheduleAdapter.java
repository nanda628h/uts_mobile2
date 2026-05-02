package com.example.remainderjadwal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Schedule> schedules;

    public ScheduleAdapter(Context context, ArrayList<Schedule> schedules) {
        this.context = context;
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule s = schedules.get(position);

        // Set data
        holder.tvDosen.setText(s.getDosen());
        holder.tvJam.setText(s.getJam());

        // Klik item → Detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ScheduleDetailActivity.class);
            intent.putExtra("schedule", s);
            context.startActivity(intent);
        });

        // Tombol Hapus
        holder.btnDelete.setOnClickListener(v -> {
            // Hapus dari storage
            ScheduleStorage.deleteSchedule(context, s.getId());

            // Hapus dari list
            schedules.remove(position);

            // Update UI (lebih aman dari notifyDataSetChanged)
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, schedules.size());

            Toast.makeText(context, "Jadwal dihapus", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    // ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDosen, tvJam;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvDosen = itemView.findViewById(R.id.tvDosen);
            tvJam = itemView.findViewById(R.id.tvJam);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
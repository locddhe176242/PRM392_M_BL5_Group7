package com.example.smartalamclock.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smartalamclock.R;
import com.example.smartalamclock.entity.Alarm;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private List<Alarm> alarms;
    private OnAlarmClickListener listener;

    public interface OnAlarmClickListener {
        void onAlarmClick(Alarm alarm);
        void onSwitchChanged(Alarm alarm, boolean enabled);
        void onAlarmLongClick(Alarm alarm);
    }

    public AlarmAdapter(List<Alarm> alarms, OnAlarmClickListener listener) {
        this.alarms = alarms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
    Alarm alarm = alarms.get(position);
    holder.tvTime.setText(alarm.getTime());
    holder.tvLabel.setText(alarm.getLabel());
    holder.switchEnabled.setChecked(alarm.isEnabled());

    // Click để sửa
    holder.itemView.setOnClickListener(v -> {
        if(listener != null) {
            listener.onAlarmClick(alarm);
        }
    });

    // Long click để xoá
    holder.itemView.setOnLongClickListener(v -> {
        if(listener != null) {
            listener.onAlarmLongClick(alarm);
        }
        return true;
    });

    // Bật/tắt báo thức
    holder.switchEnabled.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if(listener != null) {
            listener.onSwitchChanged(alarm, isChecked);
        }
    });
}

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
        notifyDataSetChanged();
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvLabel;
        Switch switchEnabled;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            switchEnabled = itemView.findViewById(R.id.switchEnabled);
        }
    }
}
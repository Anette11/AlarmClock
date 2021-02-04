package com.example.alarmclock;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.AlarmClockViewHolder> {
    private final List<AlarmClock> alarmClockList;
    private AlarmClockAdapterInterfaceOnCheckedChangeListener alarmClockAdapterInterfaceOnCheckedChangeListener;
    private AlarmClockAdapterInterfaceOnLongClickListener alarmClockAdapterInterfaceOnLongClickListener;

    public void setAlarmClockAdapterInterfaceOnLongClickListener(
            AlarmClockAdapterInterfaceOnLongClickListener alarmClockAdapterInterfaceOnLongClickListener) {
        this.alarmClockAdapterInterfaceOnLongClickListener = alarmClockAdapterInterfaceOnLongClickListener;
    }

    public AlarmClockAdapter(List<AlarmClock> alarmClockList) {
        this.alarmClockList = alarmClockList;
    }

    public interface AlarmClockAdapterInterfaceOnLongClickListener {
        void getAlarmClockAdapterInterfaceOnLongClickListener(int position);
    }

    public interface AlarmClockAdapterInterfaceOnCheckedChangeListener {
        void getAlarmClockPositionInRecyclerViewOnCheckedChangeListener(int position);

        void getAlarmClockBooleanSwitchOnOffInRecyclerViewOnCheckedChangeListener(boolean isChecked);
    }

    public void setAlarmClockAdapterInterfaceOnCheckedChangeListener(
            AlarmClockAdapterInterfaceOnCheckedChangeListener alarmClockAdapterInterfaceOnCheckedChangeListener
    ) {
        this.alarmClockAdapterInterfaceOnCheckedChangeListener = alarmClockAdapterInterfaceOnCheckedChangeListener;
    }

    @NonNull
    @Override
    public AlarmClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_item_layout, parent, false);
        return new AlarmClockAdapter.AlarmClockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmClockViewHolder holder, int position) {
        AlarmClock alarmClock = alarmClockList.get(position);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(alarmClock.getStatedTimeHour())
                .append(holder.itemView.getContext().getString(R.string.separator))
                .append(alarmClock.getStatedTimeMinute());

        holder.textViewStatedTime.setText(stringBuilder);
        holder.textViewRepeatability.setText(alarmClock.getRepeatability());

        if (alarmClock.getSwitchOnOff()
                .equals(holder.itemView.getContext().getString(R.string.on))) {
            holder.switchMaterialOnOff.setChecked(true);
        } else {
            holder.switchMaterialOnOff.setChecked(false);
            holder.textViewStatedTime.setTextColor(Color.GRAY);
            holder.textViewRepeatability.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return alarmClockList.size();
    }

    public AlarmClock getAlarmClockAtPosition(int position) {
        return alarmClockList.get(position);
    }

    class AlarmClockViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewStatedTime;
        private final TextView textViewRepeatability;
        private final SwitchMaterial switchMaterialOnOff;

        public AlarmClockViewHolder(@NonNull View view) {
            super(view);
            textViewStatedTime = view.findViewById(R.id.text_view_stated_time);
            textViewRepeatability = view.findViewById(R.id.text_view_repeatability);
            switchMaterialOnOff = view.findViewById(R.id.switch_material_on_off);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (alarmClockAdapterInterfaceOnLongClickListener != null
                            && position != RecyclerView.NO_POSITION) {
                        alarmClockAdapterInterfaceOnLongClickListener
                                .getAlarmClockAdapterInterfaceOnLongClickListener(position);
                    }
                    return true;
                }
            });

            switchMaterialOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int position = getAdapterPosition();
                    if (alarmClockAdapterInterfaceOnCheckedChangeListener != null
                            && position != RecyclerView.NO_POSITION) {
                        alarmClockAdapterInterfaceOnCheckedChangeListener
                                .getAlarmClockPositionInRecyclerViewOnCheckedChangeListener(position);
                        alarmClockAdapterInterfaceOnCheckedChangeListener
                                .getAlarmClockBooleanSwitchOnOffInRecyclerViewOnCheckedChangeListener(isChecked);
                    }

                    if (!isChecked) {
                        textViewStatedTime.setTextColor(Color.GRAY);
                        textViewRepeatability.setTextColor(Color.GRAY);
                    } else {
                        textViewStatedTime.setTextColor(Color.BLACK);
                        textViewRepeatability.setTextColor(Color.BLACK);
                    }
                }
            });
        }
    }
}

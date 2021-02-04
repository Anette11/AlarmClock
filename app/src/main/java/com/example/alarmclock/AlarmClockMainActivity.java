package com.example.alarmclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AlarmClockMainActivity extends AppCompatActivity {
    private List<AlarmClock> alarmClockList;
    private NumberPicker numberPickerHour;
    private NumberPicker numberPickerMinute;
    private NumberPicker numberPickerRepeat;
    private String[] stringArrayListHours;
    private String[] stringArrayListMinutes;
    private String[] stringArrayListRepeat;
    private String switchOnOffAtPosition;
    private AlarmClockAdapter alarmClockAdapter;
    private AlarmClockMethodsDatabase alarmClockMethodsToOperateWithDatabase;
    private AlarmClockIntent alarmClockIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        alarmClockMethodsToOperateWithDatabase = new AlarmClockMethodsDatabase(this);
        alarmClockIntent = new AlarmClockIntent(this);
        alarmClockList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmClockAdapter = new AlarmClockAdapter(alarmClockList);
        recyclerView.setAdapter(alarmClockAdapter);
        alarmClockMethodsToOperateWithDatabase.addAllAlarmClocksFromDatabaseToAlarmClockList(alarmClockList);
        initializeStringArrays();
        alarmClockAdapterSetAlarmClockAdapterInterfaceOnLongClickListener();
        alarmClockAdapterSetAlarmClockAdapterInterfaceOnCheckedChangeListener();
    }

    private void alarmClockAdapterSetAlarmClockAdapterInterfaceOnCheckedChangeListener() {
        alarmClockAdapter.setAlarmClockAdapterInterfaceOnCheckedChangeListener(
                new AlarmClockAdapter.AlarmClockAdapterInterfaceOnCheckedChangeListener() {
                    AlarmClock alarmClock;
                    int alarmClockId;
                    String statedTimeHour;
                    String statedTimeMinute;
                    String repeatability;
                    int position;

                    @Override
                    public void getAlarmClockPositionInRecyclerViewOnCheckedChangeListener(int position) {
                        this.position = position;
                        statedTimeHour = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getStatedTimeHour();
                        statedTimeMinute = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getStatedTimeMinute();
                        repeatability = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getRepeatability();
                        switchOnOffAtPosition = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getSwitchOnOff();
                        alarmClockId = alarmClockMethodsToOperateWithDatabase
                                .getAlarmClockId(
                                        statedTimeHour,
                                        statedTimeMinute,
                                        repeatability,
                                        switchOnOffAtPosition);
                    }

                    @Override
                    public void getAlarmClockBooleanSwitchOnOffInRecyclerViewOnCheckedChangeListener(boolean isChecked) {
                        if (isChecked) {
                            switchOnOffAtPosition = getResources().getString(R.string.on);

                            alarmClock = new AlarmClock();
                            alarmClock.setStatedTimeHour(statedTimeHour);
                            alarmClock.setStatedTimeMinute(statedTimeMinute);
                            alarmClock.setRepeatability(repeatability);
                            alarmClock.setSwitchOnOff(switchOnOffAtPosition);

                            alarmClock.startAlarmToBeginRingAtPreciseTime(
                                    Integer.parseInt(alarmClock.getStatedTimeHour()),
                                    Integer.parseInt(alarmClock.getStatedTimeMinute()),
                                    alarmClock,
                                    alarmClockId,
                                    getApplicationContext(),
                                    alarmClockIntent);

                        } else {
                            switchOnOffAtPosition = getResources().getString(R.string.off);

                            alarmClock = new AlarmClock();
                            alarmClock.setStatedTimeHour(statedTimeHour);
                            alarmClock.setStatedTimeMinute(statedTimeMinute);
                            alarmClock.setRepeatability(repeatability);
                            alarmClock.setSwitchOnOff(switchOnOffAtPosition);

                            alarmClockIntent.cancelAlarmClockStartRingPendingIntent(alarmClockId);
                            alarmClockIntent.cancelAlarmClockSnoozeRingPendingIntent(alarmClockId);
                        }
                        updateSwitchOnOffForAlarmClockInDatabase(alarmClock, alarmClockId, position);
                    }
                });
    }

    private void updateSwitchOnOffForAlarmClockInDatabase(
            AlarmClock alarmClock,
            int alarmClockId,
            int position
    ) {
        alarmClockList.set(position, alarmClock);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                alarmClockMethodsToOperateWithDatabase.updateSwitchOnOffForAlarmClockInDatabase(
                        alarmClock,
                        alarmClockId
                );
            }
        });
        thread.start();
    }

    private void alarmClockAdapterSetAlarmClockAdapterInterfaceOnLongClickListener() {
        alarmClockAdapter.setAlarmClockAdapterInterfaceOnLongClickListener(
                new AlarmClockAdapter.AlarmClockAdapterInterfaceOnLongClickListener() {
                    AlarmClock alarmClock;
                    int alarmClockId;
                    String statedTimeHour;
                    String statedTimeMinute;
                    String repeatability;
                    String switchOnOffAtPosition;
                    int position;

                    @Override
                    public void getAlarmClockAdapterInterfaceOnLongClickListener(int position) {
                        this.position = position;
                        statedTimeHour = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getStatedTimeHour();
                        statedTimeMinute = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getStatedTimeMinute();
                        repeatability = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getRepeatability();
                        switchOnOffAtPosition = alarmClockAdapter
                                .getAlarmClockAtPosition(position)
                                .getSwitchOnOff();
                        alarmClockId = alarmClockMethodsToOperateWithDatabase
                                .getAlarmClockId(
                                        statedTimeHour,
                                        statedTimeMinute,
                                        repeatability,
                                        switchOnOffAtPosition);
                        alarmClock = new AlarmClock(
                                statedTimeHour,
                                statedTimeMinute,
                                repeatability,
                                switchOnOffAtPosition);

                        showAlertDialogEditOrDeleteAlarmClock(
                                Arrays.asList(stringArrayListHours).indexOf(statedTimeHour),
                                Arrays.asList(stringArrayListMinutes).indexOf(statedTimeMinute),
                                Arrays.asList(stringArrayListRepeat).indexOf(repeatability),
                                statedTimeHour,
                                alarmClockId,
                                alarmClock,
                                position);
                    }
                });
    }

    private void initializeStringArrays() {
        stringArrayListHours = getResources().getStringArray(R.array.string_array_list_hours);
        stringArrayListMinutes = getResources().getStringArray(R.array.string_array_list_minutes);
        stringArrayListRepeat = getResources().getStringArray(R.array.string_array_list_repeat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            showAlertDialogAddNewAlarmClock();
        } else if (item.getItemId() == R.id.menu_delete_all_alarms) {
            showAlertDialogDeleteAllAlarms();
        }
        return true;
    }

    private void showAlertDialogDeleteAllAlarms() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog
                .Builder(AlarmClockMainActivity.this);

        View view = getLayoutInflater()
                .inflate(R.layout.alert_dialog_delete_all_alarm_clocks, null);

        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setView(view);
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
            }
        });

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmClockMethodsToOperateWithDatabase.deleteAllAlarmClocksFromDatabase(
                                alarmClockList,
                                alarmClockAdapter);
                        alertDialog.dismiss();
                    }
                });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
    }

    private void showAlertDialogEditOrDeleteAlarmClock(
            int valueHour,
            int valueMinute,
            int valueRepeat,
            String statedTimeHour,
            int id,
            AlarmClock alarmClock,
            int position
    ) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        View view = getLayoutInflater()
                .inflate(R.layout.alert_dialog_edit_or_delete_alarm_clock, null);

        numberPickerHour = view.findViewById(R.id.number_picker_hour_edit);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(stringArrayListHours.length - 1);
        numberPickerHour.setDisplayedValues(stringArrayListHours);
        numberPickerHour.setWrapSelectorWheel(true);
        numberPickerHour.setValue(valueHour);

        numberPickerMinute = view.findViewById(R.id.number_picker_minute_edit);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(stringArrayListMinutes.length - 1);
        numberPickerMinute.setDisplayedValues(stringArrayListMinutes);
        numberPickerMinute.setWrapSelectorWheel(true);
        numberPickerMinute.setValue(valueMinute);

        numberPickerRepeat = view.findViewById(R.id.number_picker_repeat_edit);
        numberPickerRepeat.setMinValue(0);
        numberPickerRepeat.setMaxValue(stringArrayListRepeat.length - 1);
        numberPickerRepeat.setDisplayedValues(stringArrayListRepeat);
        numberPickerRepeat.setWrapSelectorWheel(true);
        numberPickerRepeat.setValue(valueRepeat);

        alertDialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setView(view);
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                        .setTextColor(getResources().getColor(R.color.color2));
            }
        });

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmClock.setStatedTimeHour(stringArrayListHours[numberPickerHour.getValue()]);
                        alarmClock.setStatedTimeMinute(stringArrayListMinutes[numberPickerMinute.getValue()]);
                        alarmClock.setRepeatability(stringArrayListRepeat[numberPickerRepeat.getValue()]);
                        alarmClock.setSwitchOnOff(getResources().getString(R.string.on));

                        int counter = 0;

                        for (AlarmClock clock : alarmClockList) {
                            if (clock.getStatedTimeHour()
                                    .equals(alarmClock.getStatedTimeHour()) &&
                                    clock.getStatedTimeMinute()
                                            .equals(alarmClock.getStatedTimeMinute()) &&
                                    clock.getRepeatability()
                                            .equals(alarmClock.getRepeatability())
                            ) {
                                counter++;
                            }
                        }

                        if (counter == 1 &&
                                alarmClockAdapter.getAlarmClockAtPosition(position).getStatedTimeHour()
                                        .equals(alarmClock.getStatedTimeHour()) &&
                                alarmClockAdapter.getAlarmClockAtPosition(position).getStatedTimeMinute()
                                        .equals(alarmClock.getStatedTimeMinute()) &&
                                alarmClockAdapter.getAlarmClockAtPosition(position).getRepeatability()
                                        .equals(alarmClock.getRepeatability())
                        ) {
                            reSetEditedAlarmClock(position, alarmClock, id);
                            alertDialog.dismiss();
                        } else if (counter == 1 && !(
                                alarmClockAdapter.getAlarmClockAtPosition(position).getStatedTimeHour()
                                        .equals(alarmClock.getStatedTimeHour()) &&
                                        alarmClockAdapter.getAlarmClockAtPosition(position).getStatedTimeMinute()
                                                .equals(alarmClock.getStatedTimeMinute()) &&
                                        alarmClockAdapter.getAlarmClockAtPosition(position).getRepeatability()
                                                .equals(alarmClock.getRepeatability()))
                        ) {
                            Toast.makeText(AlarmClockMainActivity.this,
                                    getResources().getString(R.string.the_same_alarm_clock_is_already_exists),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            reSetEditedAlarmClock(position, alarmClock, id);
                            alertDialog.dismiss();
                        }
                    }
                });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        showAlertDialogDeleteAlarmClock(statedTimeHour, id, alarmClock, position);
                    }
                });
    }

    private void reSetEditedAlarmClock(
            int position,
            AlarmClock alarmClock,
            int id
    ) {
        alarmClockList.set(position, alarmClock);
        alarmClockAdapter.notifyItemChanged(position);

        alarmClockIntent.cancelAlarmClockStartRingPendingIntent(id);
        alarmClockIntent.cancelAlarmClockSnoozeRingPendingIntent(id);

        alarmClock.startAlarmToBeginRingAtPreciseTime(
                Integer.parseInt(alarmClock.getStatedTimeHour()),
                Integer.parseInt(alarmClock.getStatedTimeMinute()),
                alarmClock,
                id,
                getApplicationContext(),
                alarmClockIntent);

        alarmClockMethodsToOperateWithDatabase.updateSwitchOnOffForAlarmClockInDatabase(
                alarmClock,
                id);
    }

    private void showAlertDialogDeleteAlarmClock(
            String statedTimeHour,
            int id,
            AlarmClock alarmClock,
            int position
    ) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlarmClockMainActivity.this);
        View view = getLayoutInflater()
                .inflate(R.layout.alert_dialog_delete_alarm_clock, null);

        alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setView(view);
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
            }
        });

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteAlarmClockFromDatabase(statedTimeHour, id, alarmClock, position);
                        alertDialog.dismiss();
                    }
                });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
    }

    private void showAlertDialogAddNewAlarmClock() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AlarmClockMainActivity.this);
        View view = getLayoutInflater()
                .inflate(R.layout.alert_dialog_add_alarm_clock, null);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String stringHour;
        String stringMinute;

        if (hour < 10) {
            stringHour = getString(R.string.zero).concat(String.valueOf(hour));
        } else {
            stringHour = String.valueOf(hour);
        }

        if (minute < 10) {
            stringMinute = getString(R.string.zero).concat(String.valueOf(minute));
        } else {
            stringMinute = String.valueOf(minute);
        }

        numberPickerHour = view.findViewById(R.id.number_picker_hour);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setMaxValue(stringArrayListHours.length - 1);
        numberPickerHour.setDisplayedValues(stringArrayListHours);
        numberPickerHour.setWrapSelectorWheel(true);
        numberPickerHour.setValue(Arrays.asList(stringArrayListHours).indexOf(stringHour));

        numberPickerMinute = view.findViewById(R.id.number_picker_minute);
        numberPickerMinute.setMinValue(0);
        numberPickerMinute.setMaxValue(stringArrayListMinutes.length - 1);
        numberPickerMinute.setDisplayedValues(stringArrayListMinutes);
        numberPickerMinute.setWrapSelectorWheel(true);
        numberPickerMinute.setValue(Arrays.asList(stringArrayListMinutes).indexOf(stringMinute));

        numberPickerRepeat = view.findViewById(R.id.number_picker_repeat);
        numberPickerRepeat.setMinValue(0);
        numberPickerRepeat.setMaxValue(stringArrayListRepeat.length - 1);
        numberPickerRepeat.setDisplayedValues(stringArrayListRepeat);
        numberPickerRepeat.setWrapSelectorWheel(true);

        alertDialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertDialogBuilder.setView(view);
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(20);
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(getResources().getColor(R.color.color2));
            }
        });

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlarmClock alarmClock = new AlarmClock();

                        alarmClock.setStatedTimeHour(stringArrayListHours[numberPickerHour.getValue()]);
                        alarmClock.setStatedTimeMinute(stringArrayListMinutes[numberPickerMinute.getValue()]);
                        alarmClock.setRepeatability(stringArrayListRepeat[numberPickerRepeat.getValue()]);
                        alarmClock.setSwitchOnOff(getResources().getString(R.string.on));

                        for (AlarmClock clock : alarmClockList) {
                            if (clock.getStatedTimeHour().equals(alarmClock.getStatedTimeHour())
                                    && clock.getStatedTimeMinute().equals(alarmClock.getStatedTimeMinute())
                                    && clock.getRepeatability().equals(alarmClock.getRepeatability())) {

                                Toast.makeText(AlarmClockMainActivity.this,
                                        getResources().getString(R.string.the_same_alarm_clock_is_already_exists),
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        addAlarmClockToDatabase(alarmClock);
                        alertDialog.dismiss();
                    }
                });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
    }

    private void addAlarmClockToDatabase(AlarmClock alarmClock) {
        alarmClockList.add(alarmClock);
        alarmClockAdapter.notifyItemInserted(alarmClockList.indexOf(alarmClock));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                alarmClockMethodsToOperateWithDatabase.addAlarmClock(alarmClock);

                int alarmClockId = alarmClockMethodsToOperateWithDatabase.getAlarmClockId(
                        alarmClock.getStatedTimeHour(),
                        alarmClock.getStatedTimeMinute(),
                        alarmClock.getRepeatability(),
                        alarmClock.getSwitchOnOff()
                );

                alarmClock.startAlarmToBeginRingAtPreciseTime(
                        Integer.parseInt(alarmClock.getStatedTimeHour()),
                        Integer.parseInt(alarmClock.getStatedTimeMinute()),
                        alarmClock,
                        alarmClockId,
                        getApplicationContext(),
                        alarmClockIntent);
            }
        });
        thread.start();
    }

    private void deleteAlarmClockFromDatabase(
            String statedTimeHour,
            int id,
            AlarmClock alarmClock,
            int position
    ) {
        int index = alarmClockList.indexOf(alarmClock);
        alarmClockList.remove(position);
        alarmClockAdapter.notifyItemRemoved(position);
        alarmClockAdapter.notifyItemRangeChanged(index, alarmClockAdapter.getItemCount());

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                alarmClockMethodsToOperateWithDatabase.deleteAlarmClock(statedTimeHour, id);
            }
        });
        thread.start();
    }
}
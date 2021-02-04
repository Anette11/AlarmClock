package com.example.alarmclock;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;

import java.util.List;

public class AlarmClockMethodsDatabase extends ContextWrapper {
    private AlarmClockSQLiteOpenHelper alarmClockSQLiteOpenHelper;
    private AlarmClockIntent alarmClockIntent;

    public AlarmClockMethodsDatabase(Context base) {
        super(base);
        initialize();
    }

    private void initialize() {
        alarmClockSQLiteOpenHelper = new AlarmClockSQLiteOpenHelper(this);
        alarmClockIntent = new AlarmClockIntent(this);
    }

    public int getAlarmClockId(AlarmClock alarmClock) {
        return alarmClockSQLiteOpenHelper.getAlarmClockId(
                alarmClock.getStatedTimeHour(),
                alarmClock.getStatedTimeMinute(),
                alarmClock.getRepeatability(),
                alarmClock.getSwitchOnOff());
    }

    public int getAlarmClockId(
            String statedTimeHour,
            String statedTimeMinute,
            String repeatability,
            String switchOnOffAtPosition
    ) {
        return alarmClockSQLiteOpenHelper
                .getAlarmClockId(
                        statedTimeHour,
                        statedTimeMinute,
                        repeatability,
                        switchOnOffAtPosition);
    }

    public void updateSwitchOnOffForAlarmClockInDatabase(AlarmClock alarmClock, int id) {
        String newStatedTimeHour = alarmClock.getStatedTimeHour();
        String newStatedTimeMinute = alarmClock.getStatedTimeMinute();
        String newRepeatability = alarmClock.getRepeatability();
        String newSwitchOnOff = alarmClock.getSwitchOnOff();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                alarmClockSQLiteOpenHelper.updateAlarmClock(
                        id,
                        newStatedTimeHour,
                        newStatedTimeMinute,
                        newRepeatability,
                        newSwitchOnOff
                );
            }
        });
        thread.start();
    }

    public void addAllAlarmClocksFromDatabaseToAlarmClockList
            (List<AlarmClock> alarmClockList) {
        Cursor cursor = alarmClockSQLiteOpenHelper.getAllAlarmClocks();
        while (cursor.moveToNext()) {
            alarmClockList.add(new AlarmClock(
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4))
            );
        }
        cursor.close();
    }

    public void deleteAllAlarmClocksFromDatabase(
            List<AlarmClock> alarmClockList,
            AlarmClockAdapter alarmClockAdapter
    ) {
        if (alarmClockList.size() != 0) {
            for (AlarmClock alarmClock : alarmClockList) {
                int alarmClockId = getAlarmClockId(alarmClock);
                alarmClockIntent.cancelAlarmClockStartRingPendingIntent(alarmClockId);
                alarmClockIntent.cancelAlarmClockSnoozeRingPendingIntent(alarmClockId);
            }
        }

        alarmClockList.clear();
        alarmClockAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                alarmClockSQLiteOpenHelper.deleteAllAlarmClocks();
            }
        });
        thread.start();
    }

    public void addAlarmClock(AlarmClock alarmClock) {
        alarmClockSQLiteOpenHelper.addAlarmClock(alarmClock);
    }

    public void deleteAlarmClock(
            String statedTimeHour,
            int id
    ) {
        alarmClockIntent.cancelAlarmClockStartRingPendingIntent(id);
        alarmClockIntent.cancelAlarmClockSnoozeRingPendingIntent(id);
        alarmClockSQLiteOpenHelper.deleteAlarmClock(statedTimeHour, id);
    }
}
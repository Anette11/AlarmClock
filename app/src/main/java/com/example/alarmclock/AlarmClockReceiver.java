package com.example.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlarmClockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(
                intent.getAction(),
                AlarmClockConstants.ALARM_CLOCK_START_RING_INTENT)
        ) {
            startRingActivityFromIntentRing(context, intent);
        } else if (Objects.equals(
                intent.getAction(),
                AlarmClockConstants.ALARM_CLOCK_START_SNOOZE_INTENT)
        ) {
            startRingActivityFromIntentSnoozed(context, intent);
        } else if (Objects.equals(
                intent.getAction(),
                AlarmClockConstants.BOOT_COMPLETED)
        ) {
            startRingActivityFromIntentBootCompleted(context);
        }
    }

    private void startRingActivityFromIntentRing(Context context, Intent intent) {
        String alarmClockHour = intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_HOUR_STRING);
        String alarmClockMinute = intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_MINUTE_STRING);
        String alarmClockRepeatability = intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_REPEATABILITY_STRING);
        String alarmClockSwitchOnOff = intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_SWITCH_ON_OFF_STRING);
        int requestCode = intent.getIntExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_REQUEST_CODE_INT, 0);

        Intent intentStartAlarmClockRingActivity = new Intent(context, AlarmClockRingActivity.class);

        intentStartAlarmClockRingActivity.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_HOUR_STRING,
                alarmClockHour);
        intentStartAlarmClockRingActivity.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_MINUTE_STRING,
                alarmClockMinute);
        intentStartAlarmClockRingActivity.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REPEATABILITY_STRING,
                alarmClockRepeatability);
        intentStartAlarmClockRingActivity.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_SWITCH_ON_OFF_STRING,
                alarmClockSwitchOnOff);
        intentStartAlarmClockRingActivity.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REQUEST_CODE_INT,
                requestCode);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                context.startActivity(intentStartAlarmClockRingActivity);
            }
        });
    }

    private void startRingActivityFromIntentSnoozed(Context context, Intent intent) {
        Intent intentStartAlarmClockRingActivity = new Intent(context, AlarmClockRingActivity.class);

        int requestCode = intent.getIntExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REQUEST_CODE_INT, 0);

        intentStartAlarmClockRingActivity.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REQUEST_CODE_INT,
                requestCode);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                context.startActivity(intentStartAlarmClockRingActivity);
            }
        });
    }

    private void startRingActivityFromIntentBootCompleted(Context context) {
        AlarmClockIntent alarmClockIntent = new AlarmClockIntent(context);
        List<AlarmClock> alarmClockList = new ArrayList<>();

        AlarmClockMethodsDatabase alarmClockMethodsToOperateWithDatabase
                = new AlarmClockMethodsDatabase(context);

        alarmClockMethodsToOperateWithDatabase.addAllAlarmClocksFromDatabaseToAlarmClockList(alarmClockList);

        for (AlarmClock alarmClock : alarmClockList) {
            int alarmClockId = alarmClockMethodsToOperateWithDatabase.getAlarmClockId(alarmClock);

            alarmClock.startAlarmToBeginRingAtPreciseTime(
                    Integer.parseInt(alarmClock.getStatedTimeHour()),
                    Integer.parseInt(alarmClock.getStatedTimeMinute()),
                    alarmClock,
                    alarmClockId,
                    context,
                    alarmClockIntent);
        }
    }
}

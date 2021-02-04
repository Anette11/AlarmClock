package com.example.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import java.util.Calendar;

public class AlarmClockIntent extends ContextWrapper {

    public AlarmClockIntent(Context base) {
        super(base);
    }

    public void cancelAlarmClockStartRingPendingIntent(int alarmClockId) {
        Intent intent = new Intent(getApplicationContext(), AlarmClockReceiver.class);
        intent.setAction(AlarmClockConstants.ALARM_CLOCK_START_RING_INTENT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(),
                alarmClockId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }

    public void cancelAlarmClockSnoozeRingPendingIntent(int alarmClockId) {
        Intent intent = new Intent(getApplicationContext(), AlarmClockReceiver.class);
        intent.setAction(AlarmClockConstants.ALARM_CLOCK_START_SNOOZE_INTENT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getBaseContext(),
                (-1) * alarmClockId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
        pendingIntent.cancel();
    }

    public PendingIntent getPendingIntentForStartingAlarmClock(
            String alarmClockHour,
            String alarmClockMinute,
            String alarmClockRepeatability,
            String alarmClockSwitchOnOff,
            int requestCode
    ) {
        Intent intent = new Intent(this, AlarmClockReceiver.class);

        intent.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_HOUR_STRING,
                alarmClockHour);
        intent.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_MINUTE_STRING,
                alarmClockMinute);
        intent.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_REPEATABILITY_STRING,
                alarmClockRepeatability);
        intent.putExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_SWITCH_ON_OFF_STRING,
                alarmClockSwitchOnOff);
        intent.putExtra(AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_TO_RECEIVER_REQUEST_CODE_INT,
                requestCode);

        intent.setAction(AlarmClockConstants.ALARM_CLOCK_START_RING_INTENT);

        return PendingIntent.getBroadcast(this, requestCode, intent, 0);
    }

    public void sendIntentRepeatSnoozedAlarmClock(int requestCode) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, AlarmClockConstants.AMOUNT_OF_MINUTES_TO_SNOOZE_ALARM_CLOCK);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (requestCode > 0) {
            requestCode = (-1) * requestCode;
        }

        Intent intent = new Intent(getApplicationContext(), AlarmClockReceiver.class);

        intent.setAction(AlarmClockConstants.ALARM_CLOCK_START_SNOOZE_INTENT);
        intent.putExtra(AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REQUEST_CODE_INT, requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, 0);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}

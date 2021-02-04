package com.example.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import java.io.Serializable;
import java.util.Calendar;

public class AlarmClock implements Serializable {
    private String statedTimeHour;
    private String statedTimeMinute;
    private String repeatability;
    private String switchOnOff;

    public AlarmClock() {
    }

    public AlarmClock(
            String statedTimeHour,
            String statedTimeMinute,
            String repeatability,
            String switchOnOff
    ) {
        this.statedTimeHour = statedTimeHour;
        this.statedTimeMinute = statedTimeMinute;
        this.repeatability = repeatability;
        this.switchOnOff = switchOnOff;
    }

    public String getStatedTimeHour() {
        return statedTimeHour;
    }

    public void setStatedTimeHour(String statedTimeHour) {
        this.statedTimeHour = statedTimeHour;
    }

    public String getStatedTimeMinute() {
        return statedTimeMinute;
    }

    public void setStatedTimeMinute(String statedTimeMinute) {
        this.statedTimeMinute = statedTimeMinute;
    }

    public String getRepeatability() {
        return repeatability;
    }

    public void setRepeatability(String repeatability) {
        this.repeatability = repeatability;
    }

    public String getSwitchOnOff() {
        return switchOnOff;
    }

    public void setSwitchOnOff(String switchOnOff) {
        this.switchOnOff = switchOnOff;
    }

    public void startAlarmToBeginRingAtPreciseTime(
            int hour,
            int minute,
            AlarmClock alarmClock,
            int requestCode,
            Context context,
            AlarmClockIntent alarmClockIntent
    ) {
        String alarmClockHour = alarmClock.getStatedTimeHour();
        String alarmClockMinute = alarmClock.getStatedTimeMinute();
        String alarmClockRepeatability = alarmClock.getRepeatability();
        String alarmClockSwitchOnOff = alarmClock.getSwitchOnOff();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pendingIntent = alarmClockIntent.getPendingIntentForStartingAlarmClock(
                alarmClockHour,
                alarmClockMinute,
                alarmClockRepeatability,
                alarmClockSwitchOnOff,
                requestCode);

        Calendar calendar = setHourAndMinuteInCalendar(hour, minute);

        if (alarmManager != null) {
            setAlarmClockRepeatability(
                    alarmClockRepeatability,
                    alarmManager, calendar,
                    pendingIntent,
                    context);
        }
    }

    private Calendar setHourAndMinuteInCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private void setAlarmClockRepeatability(
            String alarmClockRepeatability,
            AlarmManager alarmManager,
            Calendar calendar,
            PendingIntent pendingIntent,
            Context context
    ) {
        if (alarmClockRepeatability.equals(context.getResources().getString(R.string.one_time))) {
            setAlarmAtExactTime(alarmManager, calendar, pendingIntent);
        } else if (alarmClockRepeatability.equals(context.getResources().getString(R.string.every_day))) {
            setAlarmWithRepeatIntervalEveryDay(alarmManager, calendar, pendingIntent);
        } else {
            setAlarmWithRepeatIntervalEveryWeek(
                    calendar,
                    alarmClockRepeatability,
                    alarmManager,
                    pendingIntent,
                    context);
        }
    }

    private void setAlarmAtExactTime(
            AlarmManager alarmManager,
            Calendar calendar,
            PendingIntent pendingIntent
    ) {
        checkIfCalendarIsBeforeForOneDay(calendar);
        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent);
    }

    private void setAlarmWithRepeatIntervalEveryDay(
            AlarmManager alarmManager,
            Calendar calendar,
            PendingIntent pendingIntent
    ) {
        checkIfCalendarIsBeforeForOneDay(calendar);
        final long REPEAT_INTERVAL_EVERY_DAY = AlarmManager.INTERVAL_DAY;
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                REPEAT_INTERVAL_EVERY_DAY,
                pendingIntent);
    }

    private void setAlarmWithRepeatIntervalEveryWeek(
            Calendar calendar,
            String alarmClockRepeatability,
            AlarmManager alarmManager,
            PendingIntent pendingIntent,
            Context context
    ) {
        int dayOfWeekAlarmClock = getCalendarDayOfWeek(alarmClockRepeatability, context);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeekAlarmClock);

        checkIfCalendarIsBeforeForOneWeek(calendar);

        final long REPEAT_INTERVAL_EVERY_WEEK = AlarmManager.INTERVAL_DAY * 7;
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                REPEAT_INTERVAL_EVERY_WEEK,
                pendingIntent);
    }

    private int getCalendarDayOfWeek(String alarmClockRepeatability, Context context) {
        int dayOfWeek;
        if (alarmClockRepeatability.equals(context.getResources()
                .getString(R.string.every_monday))) {
            dayOfWeek = Calendar.MONDAY;
        } else if (alarmClockRepeatability.equals(context.getResources()
                .getString(R.string.every_tuesday))) {
            dayOfWeek = Calendar.TUESDAY;
        } else if (alarmClockRepeatability.equals(context.getResources()
                .getString(R.string.every_wednesday))) {
            dayOfWeek = Calendar.WEDNESDAY;
        } else if (alarmClockRepeatability.equals(context.getResources()
                .getString(R.string.every_thursday))) {
            dayOfWeek = Calendar.THURSDAY;
        } else if (alarmClockRepeatability.equals(context.getResources()
                .getString(R.string.every_friday))) {
            dayOfWeek = Calendar.FRIDAY;
        } else if (alarmClockRepeatability.equals(context.getResources()
                .getString(R.string.every_saturday))) {
            dayOfWeek = Calendar.SATURDAY;
        } else {
            dayOfWeek = Calendar.SUNDAY;
        }
        return dayOfWeek;
    }

    private void checkIfCalendarIsBeforeForOneDay(Calendar calendar) {
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void checkIfCalendarIsBeforeForOneWeek(Calendar calendar) {
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
        }
    }
}

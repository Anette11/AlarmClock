package com.example.alarmclock;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class AlarmClockRingActivity extends AppCompatActivity {
    private Ringtone ringtone;
    private ImageView imageViewAlarmOn;
    private ValueAnimator valueAnimator;
    private AlarmClockMethodsDatabase alarmClockMethodsToOperateWithDatabase;
    private AlarmClockIntent alarmClockIntent;
    private Intent intent;
    private int requestCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock_ring);
        switchScreenOn();
        initialize();
    }

    private void switchScreenOn() {
        final Window window = getWindow();
        window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    private void initialize() {
        alarmClockMethodsToOperateWithDatabase = new AlarmClockMethodsDatabase(this);
        alarmClockIntent = new AlarmClockIntent(this);
        startRingtone();
        startImageViewAlarmClockAnimation();
        setTextInTextViewHourAndTextViewMinute();
        addButtonAlarmClockStop();
        addButtonAlarmClockSnooze();

        intent = getIntent();
        requestCode = intent.getIntExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REQUEST_CODE_INT,
                0);
    }

    private void setTextInTextViewHourAndTextViewMinute() {
        TextView textViewHour = findViewById(R.id.text_view_hour);
        TextView textViewMinute = findViewById(R.id.text_view_minute);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String stringHour;
        if (hour <= 9) {
            stringHour = getString(R.string.zero).concat(String.valueOf(hour));
        } else {
            stringHour = String.valueOf(hour);
        }

        String stringMinute;
        if (minute <= 9) {
            stringMinute = getString(R.string.zero).concat(String.valueOf(minute));
        } else {
            stringMinute = String.valueOf(minute);
        }

        textViewHour.setText(stringHour);
        textViewMinute.setText(stringMinute);
    }

    private void startRingtone() {
        ringtone = new AlarmClockRing().initializeRingtone(getApplicationContext());
        ringtone.play();
    }

    private void stopRingtone() {
        if (ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private void startImageViewAlarmClockAnimation() {
        imageViewAlarmOn = findViewById(R.id.image_view_alarm_on);
        valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                imageViewAlarmOn.setAlpha((Float) animation.getAnimatedValue());
            }
        });

        valueAnimator.setDuration(500);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.start();
    }

    private void stopAlarmClockImageViewAnimation() {
        valueAnimator.end();
    }

    private void addButtonAlarmClockStop() {
        Button buttonStop = findViewById(R.id.button_stop);
        setButtonBackground(buttonStop);

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRingtoneAndAnimationAndSetSwitchAsOffInDatabase();
            }
        });
    }

    private void setButtonBackground(Button button) {
        button.setBackground(
                ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.button_alarm_clock_snooze_stop_background));
    }

    private void stopRingtoneAndAnimationAndSetSwitchAsOffInDatabase() {
        stopRingtone();
        stopAlarmClockImageViewAnimation();
        setSwitchAsOffInDatabase();
        finishAlarmClockRingActivity();
    }

    private void setSwitchAsOffInDatabase() {
        String alarmClockRepeatability = intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REPEATABILITY_STRING);

        if (alarmClockRepeatability != null) {
            if (alarmClockRepeatability.equals(getResources().getString(R.string.one_time))) {
                setAlarmClockSwitchAsOffInDataBase();
            }
        }
    }

    private void finishAlarmClockRingActivity() {
        ActivityCompat.finishAffinity(AlarmClockRingActivity.this);
    }

    private void addButtonAlarmClockSnooze() {
        Button buttonSnooze = findViewById(R.id.button_snooze);
        setButtonBackground(buttonSnooze);

        buttonSnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmClockIntent.sendIntentRepeatSnoozedAlarmClock(requestCode);
                stopRingtoneAndAnimationAndSetSwitchAsOffInDatabase();
                showToastMessageAboutAlarmClockIsSnoozed();
            }
        });
    }

    private void showToastMessageAboutAlarmClockIsSnoozed() {
        Toast.makeText(AlarmClockRingActivity.this,
                getString(R.string.alarm_clock_is_snoozed_for)
                        + AlarmClockConstants.AMOUNT_OF_MINUTES_TO_SNOOZE_ALARM_CLOCK
                        + getString(R.string.minutes), Toast.LENGTH_SHORT)
                .show();
    }

    private void setAlarmClockSwitchAsOffInDataBase() {
        AlarmClock alarmClock = getAlarmClockFromIntentReceived();
        int alarmClockId = alarmClockMethodsToOperateWithDatabase.getAlarmClockId(alarmClock);
        alarmClock.setSwitchOnOff(getString(R.string.off));
        alarmClockMethodsToOperateWithDatabase.updateSwitchOnOffForAlarmClockInDatabase(alarmClock, alarmClockId);
    }

    private AlarmClock getAlarmClockFromIntentReceived() {
        Intent intent = getIntent();
        AlarmClock alarmClock = new AlarmClock();

        alarmClock.setStatedTimeHour(intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_HOUR_STRING));
        alarmClock.setStatedTimeMinute(intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_MINUTE_STRING));
        alarmClock.setRepeatability(intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_REPEATABILITY_STRING));
        alarmClock.setSwitchOnOff(intent.getStringExtra(
                AlarmClockConstants.ALARM_CLOCK_SEND_INTENT_FROM_RECEIVER_SWITCH_ON_OFF_STRING));
        return alarmClock;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone.isPlaying()) {
            ringtone.stop();
        }
    }
}

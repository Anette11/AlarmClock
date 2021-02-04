package com.example.alarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AlarmClockSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "alarmClockTable";
    private static final int DATABASE_VERSION = 1;
    private static final String COLUMN_1 = "id";
    private static final String COLUMN_2 = "statedTimeHour";
    private static final String COLUMN_3 = "statedTimeMinute";
    private static final String COLUMN_4 = "repeatability";
    private static final String COLUMN_5 = "switchOnOff";

    public AlarmClockSQLiteOpenHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_2 + " TEXT,"
                + COLUMN_3 + " TEXT,"
                + COLUMN_4 + " TEXT,"
                + COLUMN_5 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase sqLiteDatabase,
            int oldVersion,
            int newVersion
    ) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void addAlarmClock(AlarmClock alarmClock) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, alarmClock.getStatedTimeHour());
        contentValues.put(COLUMN_3, alarmClock.getStatedTimeMinute());
        contentValues.put(COLUMN_4, alarmClock.getRepeatability());
        contentValues.put(COLUMN_5, alarmClock.getSwitchOnOff());
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public Cursor getAllAlarmClocks() {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT * FROM alarmClockTable ORDER BY id ASC";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        return cursor;
    }

    public int getAlarmClockId(
            String statedTimeHour,
            String statedTimeMinute,
            String repeatability,
            String switchOnOff
    ) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_1 + " FROM " + TABLE_NAME
                + " WHERE " + COLUMN_2 + " = '" + statedTimeHour + "'"
                + " AND " + COLUMN_3 + " = '" + statedTimeMinute + "'"
                + " AND " + COLUMN_4 + " = '" + repeatability + "'"
                + " AND " + COLUMN_5 + " = '" + switchOnOff + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        int itemId = -1;

        while (cursor.moveToNext()) {
            itemId = cursor.getInt(0);
        }
        return itemId;
    }

    public void updateAlarmClock(
            int id,
            String newStatedTimeHour,
            String newStatedTimeMinute,
            String newRepeatability,
            String newSwitchOnOff
    ) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_2, newStatedTimeHour);
        contentValues.put(COLUMN_3, newStatedTimeMinute);
        contentValues.put(COLUMN_4, newRepeatability);
        contentValues.put(COLUMN_5, newSwitchOnOff);
        sqLiteDatabase.update(TABLE_NAME, contentValues,
                COLUMN_1 + "= '" + id + "'", null);
    }

    public void deleteAllAlarmClocks() {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + COLUMN_2 + " FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            sqLiteDatabase.execSQL("DELETE FROM " + TABLE_NAME);
        }
    }

    public void deleteAlarmClock(String statedTimeHour, int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_1
                + " = '" + id + "' AND " + COLUMN_2 + " = '" + statedTimeHour + "'";
        sqLiteDatabase.execSQL(query);
    }
}

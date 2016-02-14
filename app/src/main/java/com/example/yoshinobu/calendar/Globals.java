package com.example.yoshinobu.calendar;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yoshinobu on 2016/02/14.
 */
public class Globals extends Application {

    private static final String TAG = MonthFragment.class.getSimpleName();

    private ArrayList<CalendarAccount> calAccounts = new ArrayList<CalendarAccount>();
    private Activity activity;

    public void init(Activity activity){
        this.activity = activity;
        setCalendarAccount();
    }

    public void setCalendarAccount(){
        Log.i(TAG, "setCalendarAccount----------------------");
        ContentResolver cr = activity.getContentResolver();

        String[] projection = {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
        };
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            String selection = "(" +
                    "(" + CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " = ?)" +
                    ")";
            String[] selectionArgs = new String[]{"700"};
            //Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, projection, selection, selectionArgs, null);
            Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
            for (boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext()) {
                String id = cursor.getString(0);
                String name = cursor.getString(1);
                String accountName = cursor.getString(2);
                String accountType = cursor.getString(3);
                CalendarAccount calAccount = new CalendarAccount(id,name,accountName,accountType);
                calAccounts.add(calAccount);
                Log.i(TAG, id + ":" + name);
                Log.i(TAG, accountName);
                Log.i(TAG, accountType);
                Log.i(TAG, "-----------------------------------");
            }
            cursor.close();
        } else {
            Log.i(TAG, "NO_READ_CALENDAR");
        }
    }
    public  ArrayList<CalendarEvent> getCalendarEvent(long start ,long end){
        ContentResolver cr = activity.getContentResolver();
        String[] projection = {
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.CALENDAR_COLOR,
        };
        String selection = "(" +
                "(" + CalendarContract.Events.DTSTART + " >= ?) AND " +
                "(" + CalendarContract.Events.DTEND + " <= ?) AND (" +
                "(" + CalendarContract.Events.CALENDAR_ID + " = ?)";


        for(int i = 1;i < calAccounts.size();i++) {
            selection += " OR (" + CalendarContract.Events.CALENDAR_ID + " = ?)";
        }
        selection += "))";
        String sortOrder = CalendarContract.Events.DTSTART;
        ArrayList<CalendarEvent> calEvents = new ArrayList<CalendarEvent>();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            String[] selectionArgs = new String[calAccounts.size() + 2];
            selectionArgs[0] = Long.toString(start);
            selectionArgs[1] = Long.toString(end);
            int selectionArgsCount = 2;
            for (CalendarAccount value : calAccounts) {
                selectionArgs[selectionArgsCount] = value.id;
                selectionArgsCount++;
            }
            //String[] selectionArgs = new String[]{Long.toString(start), Long.toString(end), value.id};
            Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
            SimpleDateFormat format = new SimpleDateFormat("M-d H:mm", Locale.JAPAN);
            for (boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext()) {
                long eventID = cursor.getLong(0);
                String title = cursor.getString(1);
                long startSec = cursor.getLong(2);
                long endSec = cursor.getLong(3);
                int calendarColor = cursor.getInt(4);
                CalendarEvent calEvent = new CalendarEvent(eventID,title,startSec,endSec,calendarColor);
                Log.i(TAG, eventID + ":" + title + ":" + calendarColor);
                Log.i(TAG, format.format(startSec) + " - " + format.format(endSec));
                Log.i(TAG, "-----------------------------------");
                calEvents.add(calEvent);
            }
            cursor.close();
        }
        return calEvents;
    }

}

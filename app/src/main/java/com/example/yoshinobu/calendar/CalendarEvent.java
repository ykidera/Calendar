package com.example.yoshinobu.calendar;

/**
 * Created by yoshinobu on 2016/02/14.
 */
public class CalendarEvent {
    long eventID;
    String title;
    long startSec;
    long endSec;
    int calendarColor;
    CalendarEvent(long eventID,String title,long startSec,long endSec,int calendarColor){
        this.eventID = eventID;
        this.title = title;
        this.startSec = startSec;
        this.endSec = endSec;
        this.calendarColor = calendarColor;
    }
}

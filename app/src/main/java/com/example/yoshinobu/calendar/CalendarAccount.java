package com.example.yoshinobu.calendar;

/**
 * Created by yoshinobu on 2016/02/14.
 */
public class CalendarAccount{
    String id;
    String name;
    String AccountName;
    String AccountType;
    CalendarAccount(String id,String name,String AccountName,String AccountType){
        this.id = id;
        this.name = name;
        this.AccountName = AccountName;
        this.AccountType = AccountType;
    }
}
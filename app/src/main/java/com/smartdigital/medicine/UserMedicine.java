package com.smartdigital.medicine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Random;

import static com.smartdigital.medicine.util.Constants.*;

public class UserMedicine
{
    private int id;
    private final String name;
    private String targetName = "Unknown";
    private float duration = 0;
    private int hour;
    private int minute;
    private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    //todo: add time of day and other drug info here

    public UserMedicine(String name)
    {
        this.name = name;
    }

    public UserMedicine(String name, String targetName)
    {
        this.name = name;
        this.targetName = targetName;
    }

    public UserMedicine(int id, String name, String targetName, float duration, int hour, int minute, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday)
    {
        this.id = id;
        this.name = name;
        this.targetName = targetName;
        this.duration = duration;
        this.hour = hour;
        this.minute = minute;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }


    public String getName()
    {
        return this.name;
    }

    public String getTargetName()
    {
        return this.targetName;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    @NotNull
    public String toString()
    {

        return "id: " + id + "\n" +
                "name: " + name + "\n" +
                "target name: " + targetName + "\n" +
                "duration: " + duration + "\n" +
                "time: " + hour + ": " + minute + "\n" +
                "monday: " + monday + "\n" +
                "tuesday: " + tuesday + "\n" +
                "wednesday: " + wednesday + "\n" +
                "thursday: " + thursday + "\n" +
                "friday: " + friday + "\n" +
                "saturday: " + saturday + "\n" +
                "sunday: " + sunday + "\n";
    }



    public void schedule(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);

        intent.putExtra(NAME, name);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmPendingIntent);


    }
}

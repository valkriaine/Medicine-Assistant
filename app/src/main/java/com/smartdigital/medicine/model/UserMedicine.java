package com.smartdigital.medicine.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.smartdigital.medicine.AlarmReceiver;
import org.jetbrains.annotations.NotNull;
import java.util.Calendar;
import static com.smartdigital.medicine.util.Constants.*;

@Entity
public class UserMedicine
{

    //SQLite fields

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private final String name;

    @ColumnInfo(name = "target_name")
    private String targetName = "Unknown";

    @ColumnInfo(name = "duration")
    private float duration = 0;

    @ColumnInfo(name = "hour")
    private int hour;

    @ColumnInfo(name = "minute")
    private int minute;

    @ColumnInfo(name = "monday")
    private boolean monday;

    @ColumnInfo(name = "tuesday")
    private boolean tuesday;

    @ColumnInfo(name = "wednesday")
    private boolean wednesday;

    @ColumnInfo(name = "thursday")
    private boolean thursday;

    @ColumnInfo(name = "friday")
    private boolean friday;

    @ColumnInfo(name = "saturday")
    private boolean saturday;

    @ColumnInfo(name = "sunday")
    private boolean sunday;



    //constructors
    @Ignore
    public UserMedicine(String name)
    {
        this.name = name;
    }

    @Ignore
    public UserMedicine(String name, String targetName)
    {
        this.name = name;
        this.targetName = targetName;
    }


    public UserMedicine(int id,
                        String name,
                        String targetName,
                        float duration,
                        int hour,
                        int minute,
                        boolean monday,
                        boolean tuesday,
                        boolean wednesday,
                        boolean thursday,
                        boolean friday,
                        boolean saturday,
                        boolean sunday)
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


    @Ignore
    public UserMedicine(Bundle bundle)
    {
        this.id = bundle.getInt(ID);
        this.name = bundle.getString(NAME);
        this.targetName = bundle.getString(TARGET_NAME);
        this.duration = bundle.getFloat(DURATION);
        this.hour = bundle.getInt(HOUR);
        this.minute = bundle.getInt(MINUTE);
        this.monday = bundle.getBoolean(MONDAY);
        this.tuesday = bundle.getBoolean(TUESDAY);
        this.wednesday = bundle.getBoolean(WEDNESDAY);
        this.thursday = bundle.getBoolean(THURSDAY);
        this.friday = bundle.getBoolean(FRIDAY);
        this.saturday = bundle.getBoolean(SATURDAY);
        this.sunday = bundle.getBoolean(SUNDAY);
    }











    //getters and setters

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

    public boolean getMonday()
    {
        return monday;
    }


    public boolean getTuesday()
    {
        return tuesday;
    }


    public boolean getWednesday()
    {
        return wednesday;
    }


    public boolean getThursday()
    {
        return thursday;
    }


    public boolean getFriday()
    {
        return friday;
    }

    public boolean getSaturday()
    {
        return saturday;
    }

    public boolean getSunday()
    {
        return sunday;
    }







    //compare the id when calling .equals()
    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj)
    {
        if (obj instanceof UserMedicine)
            return this.id == ((UserMedicine) obj).getId();
        else
            return false;
    }


    //for logging purpose
    @NotNull
    public String toString()
    {

        return "\nid: " + id + "\n" +
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

    public String getInfo()
    {
        return name + " \n" + targetName;
    }






    //add the alarm to system
    public void schedule(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), generateIntent(context));
    }

    //reschedule the alarm when received
    public void reSchedule(Context context)
    {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY, generateIntent(context));
    }



    //generate PendingIntent object specific to the current UserMedicine object
    public PendingIntent generateIntent(Context context)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(MONDAY, monday);
        intent.putExtra(TUESDAY, tuesday);
        intent.putExtra(WEDNESDAY, wednesday);
        intent.putExtra(THURSDAY, thursday);
        intent.putExtra(FRIDAY, friday);
        intent.putExtra(SATURDAY, saturday);
        intent.putExtra(SUNDAY, sunday);
        intent.putExtra(NAME, name);
        intent.putExtra(TARGET_NAME, targetName);
        intent.putExtra(ID, id);
        intent.putExtra(HOUR, hour);
        intent.putExtra(MINUTE, minute);
        intent.putExtra(DURATION, duration);


       return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

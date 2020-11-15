package com.smartdigital.medicine;

import org.jetbrains.annotations.NotNull;

public class UserMedicine
{
    private int id;
    private final String name;
    private String targetName = "Unknown";
    private float duration = 0;
    private float frequency = 0;
    private int hour;
    private int minute;
    private boolean[] dayOfWeekList = new boolean[7];

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

    public String getName()
    {
        return this.name;
    }

    public String getTargetName()
    {
        return this.targetName;
    }

    @NotNull
    public String toString()
    {
        return getName() + "\n" + getTargetName();
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
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

    public boolean[] getDayOfWeekList() {
        return dayOfWeekList;
    }

    public void setDayOfWeekList(boolean[] dayOfWeekList) {
        this.dayOfWeekList = dayOfWeekList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

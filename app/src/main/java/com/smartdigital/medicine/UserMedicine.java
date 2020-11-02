package com.smartdigital.medicine;

import org.jetbrains.annotations.NotNull;

public class UserMedicine
{
    private final String name;
    private String targetName = "Unknown";
    //todo: add more medicine information here

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

}

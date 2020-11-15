package com.smartdigital.medicine.model;

public class SuggestionMedicine
{
    private final String name;
    private final String targetName;

    public SuggestionMedicine(String name, String targetName)
    {
        this.name = name;
        this.targetName = targetName;
    }


    public String getName() {
        return name;
    }

    public String getTargetName() {
        return targetName;
    }

    public UserMedicine toUserMedicine()
    {
        return new UserMedicine(name, targetName);
    }

    public String toString()
    {
        return name + "\n" + targetName;
    }
}

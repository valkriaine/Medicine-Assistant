package com.smartdigital.medicine;

public class UserMedicine
{
    private String name;
    //todo: add more medicine information here

    public UserMedicine(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public String toString()
    {
        return getName();
    }

}

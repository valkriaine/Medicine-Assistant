package com.smartdigital.medicine.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.smartdigital.medicine.model.UserMedicine;

@Database(entities = {UserMedicine.class}, version = 1)
public abstract class UserMedicineDatabase extends RoomDatabase
{
    public abstract Dao dao();
}

package com.smartdigital.medicine.database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.smartdigital.medicine.model.UserMedicine;

import java.util.List;

@androidx.room.Dao
public interface Dao
{
    @Query("SELECT * FROM usermedicine")
    List<UserMedicine> getAll();

    @Insert
    void insert(UserMedicine userMedicine);

    @Delete
    void delete(UserMedicine userMedicine);
}

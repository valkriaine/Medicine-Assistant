package com.smartdigital.medicine.util;

import android.content.Context;
import android.database.Cursor;

public class DrugsDatabaseTable
{
    private final DBHelper dbHelper;

    public DrugsDatabaseTable(Context context) {
        dbHelper = new DBHelper(context);
    }


    public Cursor getWordMatches(String query) {
        return dbHelper.getReadableDatabase().rawQuery("select * from drugs where DRUG_NAME like '%" + query + "%'", null);
    }
}

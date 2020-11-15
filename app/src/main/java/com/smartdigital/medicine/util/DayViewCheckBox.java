package com.smartdigital.medicine.util;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import com.smartdigital.medicine.R;

public class DayViewCheckBox extends AppCompatCheckBox {
    private final Context context;

    public DayViewCheckBox(Context context) {
        super(context);
        this.context = context;
    }

    public DayViewCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void setChecked(boolean t){
        if(t) {
            this.setTextColor(Color.RED);
        } else {
            this.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }
        super.setChecked(t);
    }
}

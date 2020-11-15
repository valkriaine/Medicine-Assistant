package com.smartdigital.medicine;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ramotion.fluidslider.FluidSlider;
import com.smartdigital.medicine.databinding.ActivityMainBinding;
import com.smartdigital.medicine.util.CustomSuggestionsAdapter;
import com.smartdigital.medicine.util.DrugsDatabaseTable;
import com.smartdigital.medicine.util.OnPageChangeListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.valkriaine.factor.HomePager;


public class MainActivity extends AppCompatActivity
{

    private ActivityMainBinding binding;

    private UserDataManager userDataManager;
    private DrugsDatabaseTable drugsDatabaseTable;
    private MaterialSearchBar searchBar;
    private CustomSuggestionsAdapter suggestionsAdapter;
    public static UserMedicine currentMed;
    public  int notificationId = 1;

    private final float[] positionDuration = {0f};

    private boolean[] dayOfWeekList = new boolean[7];

    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        userDataManager = new UserDataManager(this);

        //access drugs.db
        drugsDatabaseTable = new DrugsDatabaseTable(this);
        for (int i = 0; i < 7; i++)
        {
            dayOfWeekList[i] = false;
        }


        initializeComponents();







        //alarm code








    }








































    public void confirmAddDrug(View view)
    {
        if (currentMed != null)
        {
            currentMed.setDuration(positionDuration[0]);
            if (currentMed.getDuration() != 0)
            {
                currentMed.setHour(binding.timePicker.getHour());
                currentMed.setMinute(binding.timePicker.getMinute());
                currentMed.setDayOfWeekList(dayOfWeekList);
                userDataManager.add(currentMed);
                SlidingUpPanelLayout drawer = findViewById(R.id.drawer);
                drawer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }
    }


















    private void initializeComponents()
    {
        //setup HomePager
        HomePager homePager = findViewById(R.id.view_pager);
        homePager.addView(findViewById(R.id.search_page), 0);
        homePager.addView(findViewById(R.id.setup_page), 1);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setSuggestionsEnabled(false);

        //setup search bar suggestions
        suggestionsAdapter = new CustomSuggestionsAdapter(homePager, findViewById(R.id.searchBar));
        RecyclerView r = findViewById(R.id.suggestions_list);
        r.setLayoutManager(new LinearLayoutManager(this));
        r.setAdapter(suggestionsAdapter);
        homePager.addOnPageChangeListener(new OnPageChangeListener(findViewById(R.id.search_box), suggestionsAdapter, findViewById(R.id.drug_name)));

        //search for drug after text change in the search bar
        searchBar.addTextChangeListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                suggestionsAdapter.clear();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (homePager.getCurrentItem() != 0)
                    homePager.setCurrentItem(0, true);
                c = drugsDatabaseTable.getWordMatches(searchBar.getText());
                if (!searchBar.getText().isEmpty()) {
                    c.moveToFirst();
                    while (!c.isAfterLast() && suggestionsAdapter.getItemCount() <= 40) {
                        UserMedicine u = new UserMedicine(String.valueOf(c.getString(c.getColumnIndex("DRUG_NAME"))), String.valueOf(c.getString(c.getColumnIndex("TARGET_NAME"))));
                        if (!suggestionsAdapter.contains(u))
                            suggestionsAdapter.addSuggestions(u);
                        c.moveToNext();
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s)
            {}
        });

        //setup RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userDataManager.getAdapter());


        //initialize sliders
        FluidSlider durationSlider = findViewById(R.id.duration_slider);
        TextView durationText = findViewById(R.id.duration_text);

        //setup duration slider
        durationSlider.setStartText("0");
        durationSlider.setEndText("365");
        durationSlider.setPosition(0);
        durationSlider.setPositionListener(aFloat -> {
            //do not update UI here
            positionDuration[0] = (int) (365 * aFloat);
            durationSlider.setBubbleText(String.valueOf(positionDuration[0]));
            return null;
        });
        durationSlider.setEndTrackingListener(() -> {
            durationSlider.setPosition(positionDuration[0] /365);
            //update UI here
            durationText.setText("I am taking medicine for: " + positionDuration[0] + " days.");
            return null;
        });

















    }

    public void onCheckboxClicked(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();

        /** Checking which checkbox was clicked */
        switch (view.getId()) {
            case R.id.dv_monday:
                if (checked) {
                    dayOfWeekList[1] = true;
                } else {
                    dayOfWeekList[1] = false;
                    binding.everyDay.setChecked(false);
                }
                break;
            case R.id.dv_tuesday:
                if (checked) {
                    dayOfWeekList[2] = true;
                } else {
                    dayOfWeekList[2] = false;
                    binding.everyDay.setChecked(false);
                }
                break;
            case R.id.dv_wednesday:
                if (checked) {
                    dayOfWeekList[3] = true;
                } else {
                    dayOfWeekList[3] = false;
                    binding.everyDay.setChecked(false);
                }
                break;
            case R.id.dv_thursday:
                if (checked) {
                    dayOfWeekList[4] = true;
                } else {
                    dayOfWeekList[4] = false;
                    binding.everyDay.setChecked(false);
                }
                break;
            case R.id.dv_friday:
                if (checked) {
                    dayOfWeekList[5] = true;
                } else {
                    dayOfWeekList[5] = false;
                    binding.everyDay.setChecked(false);
                }
                break;
            case R.id.dv_saturday:
                if (checked) {
                    dayOfWeekList[6] = true;
                } else {
                    dayOfWeekList[6] = false;
                    binding.everyDay.setChecked(false);
                }
                break;
            case R.id.dv_sunday:
                if (checked) {
                    dayOfWeekList[0] = true;
                } else {
                    dayOfWeekList[0] = false;
                    binding.everyDay.setChecked(false);
                }
                break;
            case R.id.every_day:
                binding.dvMonday.setChecked(checked);
                binding.dvTuesday.setChecked(checked);
                binding.dvWednesday.setChecked(checked);
                binding.dvThursday.setChecked(checked);
                binding.dvFriday.setChecked(checked);
                binding.dvSaturday.setChecked(checked);
                binding.dvSunday.setChecked(checked);
                for (int i = 0; i < 7; i ++)
                {
                    dayOfWeekList[i] = checked;
                }
                break;
        }
    }
}
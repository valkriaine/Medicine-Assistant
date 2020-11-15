package com.smartdigital.medicine;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.smartdigital.medicine.databinding.ActivityMainBinding;
import com.smartdigital.medicine.model.SuggestionMedicine;
import com.smartdigital.medicine.model.UserMedicine;
import com.smartdigital.medicine.util.CustomSuggestionsAdapter;
import com.smartdigital.medicine.util.DrugsDatabaseTable;
import com.smartdigital.medicine.util.OnPageChangeListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;


public class MainActivity extends AppCompatActivity
{

    private ActivityMainBinding binding;

    private UserDataManager userDataManager;
    private DrugsDatabaseTable drugsDatabaseTable;
    private CustomSuggestionsAdapter suggestionsAdapter;
    public static UserMedicine currentMed;

    private final float[] positionDuration = {0f};


    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        userDataManager = new UserDataManager(this, binding.recyclerview);

        //access drugs.db
        drugsDatabaseTable = new DrugsDatabaseTable(this);

        initializeComponents();


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
                currentMed.setMonday(binding.dvMonday.isChecked());
                currentMed.setTuesday(binding.dvTuesday.isChecked());
                currentMed.setWednesday(binding.dvWednesday.isChecked());
                currentMed.setThursday(binding.dvThursday.isChecked());
                currentMed.setFriday(binding.dvFriday.isChecked());
                currentMed.setSaturday(binding.dvSaturday.isChecked());
                currentMed.setSunday(binding.dvSunday.isChecked());
                userDataManager.add(currentMed);
                Log.d("medicine", currentMed.toString());
                SlidingUpPanelLayout drawer = findViewById(R.id.drawer);
                drawer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        }
    }







    private void initializeComponents()
    {
        //setup HomePager
        binding.viewPager.addView(binding.searchPage, 0);
        binding.viewPager.addView(binding.setupPage, 1);

        binding.searchBar.setSuggestionsEnabled(false);

        //setup search bar suggestions
        suggestionsAdapter = new CustomSuggestionsAdapter(binding.viewPager, binding.searchBar);
        binding.suggestionsList.setLayoutManager(new LinearLayoutManager(this));
        binding.suggestionsList.setAdapter(suggestionsAdapter);
        binding.viewPager.addOnPageChangeListener(new OnPageChangeListener(binding.searchBox, suggestionsAdapter, binding.drugName));

        //search for drug after text change in the search bar
        binding.searchBar.addTextChangeListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                suggestionsAdapter.clear();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (binding.viewPager.getCurrentItem() != 0)
                    binding.viewPager.setCurrentItem(0, true);
                c = drugsDatabaseTable.getWordMatches(binding.searchBar.getText());
                if (!binding.searchBar.getText().isEmpty()) {
                    c.moveToFirst();
                    while (!c.isAfterLast() && suggestionsAdapter.getItemCount() <= 40) {
                        SuggestionMedicine u = new SuggestionMedicine(String.valueOf(c.getString(c.getColumnIndex("DRUG_NAME"))), String.valueOf(c.getString(c.getColumnIndex("TARGET_NAME"))));
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
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(userDataManager.getAdapter());


        //setup duration slider
        binding.durationSlider.setStartText("0");
        binding.durationSlider.setEndText("365");
        binding.durationSlider.setPosition(0);
        binding.durationSlider.setPositionListener(aFloat -> {
            //do not update UI here
            positionDuration[0] = (int) (365 * aFloat);
            binding.durationSlider.setBubbleText(String.valueOf(positionDuration[0]));
            return null;
        });
       binding.durationSlider.setEndTrackingListener(() -> {
            binding.durationSlider.setPosition(positionDuration[0] /365);
            //update UI here
            binding.durationText.setText("I am taking medicine for: " + positionDuration[0] + " days.");
            return null;
        });
    }


    public void onCheckboxClicked(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();
        binding.dvMonday.setChecked(checked);
        binding.dvTuesday.setChecked(checked);
        binding.dvWednesday.setChecked(checked);
        binding.dvThursday.setChecked(checked);
        binding.dvFriday.setChecked(checked);
        binding.dvSaturday.setChecked(checked);
        binding.dvSunday.setChecked(checked);

    }
}
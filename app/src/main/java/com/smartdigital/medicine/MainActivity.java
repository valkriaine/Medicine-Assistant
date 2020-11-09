package com.smartdigital.medicine;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.ramotion.fluidslider.FluidSlider;
import com.smartdigital.medicine.util.CustomSuggestionsAdapter;
import com.smartdigital.medicine.util.DrugsDatabaseTable;
import com.smartdigital.medicine.util.OnPageChangeListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.valkriaine.factor.HomePager;


public class MainActivity extends AppCompatActivity
{

    private final UserDataManager userDataManager = new UserDataManager();
    private DrugsDatabaseTable drugsDatabaseTable;
    private MaterialSearchBar searchBar;
    private CustomSuggestionsAdapter suggestionsAdapter;
    public static UserMedicine currentMed;

    private final float[] positionDuration = {0f};
    private final float[] positionFrequency = {0f};

    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access drugs.db
        drugsDatabaseTable = new DrugsDatabaseTable(this);


        initializeComponents();








    }

    public void confirmAddDrug(View view)
    {
        if (currentMed != null)
        {
            currentMed.setDuration(positionDuration[0]);
            currentMed.setFrequency(positionFrequency[0]);
            if (currentMed.getDuration() != 0 && currentMed.getFrequency() != 0)
            {
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
        FluidSlider frequencySlider = findViewById(R.id.frequency_slider);
        TextView durationText = findViewById(R.id.duration_text);
        TextView frequencyText = findViewById(R.id.frequency_text);

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

        //setup frequency slider
        frequencySlider.setStartText("0");
        frequencySlider.setEndText("7");
        frequencySlider.setPosition(0);
        frequencySlider.setPositionListener(aFloat -> {
            //do not update UI here
            positionFrequency[0] = (int) (7 * aFloat);
            frequencySlider.setBubbleText(String.valueOf(positionFrequency[0]));
            return null;
        });
        frequencySlider.setEndTrackingListener(() -> {
            frequencySlider.setPosition(positionFrequency[0] /7);
            //update UI here
            frequencyText.setText("I am taking medicine every: " + positionFrequency[0] + " days.");
            return null;
        });
    }
}
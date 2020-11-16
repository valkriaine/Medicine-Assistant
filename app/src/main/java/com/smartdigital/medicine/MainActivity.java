package com.smartdigital.medicine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.smartdigital.medicine.databinding.ActivityMainBinding;
import com.smartdigital.medicine.model.SuggestionMedicine;
import com.smartdigital.medicine.model.UserMedicine;
import com.smartdigital.medicine.util.CustomSuggestionsAdapter;
import com.smartdigital.medicine.ui.DayViewCheckBox;
import com.smartdigital.medicine.database.DrugsDatabaseTable;
import com.smartdigital.medicine.uihelper.OnPageChangeListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;


public class MainActivity extends AppCompatActivity
{

    private ActivityMainBinding binding;
    private UserDataManager userDataManager;
    private DrugsDatabaseTable drugsDatabaseTable;
    private CustomSuggestionsAdapter suggestionsAdapter;
    public static UserMedicine currentMed;

    private Bitmap imageBitmap;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private final float[] positionDuration = {0f};

    private Cursor c;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //initialize user data manager
        userDataManager = new UserDataManager(this, binding.recyclerview);


        //access drugs.db
        drugsDatabaseTable = new DrugsDatabaseTable(this);


        initializeComponents();


    }


    //add new medicine and create its alarm
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
                SlidingUpPanelLayout drawer = findViewById(R.id.drawer);
                drawer.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
            else
                Toast.makeText(this, "Please set a duration", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this, "Please setup the medicine", Toast.LENGTH_SHORT).show();
    }







    //initialize UI components
    private void initializeComponents()
    {
        //setup HomePager
        binding.viewPager.addView(binding.searchPage, 0);
        binding.viewPager.addView(binding.setupPage, 1);

        binding.viewPager.setSwipeAllowed(false);
        binding.searchBar.setSuggestionsEnabled(false);

        //setup search bar suggestions
        suggestionsAdapter = new CustomSuggestionsAdapter(binding.viewPager, binding.searchBar);
        binding.suggestionsList.setLayoutManager(new LinearLayoutManager(this));
        binding.suggestionsList.setAdapter(suggestionsAdapter);
        binding.viewPager.addOnPageChangeListener(new OnPageChangeListener(binding.searchBox, suggestionsAdapter, binding.drugName, binding.viewPager, this));

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
            positionDuration[0] = (int) (365 * aFloat);
            binding.durationSlider.setBubbleText(String.valueOf(positionDuration[0]));
            return null;
        });
       binding.durationSlider.setEndTrackingListener(() -> {
            binding.durationSlider.setPosition(positionDuration[0] /365);
            binding.durationText.setText("I am taking medicine for: " + positionDuration[0] + " days.");
            return null;
        });


       binding.openCamera.setOnClickListener(view -> dispatchTakePictureIntent());
    }





    private void dispatchTakePictureIntent()
    {

        int permissionCheck = ContextCompat.checkSelfPermission(binding.cameraImage.getContext(), Manifest.permission.CAMERA);
        if(permissionCheck!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 1888);
        }
        else
            {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager())!=null){
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            binding.cameraImage.setImageBitmap(imageBitmap);
            readTextFromImage();
        }
    }




    private void readTextFromImage()
    {
        FirebaseVisionImage firstFirebaseVisionImage= FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer();
        detector.processImage(firstFirebaseVisionImage)
                .addOnSuccessListener(this::displayTextFromImage)
                .addOnFailureListener(e -> Log.d("image_fail", e.getMessage()));
    }




    private void displayTextFromImage(FirebaseVisionText firebaseVisionText)
    {
        int lastSuggestionCount = 0;
        String searchText = "";
        String text = "unknown";
        List<FirebaseVisionText.TextBlock> blockList = firebaseVisionText.getTextBlocks();
        if(blockList.size()==0){
            Toast.makeText(this, "no text in the image", Toast.LENGTH_SHORT);
        }
        else {
            for(FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks())
            {
                text = block.getText();
                Cursor cursor = drugsDatabaseTable.getWordMatches(text);
                if (cursor.getCount() > lastSuggestionCount)
                {
                    lastSuggestionCount = cursor.getCount();
                    searchText = text;
                }
            }

            if (!searchText.equals(""))
            {
                binding.searchBar.setText(searchText);
                binding.searchBar.requestFocus();
            }
            else
            {
                Toast.makeText(this, "no record found, but you may still add this medicine to your routines!", Toast.LENGTH_SHORT).show();
                currentMed = new UserMedicine(text, text);
                binding.viewPager.setCurrentItem(1, true);
            }

        }
    }



    //click event for "every day"
    public void onCheckboxClicked(View view)
    {
        CheckBox c = (CheckBox) view;
        boolean checked = c.isChecked();
        binding.dvMonday.setChecked(checked);
        binding.dvTuesday.setChecked(checked);
        binding.dvWednesday.setChecked(checked);
        binding.dvThursday.setChecked(checked);
        binding.dvFriday.setChecked(checked);
        binding.dvSaturday.setChecked(checked);
        binding.dvSunday.setChecked(checked);

    }

    public void check(View view)
    {
        DayViewCheckBox d = (DayViewCheckBox) view;
        boolean checked = d.isChecked();
        if (!checked)
            binding.everyDay.setChecked(false);
        else
        {
            if (binding.dvMonday.isChecked() &&
                    binding.dvTuesday.isChecked() &&
                    binding.dvWednesday.isChecked() &&
                    binding.dvThursday.isChecked() &&
                    binding.dvFriday.isChecked() &&
                    binding.dvSaturday.isChecked() &&
                    binding.dvSunday.isChecked())
                binding.everyDay.setChecked(true);
        }
    }
}
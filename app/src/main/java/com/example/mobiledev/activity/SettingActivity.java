package com.example.mobiledev.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mobiledev.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SettingActivity extends AppCompatActivity {

    private boolean isModified;
    private String textSize;
    private String sortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        isModified = false;

        ImageView imageBack = (ImageView) findViewById(R.id.SettingBackButton);

        imageBack.setOnClickListener((v) -> {

            if(isModified){
                openMainActWithChange();
            }
            else{
                onBackPressed();
            }

        });

        List<String> textArr = new ArrayList<>();
        textArr.addAll(Arrays.asList(getResources().getStringArray(R.array.fontsize)));

        String currText = getIntent().getStringExtra("stateTextSize");
        textArr.remove(textArr.indexOf(currText));
        textArr.add(0, currText);

        List<String> sortArr = new ArrayList<>();
        sortArr.addAll(Arrays.asList(getResources().getStringArray(R.array.sort)));

        String currSort = getIntent().getStringExtra("stateSortType");
        sortArr.remove(sortArr.indexOf(currSort));
        sortArr.add(0, currSort);


        Spinner SPFontSize = findViewById(R.id.SPFontSize);
        ArrayAdapter<CharSequence> FSadapter = new ArrayAdapter(
                getApplicationContext(),
                R.layout.spinner_layout,
                textArr);
        SPFontSize.setAdapter(FSadapter);

        TextView SPFontText = (TextView) findViewById(R.id.SPFontSizeText);

        SPFontSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textSize = textArr.get(i);
                isModified = true;
                SPFontText.setText(textArr.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        SPFontText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fontSpinner.setVisibility(Spinner.VISIBLE);
                SPFontSize.performClick();
            }
        });

        Spinner SPSort = findViewById(R.id.SPSort);
        ArrayAdapter<CharSequence> Sadapter = new ArrayAdapter(getApplicationContext(),
                R.layout.spinner_layout,
                sortArr);
        SPSort.setAdapter(Sadapter);

        TextView SPSortText = (TextView) findViewById(R.id.SPSortText);

        SPSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sortType = sortArr.get(i);
                isModified = true;
                SPSortText.setText(sortArr.get(i));

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SPSortText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fontSpinner.setVisibility(Spinner.VISIBLE);
                SPSort.performClick();
            }
        });

        ImageButton IMGBTNDeleted = findViewById(R.id.IMGBTNDeleted);
        View.OnClickListener OCLDeleted = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                openDeletedActivity();
            }
        };
        IMGBTNDeleted.setOnClickListener(OCLDeleted);

        Switch SWDarkMode = findViewById(R.id.SWDarkMode);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();

//        SharedPreferences sharedPreferences = null;
//        sharedPreferences = this.getSharedPreferences("night",0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);

        if(booleanValue){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            SWDarkMode.setChecked(true);
        }

        SharedPreferences finalSharedPreferences = sharedPreferences;
        SWDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    SWDarkMode.setChecked(true);
                    SharedPreferences.Editor editor = finalSharedPreferences.edit();
                    editor.putBoolean("night_mode",true);
                    editor.commit();
                }else{
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    SWDarkMode.setChecked(false);
                    SharedPreferences.Editor editor = finalSharedPreferences.edit();
                    editor.putBoolean("night_mode",false);
                    editor.commit();
                }
            }
        });

    }

    private void openMainActWithChange(){
        String filename = "saveState";
        String fileContents = textSize+"\n"+sortType;
        try (FileOutputStream fos = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("setting_change", MainActivity.REQUEST_SETTING_CHANGE+"");
        startActivity(intent);
    }

    private void openDeletedActivity(){
        Intent intent = new Intent(this, DeletedNotes.class);
        startActivity(intent);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        try {
//            Navigation.findNavController(this, R.id.NHFMain).navigate(item.getItemId());
//            return true;
//        }catch (Exception ex){
//            return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        return Navigation.findNavController(this, R.id.NHFMain).navigateUp();
//    }
}


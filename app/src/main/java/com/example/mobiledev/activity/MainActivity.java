package com.example.mobiledev.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobiledev.R;
import com.example.mobiledev.database.NoteDatabase;
import com.example.mobiledev.entities.Note;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    public static final int REQUEST_ADD_NOTE = 1;
    public static final int REQUEST_OVERWRITE_NOTE = 2;
    public static final int REQUEST_SHOW_NOTE = 3;
    public static final int REQUEST_DELETE_NOTE = 4;

    public static final int REQUEST_SETTING_SHOW = 5;
    public static final int REQUEST_SETTING_CHANGE = 6;

    private FirebaseAuth mFirebaseAuth;

    private RecyclerView noteRecyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;


    private int noteClickedPos = -1;
    private Note clickedNote;

    private String stateSort;
    private String stateTextSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getSaveState();
        mFirebaseAuth = FirebaseAuth.getInstance();

        Button accountButton = (Button) findViewById(R.id.AccountBtn);
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null){
            //there is user
            accountButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    openActivitySign();
                }
            });
        } else {
            //not logged
            accountButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View V){
                    openActivityLogin();
                }
            });
        }


        ImageView createNoteButton = (ImageView) findViewById(R.id.imageAdd);
        createNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityNote(REQUEST_ADD_NOTE);
            }
        });

        ImageView settingButton = (ImageView) findViewById(R.id.settingBtn);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingActivity(REQUEST_SETTING_SHOW);
            }
        });

        noteRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        noteRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this, true);
        noteRecyclerView.setAdapter(noteAdapter);
        System.out.println("asd DEEBUG"+noteAdapter.getItemCount()+"fsdsdfsdfsdfsdfsdfdfdfsdf");

        getNotes(REQUEST_SHOW_NOTE, false);

        //new
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);

        if(booleanValue){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            SWDarkMode.setChecked(true);
        }

    }

    public String getStateSort() {
        return stateSort;
    }

    public String getStateTextSize() {
        return stateTextSize;
    }

    private void createSaveFile(){
        String filename = "saveState";
        String fileContents = "Medium\nModification Date";
        try (FileOutputStream fos = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSaveState(){

        FileInputStream fis = null;
        try {
            fis = getApplicationContext().openFileInput("saveState");
        } catch (FileNotFoundException e) {
            createSaveFile();
            getSaveState();
            return;
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            stateTextSize = line;
            line = reader.readLine();
            stateSort = line;
            reader.close();
            fis.close();
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            String contents = stringBuilder.toString();
        }

    }

    public void getNotes(int requestCode, final boolean isNoteDeleted){



        class GetNoteTask extends AsyncTask<Void, Void, List<Note>> {


            @Override
            protected List<Note> doInBackground(Void... voids) {
                if(stateSort.equals("Modification Date")){
                    return NoteDatabase.getDatabase(getApplicationContext())
                            .noteDao().getAllNotesByDate("No");
                }else if(stateSort.equals("Name")){
                    return NoteDatabase.getDatabase(getApplicationContext())
                            .noteDao().getAllNotesByName("No");
                }else
                    return null;

            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_SHOW_NOTE) {
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    noteRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_OVERWRITE_NOTE) {
                    noteList.remove(noteClickedPos);
                    noteList.add(noteClickedPos, notes.get(noteClickedPos));
                    noteAdapter.notifyItemChanged(noteClickedPos);

                }
                else if(requestCode == REQUEST_DELETE_NOTE){
                    try{
                        noteList.remove(noteClickedPos);
                        notes.remove(noteClickedPos);
                    }catch(IndexOutOfBoundsException e){
                    }

                    noteAdapter.notifyItemRemoved(noteClickedPos);
                }

                if(notes.isEmpty()){
                    showEmpty();
                }

            }
        }
        new GetNoteTask().execute();


    }

    public void showEmpty(){
        Toast.makeText(this, "Empty Notes", Toast.LENGTH_SHORT).show();
    }

    private void openActivitySign(){
        Intent intent = new Intent(this, SignActivity.class);
        startActivity(intent);
    }

    private void openActivityLogin() {
        Intent intent = new Intent (this,LoginActivity.class);
        startActivity(intent);
    }

    private void openActivityNote(int requestCode){
        Intent intent = new Intent(this, NoteAppActivity.class);
        startActivityForResult(intent, requestCode);
    }

    private void openSettingActivity(int requestCode){
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra("stateTextSize", stateTextSize);
        intent.putExtra("stateSortType", stateSort);
        startActivityForResult(intent, requestCode);
    }

    public void setNoteClickedPos(int p){
        this.noteClickedPos = p;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_ADD_NOTE, false);
        }
        else if (requestCode == REQUEST_OVERWRITE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_OVERWRITE_NOTE, false);
            }
        }
        else if(requestCode == REQUEST_SETTING_CHANGE){
            getSaveState();
        }
    }

    public void onNoteClicked(Note note, int position) {
        noteClickedPos = position;
        clickedNote = note;
        Intent intent = new Intent(getApplicationContext(), NoteAppActivity.class);
        intent.putExtra("isUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_OVERWRITE_NOTE);
    }
}


package com.example.mobiledev.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mobiledev.R;
import com.example.mobiledev.database.NoteDatabase;
import com.example.mobiledev.entities.Note;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DeletedNotes extends AppCompatActivity {
    public static final int REQUEST_ADD_NOTE = 1;
    public static final int REQUEST_OVERWRITE_NOTE = 2;
    public static final int REQUEST_SHOW_NOTE = 3;
    public static final int REQUEST_DELETE_NOTE = 4;

    public static final int REQUEST_SETTING_SHOW = 5;
    public static final int REQUEST_SETTING_CHANGE = 6;

    private RecyclerView noteRecyclerView2;
    private NoteAdapter noteAdapter2;
    private List<Note> noteList2;


    private int noteClickedPos = -1;
    private Note clickedNote;

    private String stateSort;
    private String stateTextSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_notes);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getSaveState();

        ImageView imageBack = (ImageView) findViewById(R.id.DeleteBackButton);
        imageBack.setOnClickListener((v -> {onBackPressed();}));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);

        if(booleanValue){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        noteRecyclerView2 = (RecyclerView) findViewById(R.id.mainRecyclerView2);
        noteRecyclerView2.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList2 = new ArrayList<>();
        noteAdapter2 = new NoteAdapter(noteList2, this, false);
        noteRecyclerView2.setAdapter(noteAdapter2);

        getNotes(REQUEST_SHOW_NOTE, false);

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
            fis = getApplicationContext().openFileInput("saveState"
            );
            System.out.println("Main Act:   "+getFilesDir());
        } catch (FileNotFoundException e) {
            createSaveFile();
            getSaveState();
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

    public void showEmpty(){
        Toast.makeText(this, "No Deleted Notes", Toast.LENGTH_SHORT).show();
    }

    public void getNotes(int requestCode, final boolean isNoteDeleted){

        class GetNoteTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {

                return NoteDatabase.getDatabase(getApplicationContext())
                        .noteDao().getAllDeletedNotes("Yes");


            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_SHOW_NOTE) {
                    noteList2.addAll(notes);
                    noteAdapter2.notifyDataSetChanged();
                } else if (requestCode == REQUEST_ADD_NOTE) {
                    noteList2.add(0, notes.get(0));
                    noteAdapter2.notifyItemInserted(0);
                    noteRecyclerView2.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_OVERWRITE_NOTE) {
                    noteList2.remove(noteClickedPos);
                    noteList2.add(noteClickedPos, notes.get(noteClickedPos));
                    noteAdapter2.notifyItemChanged(noteClickedPos);

                }
                else if(requestCode == REQUEST_DELETE_NOTE){
                    noteList2.remove(noteClickedPos);
                    noteAdapter2.notifyItemRemoved(noteClickedPos);
                }

                if(notes.isEmpty()){
                    showEmpty();
                }

            }
        }
        new GetNoteTask().execute();
    }

    private void openActivitySign(){
        Intent intent = new Intent(this, SignActivity.class);
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
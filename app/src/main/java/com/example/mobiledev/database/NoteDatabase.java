package com.example.mobiledev.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.mobiledev.dao.NoteDao;
import com.example.mobiledev.entities.Note;


@Database(entities = Note.class, version = 2, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase noteDatabase;

    public static synchronized NoteDatabase getDatabase(Context context){
        if(noteDatabase == null){
            noteDatabase = Room.databaseBuilder(
                    context, NoteDatabase.class, "notes_db"
            ).fallbackToDestructiveMigration().build();
        }
        return noteDatabase;
    }

    public abstract NoteDao noteDao();

}

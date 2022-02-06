package com.example.mobiledev.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mobiledev.entities.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes WHERE isDeleted =  :isDel ORDER BY date DESC")
    List<Note> getAllNotesByDate(String isDel);

    @Query("SELECT * FROM notes WHERE isDeleted =:isDel ORDER BY title")
    List<Note> getAllNotesByName(String isDel);

    @Query("SELECT * FROM notes WHERE isDeleted = :isDel ORDER BY date DESC")
    List<Note> getAllDeletedNotes(String isDel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);

    @Delete
    void deleteNote(Note note);

}

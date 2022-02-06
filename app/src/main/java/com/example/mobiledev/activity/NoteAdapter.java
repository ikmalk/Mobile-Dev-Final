package com.example.mobiledev.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobiledev.R;
import com.example.mobiledev.database.NoteDatabase;
import com.example.mobiledev.entities.Note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder>{

    private List<Note> notes;
    private MainActivity mainAct;
    private DeletedNotes delAct;
    private int textSize;
    private int titleSize;
    private boolean isMain;

    public NoteAdapter(List<Note> notes, MainActivity mainAct, boolean isMain){

        this.mainAct = mainAct;
        this.notes = notes;
        this.isMain = isMain;
        setSize(mainAct.getStateTextSize(), 0);

    }

    public NoteAdapter(List<Note> notes, DeletedNotes delAct, boolean isMain){

        this.delAct = delAct;
        this.notes = notes;
        this.isMain = isMain;
        setSize(delAct.getStateTextSize(), 1);

    }

    private void setSize(String textSizeState, int k) {

        if (k == 0) {
            switch (textSizeState.toLowerCase()) {
                case "small":
                    textSize = mainAct.getResources().getInteger(R.integer.text_font_size_small);
                    titleSize = mainAct.getResources().getInteger(R.integer.title_font_size_small);
                    break;
                case "medium":
                    textSize = mainAct.getResources().getInteger(R.integer.text_font_size_medium);
                    titleSize = mainAct.getResources().getInteger(R.integer.title_font_size_medium);
                    break;

                case "large":
                    textSize = mainAct.getResources().getInteger(R.integer.text_font_size_large);
                    titleSize = mainAct.getResources().getInteger(R.integer.title_font_size_large);
                    break;
            }
        } else if (k == 1) {
            switch (textSizeState.toLowerCase()) {
                case "small":
                    textSize = delAct.getResources().getInteger(R.integer.text_font_size_small);
                    titleSize = delAct.getResources().getInteger(R.integer.title_font_size_small);
                    break;
                case "medium":
                    textSize = delAct.getResources().getInteger(R.integer.text_font_size_medium);
                    titleSize = delAct.getResources().getInteger(R.integer.title_font_size_medium);
                    break;

                case "large":
                    textSize = delAct.getResources().getInteger(R.integer.text_font_size_large);
                    titleSize = delAct.getResources().getInteger(R.integer.title_font_size_large);
                    break;
            }

        }
    }



    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(isMain){
            return new NoteHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.note_view,
                            parent,
                            false

                    ), parent.getContext(), mainAct, titleSize, textSize, isMain
            );
        }
        return new NoteHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.note_view,
                        parent,
                        false

                ), parent.getContext(), delAct, titleSize, textSize, isMain
        );

    }





    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        holder.setNote(notes.get(position), position);
//        holder.layoutNote.setOnClickListener(view -> noteListener.onNoteClicked(notes.get(position), position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteHolder extends RecyclerView.ViewHolder{

        private TextView textTitle, textOverview, textDate;
        private AlertDialog deleteView;
        private ImageView deleteImg, imageNote;
        private Context context;
        private View item;
        private MainActivity mainAct;
        private DeletedNotes delAct;
        private LinearLayout layoutNote;
        private LinearLayout layoutNote2;
        private int titleSize;
        private int textSize;
        private boolean isMain;

        public NoteHolder(@NonNull View itemView, Context ctt, MainActivity mAct,
                          int ttSize, int txtSize, boolean main) {
            super(itemView);

            textTitle = (TextView)itemView.findViewById(R.id.noteTextTitle);
            textOverview = (TextView) itemView.findViewById(R.id.noteTextOverview);
            textDate = (TextView) itemView.findViewById(R.id.noteDate);
            deleteImg = (ImageView) itemView.findViewById(R.id.note_delete);
            layoutNote = (LinearLayout) itemView.findViewById(R.id.noteLayout);
            layoutNote2 = (LinearLayout) itemView.findViewById(R.id.noteLayout2);
            imageNote = (ImageView) itemView.findViewById(R.id.noteImageView);

            context = ctt;
            item = itemView;
            mainAct = mAct;
            isMain = main;

            titleSize = ttSize;
            textSize = txtSize;

        }

        public NoteHolder(@NonNull View itemView, Context ctt, DeletedNotes dAct,
                          int ttSize, int txtSize, boolean main) {
            super(itemView);

            textTitle = (TextView)itemView.findViewById(R.id.noteTextTitle);
            textOverview = (TextView) itemView.findViewById(R.id.noteTextOverview);
            textDate = (TextView) itemView.findViewById(R.id.noteDate);
            deleteImg = (ImageView) itemView.findViewById(R.id.note_delete);
            layoutNote = (LinearLayout) itemView.findViewById(R.id.noteLayout);
            layoutNote = (LinearLayout) itemView.findViewById(R.id.noteLayout);
            layoutNote2 = (LinearLayout) itemView.findViewById(R.id.noteLayout2);


            context = ctt;
            item = itemView;
            delAct = dAct;
            isMain = main;

            titleSize = ttSize;
            textSize = txtSize;

        }

        private void showDeletedNoteDialog(Note note, int position){
            if(deleteView == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(
                        R.layout.delete_note_view,
                        (ViewGroup) item.findViewById(R.id.dlv_layout)
                );
                builder.setView(view);
                deleteView = builder.create();
                if(deleteView.getWindow() != null){
                    deleteView.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                view.findViewById(R.id.dlv_delete).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        class DeleteNote extends AsyncTask<Void, Void, Void> {

                            @Override
                            protected Void doInBackground(Void... voids) {

                                if(isMain){
                                    note.setIsDeleted("Yes");
                                    NoteDatabase.getDatabase(context).noteDao().insertNote(note);
                                }
                                else{
                                    NoteDatabase.getDatabase(context).noteDao()
                                        .deleteNote(note);
                                }
                                return null;
                            }

                        }

                        new DeleteNote().execute();


                        deleteView.dismiss();

                        if(isMain){
                            mainAct.setNoteClickedPos(position);
                            mainAct.getNotes(MainActivity.REQUEST_DELETE_NOTE, true);
                        }else{
                            delAct.setNoteClickedPos(position);
                            delAct.getNotes(DeletedNotes.REQUEST_DELETE_NOTE, true);
                        }



                    }
                });

                view.findViewById(R.id.dlv_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteView.dismiss();
                    }
                });
            }

            deleteView.show();
        }

        public void setNote(Note note, int position){
            textTitle.setText(note.getTitle());
            textTitle.setOnClickListener(view -> mainAct.onNoteClicked(note, position));
            textTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleSize);
            textTitle.setTextColor(Color.parseColor(note.getText_color()));
            imageNote = itemView.findViewById(R.id.noteImageView);

            String text = note.getText();

            String overview = "";

            if(text.length() > 13){
                for(int i = 0; i < 13; i++){
                    overview += text.charAt(i);
                }
                overview += "...";
            }else{
                overview = text;
            }
            
//            if(note.getImage_path() !=null) {
//                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImage_path()));
//                imageNote.setVisibility(View.VISIBLE);
//            } else {
//                imageNote.setVisibility(View.GONE);
//            }

            textOverview.setText(overview);
            textOverview.setOnClickListener(view -> mainAct.onNoteClicked(note, position));
            textOverview.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
            textOverview.setTextColor(Color.parseColor(note.getText_color()));

            textDate.setText(note.getDate());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            gradientDrawable.setColor(Color.parseColor(note.getBackground_color()));

            deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeletedNoteDialog(note, position);
                }
            });

//            if(note.getImage_path() != null){
//                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImage_path()));
//                imageNote.setVisibility(View.VISIBLE);
//            } else {
//                imageNote.setVisibility(View.GONE);
//            }

            if(!isMain){
                long diff = -1;

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm a", Locale.ENGLISH);
                    Date firstDate = sdf.parse(note.getDate());
                    Date secondDate = sdf.parse(new SimpleDateFormat("dd MMMM yyyy HH:mm a", Locale.getDefault())
                            .format(new Date()));

//                    Date secondDate = sdf.parse("10 March 2022 15:51 PM");
                    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                    diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                }
                catch (ParseException e) {
                }

                if(diff>30){
                    class DeleteNote extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {


                        NoteDatabase.getDatabase(context).noteDao()
                                .deleteNote(note);

                            return null;
                        }

                    }

                    new DeleteNote().execute();
                    delAct.setNoteClickedPos(position);
                    delAct.getNotes(DeletedNotes.REQUEST_DELETE_NOTE, true);
                }

            }


        }

    }

}

package com.example.mobiledev.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobiledev.database.NoteDatabase;
import com.example.mobiledev.R;
import com.example.mobiledev.entities.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class NoteAppActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteText, fontSize;
    private TextView date;
    private ImageView colorCircle, fontImage, addImage, textColImage, imageNote;
    private CoordinatorLayout screenLayout;
    private ConstraintLayout screenLayout2;

    private Note updateNote;
    private String titleT;
    private String textT;
    private String fnt;
    private String textColor;
    private String screenColor;
    private boolean onCreate;
    private String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_app);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        onCreate = true;
        imagePath = "";

        ImageView imageBack = (ImageView) findViewById(R.id.imageBack);
        imageBack.setOnClickListener((v) -> {onBackPressed();});

        inputNoteTitle = (EditText) findViewById(R.id.inputNoteTitle);
        inputNoteText = (EditText) findViewById(R.id.inputNote);
        date = findViewById(R.id.textDateTime);

        screenLayout = (CoordinatorLayout) findViewById(R.id.note_screen);
        screenLayout2 = (ConstraintLayout) findViewById(R.id.note_screen_const);


        fontSize = (EditText) findViewById(R.id.inputFontSize);
        colorCircle = (ImageView) findViewById(R.id.imageColorChange);
        fontImage = (ImageView) findViewById(R.id.imageFont);
        ImageView imageAddSize = (ImageView) findViewById(R.id.imageAddSizeFont);
        ImageView imageMinusSize = (ImageView) findViewById(R.id.imageMinusSizeFont);
        addImage = (ImageView) findViewById(R.id.imageImage);
        imageNote = findViewById(R.id.noteImageView);

        textColImage = (ImageView) findViewById(R.id.imageTextColor);

        fontSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(!fontSize.getText().toString().equals("")){
                    float size = Float.parseFloat(fontSize.getText().toString());
                    inputNoteText.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

                }else{
                    fontSize.setText("1");
                    inputNoteText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 1);
                }
            }
        });



        imageAddSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fontSize.getText().toString().equals("")){
                    fontSize.setText("1");
                }else{
                    int size = Integer.parseInt(fontSize.getText().toString()) + 1;
                    fontSize.setText(Integer.toString(size));
                }
            }
        });

        imageMinusSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fontSize.getText().toString().equals("")){
                    fontSize.setText("1");
                }else{
                    int size = Integer.parseInt(fontSize.getText().toString()) - 1;
                    fontSize.setText(Integer.toString(size));
                }
            }
        });

        date.setText(
                new SimpleDateFormat("dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );

        ImageView imageSave = (ImageView) findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!getIntent().getBooleanExtra("isUpdate", false)) {
                    saveNote(MainActivity.REQUEST_ADD_NOTE);
                }
                else{
                    saveNote(MainActivity.REQUEST_OVERWRITE_NOTE);
                }

            }
        });

        addImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pickImageFromGallery();
            }
        });

        //////////////////////////////////////////////////////////////////////////

        fnt = "Ubuntu";
        textColor = "#000000";
        screenColor = "#E9F2EB";

        if (getIntent().getBooleanExtra("isUpdate", false)) {
            updateNote = (Note) getIntent().getSerializableExtra("note");
            setUpdateNote();
            fnt = updateNote.getFont();
        }

        ArrayList<String> fontArr = new ArrayList<>();
        fontArr.addAll(Arrays.asList(getResources().getStringArray(R.array.font)));

        fontArr.remove(fontArr.indexOf(fnt));
        fontArr.add(0, fnt);

        Spinner fontSpinner = (Spinner) findViewById(R.id.noteFontSpinner);
        ArrayAdapter<CharSequence> fsAdapter = new ArrayAdapter(
                getBaseContext(),
                R.layout.spinner_layout,
                fontArr

        );

        fontSpinner.setAdapter(fsAdapter);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!onCreate){
                    setFontTypeface(fontArr.get(i), inputNoteText);
                    fnt = fontArr.get(i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        fontImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fontSpinner.setVisibility(Spinner.VISIBLE);
                fontSpinner.performClick();
            }
        });

        /////////////////
        ArrayList<String> col = new ArrayList<>();
        String[] v = getResources().getStringArray(R.array.text_color);
        col.addAll(Arrays.asList(v));

        col.remove(col.indexOf(textColor));
        col.add(0, textColor);

        TextColorAdapter textColorAdapter = new TextColorAdapter(this, R.layout.text_color_layout, col);
        Spinner textColSpinner = (Spinner) findViewById(R.id.textColSpinner);
        textColSpinner.setAdapter(textColorAdapter);

        textColSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                inputNoteText.setTextColor(Color.parseColor(col.get(i)));
                inputNoteTitle.setTextColor(Color.parseColor(col.get(i)));
                textColImage.setColorFilter(Color.parseColor(col.get(i)), PorterDuff.Mode.SRC_ATOP);
                textColor = col.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        textColImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fontSpinner.setVisibility(Spinner.VISIBLE);
                textColSpinner.performClick();
            }
        });

        ///////
        ArrayList<String> scl = new ArrayList<>();
        String[] c = getResources().getStringArray(R.array.screen_color);
        scl.addAll(Arrays.asList(c));

        scl.remove(scl.indexOf(screenColor));
        scl.add(0, screenColor);

        ScreenColorAdapter screenColorAdapter = new ScreenColorAdapter(this, R.layout.screen_color_layout, scl);
        Spinner screenColSpinner = (Spinner) findViewById(R.id.screenColSpinner);
        screenColSpinner.setAdapter(screenColorAdapter);

        screenColSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                screenLayout.setBackgroundColor(Color.parseColor(scl.get(i)));
                screenLayout2.setBackgroundColor(Color.parseColor(scl.get(i)));
                colorCircle.setColorFilter(Color.parseColor(scl.get(i)), PorterDuff.Mode.SRC_ATOP);
                screenColor = scl.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        colorCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                fontSpinner.setVisibility(Spinner.VISIBLE);
                screenColSpinner.performClick();
            }
        });

//////////////////////////////////////////////////////////////////////////////////////////////

        onCreate = false;

    }

    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }


    private void saveImage(Bitmap finalBitmap, Uri uri) {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        if (isStoragePermissionGranted()) {
            File myDir = new File(root + "/saved_images");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            File fn= new File(uri.getPath());

            String fname = fn.getName().replace(':', '_') + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                imagePath = file.getPath();
                System.out.println(imagePath);

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, new String[]{file.getName()}, null);
        }
    }

    private void pickFromGallery(View view) {
        if(isPermissionGranted()){
            pickImageFromGallery();
        } else {
            takePermissions();
        }
    }

    private boolean isPermissionGranted() {
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        } else {
            int readExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return readExternalStorage == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void takePermissions(){
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.R){
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category, DEFAULT");
                intent.setData(Uri.parse(String.format("package%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent,100);
            } catch (Exception exception) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent,100);
            }
        } else {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent,102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 100){
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.R) {
                    if(Environment.isExternalStorageManager()){
                        pickImageFromGallery();
                    } else {
                        takePermissions();
                    }
                }
            } else if (requestCode == 102){
                if(data != null){
                    Uri uri = data.getData();
                    if(uri != null){

                        Uri imgUri = data.getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                            imageNote.setImageBitmap(bitmap);
                            saveImage(bitmap, imgUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            if(requestCode == 101){
                boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if(readExternalStorage){
                    pickImageFromGallery();
                }else{
                    takePermissions();
                }
            }
        }
    }

    private void setFontTypeface(String fnt, EditText et){
        /*
                <item>Alegreya</item>
                <item>Arvo</item>
                <item>IBM Plex Sans</item>
                <item>Lora</item>
                <item>Merriweather Sans</item>
                <item>Nunito</item>
                <item>Roboto</item>
                <item>Roboto Slab</item>
                <item>Rubik</item>
                <item>Space Mono</item>
                <item>Ubuntu</item>
                <item>Vesper Libre</item>
                 */

        Typeface type;
        switch (fnt) {
            case "Alegreya":
                // Alegreya
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.alegreya);
                et.setTypeface(type);
                break;

            case "Arvo":
                // Arvo
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.arvo);
                et.setTypeface(type);
                break;
            case "IBM Plex Sans":
                // IBM Plex Sans
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.ibm_plex_sans);
                et.setTypeface(type);
                break;
            case "Lora":
                // Lora
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.lora);
                et.setTypeface(type);
                break;
            case "Merriweather Sans":
                // Merriweather Sans
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.merriweather_sans);
                et.setTypeface(type);
                break;
            case "Nunito":
                // Nunito
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
                et.setTypeface(type);
                break;
            case "Roboto":
                // Roboto
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.roboto);
                et.setTypeface(type);
                break;
            case "Roboto Slab":
                // Roboto Slab
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.roboto_slab);
                et.setTypeface(type);
                break;
            case "Rubik":
                // Rubik
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.rubik);
                et.setTypeface(type);
                break;
            case "Space Mono":
                // Space Mono
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.space_mono);
                et.setTypeface(type);
                break;
            case "Ubuntu":
                // Ubuntu
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.ubuntu);
                et.setTypeface(type);
                break;
            case "Vesper Libre":
                // Vesper Libre
                type = ResourcesCompat.getFont(getApplicationContext(), R.font.vesper_libre);
                et.setTypeface(type);
                break;
        }

    }


    private void setUpdateNote(){
        inputNoteTitle.setText(updateNote.getTitle());
        inputNoteText.setText(updateNote.getText());
        date.setText(updateNote.getDate());
        fontSize.setText(updateNote.getFont_size()+"");
        textT = updateNote.getText();
        titleT = updateNote.getText();
        textColor = updateNote.getText_color();
        screenColor = updateNote.getBackground_color();

        if(updateNote.getImage_path() != null){
            if(!updateNote.getImage_path().equals("")){
                imagePath = updateNote.getImage_path();
                Uri uri = Uri.parse(new File(imagePath).toString());
                System.out.println(imagePath);

                Bitmap bmp = BitmapFactory.decodeFile(uri.toString());
                imageNote.setImageBitmap(bmp);
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                imageNote.setImageBitmap(bitmap);

            }
        }


    }

    private void saveNote(int requestCode){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setText(inputNoteText.getText().toString());
        note.setDate(date.getText().toString());
        note.setFont_size(Integer.parseInt(fontSize.getText().toString()));
        note.setIsDeleted("No");
        note.setFont(fnt);
        note.setBackground_color(screenColor);
        note.setText_color(textColor);
        note.setImage_path(imagePath);

        if(updateNote != null){
            note.setId(updateNote.getId());

            if(!textT.equals(inputNoteText.getText().toString()) &&
                    !titleT.equals(inputNoteTitle.getText().toString())){
                note.setDate(date.getText().toString());
            }

        }

        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);

                finish();
            }
        }

        new SaveNoteTask().execute();
        openMainActivity(requestCode);

    }

    private void openMainActivity(int req){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("noteapp", req+"");
        startActivity(intent);
    }



}
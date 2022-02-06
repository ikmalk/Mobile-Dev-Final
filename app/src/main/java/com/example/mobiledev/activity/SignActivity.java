package com.example.mobiledev.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobiledev.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SignActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Button homeButton = (Button) findViewById(R.id.HomeBtnSignedIn);
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                openActivityHome();
            }
        });


        Button logoutButton = (Button)findViewById(R.id.LogoutBtn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode", true);

        if(booleanValue){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            SWDarkMode.setChecked(true);
        }

    }

    public void openActivityHome(){
        Intent intent = new Intent(this, com.example.mobiledev.activity.MainActivity.class);
        startActivity(intent);
    }

    public void openActivityLogin() {
        Intent intent = new Intent (this, LoginActivity.class);
        startActivity(intent);
    }
}
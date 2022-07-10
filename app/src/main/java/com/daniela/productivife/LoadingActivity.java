package com.daniela.productivife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadingActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        int time = 3000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                verifyUser();
            }
        }, time);
    }
    private void verifyUser(){ //If user already logged in, go directly to main activity and skip Log in/Sign up process

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(LoadingActivity.this, SignUpActivity.class));
        } else{
            startActivity(new Intent(LoadingActivity.this, MainActivity.class));
        }
        finish();
    }
}

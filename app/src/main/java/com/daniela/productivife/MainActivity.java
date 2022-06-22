package com.daniela.productivife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "MainActivity";
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    //Upper menu (Logout)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:{
                Log.d(TAG, "Logging out");
                onLogoutButton();
                return true;
            }
        }
        return  super.onOptionsItemSelected(item);
    }

    private void onLogoutButton() {
        firebaseAuth.signOut();
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        Toast.makeText(this, "Successfully logged out", Toast.LENGTH_SHORT).show();
    }
}
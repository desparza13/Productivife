package com.daniela.productivife;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUp";
    private Button btnLogin;
    private Button btnSignup;
    private LoginButton btnFacebook;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private String name = "";
    private String email ="";
    private String password = "";
    private String confirmPassword = "";

    private CallbackManager callbackManager;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_sign_up);

        callbackManager = CallbackManager.Factory.create();
        configureToasty();

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        btnFacebook = findViewById(R.id.btnFacebook);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Wait a second please");
        progressDialog.setCanceledOnTouchOutside(false); //User can't close de process dialog when pressing

        btnFacebook = (LoginButton) findViewById(R.id.btnFacebook);
        btnFacebook.setReadPermissions(Arrays.asList(EMAIL));

        // Callback registration
        btnFacebook.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                //Asynchronous
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override //When server answers
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            email = object.getString("email");
                                            name = object.getString("name");
                                            password = object.getString("name");
                                            //encrypt(password);
                                            Log.i(TAG, email);
                                            Log.i(TAG, name);
                                            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");
                                            ref.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener(){
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if(!dataSnapshot.exists()) {
                                                        Log.d(TAG, "Account doesn't exists.\nCreating account");
                                                        createAccount();
                                                    }
                                                    else{
                                                        Log.d(TAG, "Account already exists.\nLogging in");
                                                        loginUser();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                                                }
                                            });

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                        Log.i(TAG, "Access token is: "+isLoggedIn);

                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG,"Cancel");
                        Toasty.warning(SignUpActivity.this, "Log in through facebook cancelled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i(TAG,"Error");
                        Toasty.error(SignUpActivity.this, "Log in through facebook error"+exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkData(){
        name = etName.getText().toString();
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(name)){
            Toasty.warning(this,"Enter a name", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toasty.warning(this, "Enter a valid e-mail", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toasty.warning(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(confirmPassword)){
            Toasty.warning(this, "Confirm your password", Toast.LENGTH_SHORT).show();
        }
        else if (!password.equals(confirmPassword)){
            Toasty.warning(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        }
        else{
            createAccount();
        }
    }

    private void createAccount() {
        /*
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", email);
        editor.putString("password",password);
        editor.apply();
         */
        progressDialog.setMessage("Creating account...");
        progressDialog.show();
        //Create user in Firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //
                        Submit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.error(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void Submit() {
        progressDialog.setMessage("Submiting information");
        progressDialog.dismiss();

        //Get current user id
        String uid = firebaseAuth.getUid();

        HashMap<String,String> data = new HashMap<>();
        data.put("uid", uid);
        data.put("email", email);
        data.put("name", name);
        data.put("password", password);

        //Register in database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(uid)
                .setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toasty.success(SignUpActivity.this, "Account successfully created", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.error(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.commit();
    }

    private void loginUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password",password);
        editor.apply();
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toasty.normal(SignUpActivity.this, "Welcome: "+user.getEmail(), AppCompatResources.getDrawable(SignUpActivity.this,R.drawable.ic_person_white)).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.error(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void configureToasty(){
        Typeface font = ResourcesCompat.getFont(this, R.font.dmsans); //keep style consistent throughout the app
        Toasty.Config.getInstance().setToastTypeface(font)
                .setTextSize(14)
                .allowQueue(true)
                .apply();
    }

    private void encrypt(String password){


    }
}
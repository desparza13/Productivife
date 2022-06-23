package com.daniela.productivife;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomeFragment extends Fragment {
    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    TextView tvUserName;
    TextView tvUserEmail;
    ProgressBar pbUser;

    DatabaseReference Users;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        pbUser = view.findViewById(R.id.pbUser);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        Users = FirebaseDatabase.getInstance().getReference("Users");
    }

    @Override
    public void onStart() {
        checkLogin();
        super.onStart();
    }

    private void checkLogin(){
        if (user!=null){
            loadData();
        } else{
            startActivity(new Intent(getContext(), SignUpActivity.class));
        }
    }
    private void loadData(){
        Users.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //Allows to read database information on realtime
                //If user exists
                if (snapshot.exists()){
                    pbUser.setVisibility(View.GONE);
                    tvUserName.setVisibility(View.VISIBLE);
                    tvUserEmail.setVisibility(View.VISIBLE);

                    String name = "" + snapshot.child("name").getValue();
                    String email = "" + snapshot.child("email").getValue();

                    tvUserName.setText(name);
                    tvUserEmail.setText(email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Couldn't get user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
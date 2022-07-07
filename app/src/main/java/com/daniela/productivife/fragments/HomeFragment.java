package com.daniela.productivife.fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daniela.productivife.AddItemActivity;
import com.daniela.productivife.R;
import com.daniela.productivife.ShowListActivity;
import com.daniela.productivife.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;


public class HomeFragment extends Fragment {
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;

    private TextView tvUid;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private ProgressBar pbUser;
    private Button btnAdd;
    private Button btnShowList;

    private DatabaseReference Users;

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

        tvUid = view.findViewById(R.id.tvUid);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        pbUser = view.findViewById(R.id.pbUser);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnShowList = view.findViewById(R.id.btnShowList);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        Users = FirebaseDatabase.getInstance().getReference("Users");

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUid = tvUid.getText().toString();
                String userEmail = tvUserEmail.getText().toString();
                Intent intent = new Intent(getContext(), AddItemActivity.class);
                intent.putExtra("uid", userUid);
                intent.putExtra("email", userEmail);
                startActivity(intent);
            }
        });
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ShowListActivity.class));
            }
        });
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
                    tvUid.setVisibility(View.VISIBLE);
                    tvUserName.setVisibility(View.VISIBLE);
                    tvUserEmail.setVisibility(View.VISIBLE);

                    String uid = "" + snapshot.child("uid").getValue();
                    String name = "" + snapshot.child("name").getValue();
                    String email = "" + snapshot.child("email").getValue();

                    tvUid.setText(uid);
                    tvUserName.setText(name);
                    tvUserEmail.setText(email);

                    btnAdd.setEnabled(true);
                    btnShowList.setEnabled(true);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toasty.error(getContext(),"Couldn't get user info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.hbtu.cycleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvRollNo, tvSession, tvBranch, tvEmail;
    String name, rollNo, session, branch, email;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference UserRef;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().hide();

        tvName = findViewById(R.id.tvName);
        tvRollNo = findViewById(R.id.tvRollNo);
        tvSession = findViewById(R.id.tvYear);
        tvBranch = findViewById(R.id.tvBranch);
        tvEmail = findViewById(R.id.tvEmail);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!= null){
            currentUserId = currentUser.getUid();
            UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

            UserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){

                        name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        rollNo = Objects.requireNonNull(snapshot.child("roll_No").getValue()).toString();
                        session = Objects.requireNonNull(snapshot.child("session").getValue()).toString();
                        branch = Objects.requireNonNull(snapshot.child("branch").getValue()).toString();
                        email = Objects.requireNonNull(snapshot.child("email").getValue()).toString();

                        tvName.setText(name);
                        tvRollNo.setText(String.format("Roll-No: %s", rollNo));
                        tvSession.setText(String.format("Session: %s", session));
                        tvBranch.setText(branch);
                        tvEmail.setText(email);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
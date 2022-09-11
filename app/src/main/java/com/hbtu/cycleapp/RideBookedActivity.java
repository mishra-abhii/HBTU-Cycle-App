package com.hbtu.cycleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class RideBookedActivity extends AppCompatActivity {

    private TextView bookedCycle;
    private Button endRideButton;
    private ProgressDialog dialog;
    private DatabaseReference cycleRef;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_booked);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUID = currentUser!= null ? currentUser.getUid() : "-1";

        cycleRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUID);

        bookedCycle = findViewById(R.id.cycleNumber);
        endRideButton = findViewById(R.id.endRideButton);


        final String[] cycleNumber = new String[1];
        cycleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cycleNumber[0] = Objects.requireNonNull(snapshot.child("cycleTaken").getValue()).toString();
                    bookedCycle.setText("Cycle Number: "+ cycleNumber[0]);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        endRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RideBookedActivity.this, "Ask Guard to Scan QR to return Cycle", Toast.LENGTH_SHORT).show();
                dialog = new ProgressDialog(RideBookedActivity.this);
                dialog.setMessage("Waiting for Guard to scan QR on cycle...");
                dialog.setTitle("Ending Ride");
                dialog.show();
                //Set it to false. And take a times. If in 1 min QR not scanned then this will vanish.
                dialog.setCancelable(false);
            }
        });
    }
}
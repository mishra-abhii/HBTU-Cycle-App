package com.hbtu.cycleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RideBookedActivity extends AppCompatActivity {

    private TextView bookedCycle;
    private Button endRideButton;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_booked);

        String cycleNumber = getIntent().getStringExtra("cycle");
        bookedCycle = findViewById(R.id.cycleNumber);
        endRideButton = findViewById(R.id.endRideButton);

        bookedCycle.setText("Cycle Number: "+cycleNumber);


        endRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RideBookedActivity.this, "Ask Guard to Scan QR to return Cycle", Toast.LENGTH_SHORT).show();
                dialog = new ProgressDialog(RideBookedActivity.this);
                dialog.setMessage("Waiting for Guard to scan QR on cycle...");
                dialog.setTitle("Ending Ride");
                dialog.show();
                //Set it to false. And take a times. If in 1 min QR not scanned then this will vanish.
                dialog.setCancelable(true);
            }
        });
    }
}
package com.hbtu.cycleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.hbtu.cycleapp.auth.GoogleSignInActivity;
import com.hbtu.cycleapp.model.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    GoogleSignInClient gsc;
    private CodeScanner codeScanner;
    private final int CAMERA_REQUEST_CODE = 101;
    FirebaseDatabase db;
    DatabaseReference ref;
    boolean booked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To blend Actionbar with layout. Removing its elevation shadow.
        getSupportActionBar().setElevation(0);
        setupPermissions();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUID = currentUser != null ? currentUser.getUid() : "-1";
        booked = false;

        db = FirebaseDatabase.getInstance();
        ref = db.getReference().child("Users");

        final String[] cycleNumberForCurrentUsr = new String[1];
        //if cycleTaken != -1 this activity should not show up
        ref.child(currentUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cycleNumberForCurrentUsr[0] = Objects.requireNonNull(snapshot.child("cycleTaken").getValue()).toString();

                    if (!cycleNumberForCurrentUsr[0].equals("-1")) {
                        Intent intent = new Intent(MainActivity.this, RideBookedActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        CodeScannerView codeScannerView = findViewById(R.id.codescanner);
        codeScanner = new CodeScanner(this, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            int scannedCycleNum = Integer.parseInt(result.getText());
                            if (scannedCycleNum > 0 && scannedCycleNum <= 100) {

    //                        This is done to check if the cycle number has already been booked
                                Query q = ref.orderByChild("cycleTaken").equalTo(result.getText());
                                q.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                                            if (datasnapshot.exists()) {
                                                //cycle number already exists in DB. i.e. cycle already taken by someone
                                                booked = true;
                                            }
                                        }

                                        if (booked) {
                                            Toast.makeText(MainActivity.this, "Cycle Already booked. Please Scan again!!", Toast.LENGTH_LONG).show();
                                            booked = !booked;
                                        } else {
                                            ref.child(currentUID).child("cycleTaken").setValue(result.getText());

                                            Intent intent = new Intent(MainActivity.this, RideBookedActivity.class);
    //                                    intent.putExtra("cycle", result.getText());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        } catch (NumberFormatException e) {
//                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Invalid QR", Toast.LENGTH_LONG).show();
                        }
                    }

                });
            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.credit) {
            Toast.makeText(this, "Credits to be given", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.logout) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            gsc = GoogleSignIn.getClient(MainActivity.this, gso);
            currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mAuth.signOut();
                            Intent intent = new Intent(MainActivity.this, GoogleSignInActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }
        return true;
    }

    //Done for dynamic permission for API level > 23
    private void setupPermissions() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED)
            makeRequest();
    }

    private void makeRequest() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    //This will give us result of above makeRequest, that is when we made request for a permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults == null || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Give CAM permissions....", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                //successful
            }
        }
    }
}
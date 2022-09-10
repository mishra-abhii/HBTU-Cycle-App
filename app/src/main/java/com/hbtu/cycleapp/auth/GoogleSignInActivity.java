package com.hbtu.cycleapp.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbtu.cycleapp.MainActivity;
import com.hbtu.cycleapp.R;
import com.hbtu.cycleapp.model.User;

import java.util.Objects;

public class GoogleSignInActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 100;
    GoogleSignInClient gsc;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    String name, email, branch, roll_no, year;

    Button btnSignIn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        mAuth = FirebaseAuth.getInstance();
        btnSignIn = findViewById(R.id.btnLogin);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(GoogleSignInActivity.this);
                progressDialog.setMessage("Google Sign In..");
                progressDialog.show();

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                gsc = GoogleSignIn.getClient(GoogleSignInActivity.this, gso);
                gsc.revokeAccess();

                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    email = account.getEmail();
                    assert email != null;
                    if (validateEmail(email)) {
                        firebaseAuthWithGoogle(account.getIdToken());
                    } else {
                        progressDialog.dismiss();
                        gsc.signOut();
                        Toast.makeText(GoogleSignInActivity.this, "Please use your college mail-id", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (ApiException e) {
                progressDialog.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            updateUserInfo();

                            Intent intent = new Intent(GoogleSignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(GoogleSignInActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    private void updateUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserId;

        if(currentUser!=null){
            currentUserId = currentUser.getUid();
            mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            if(account!=null){

                name = account.getGivenName();
                email = account.getEmail();

                assert email != null;
                String branchCode = email.substring(4, 6);
                if(branchCode.equals("01")){
                    branch = "Bio-Chemical Engineering";
                }
                if(branchCode.equals("02")){
                    branch = "Civil Engineering";
                }
                if(branchCode.equals("03")){
                    branch = "Chemical Engineering";
                }
                if(branchCode.equals("04")){
                    branch = "Computer Science Engineering";
                }
                if(branchCode.equals("05")){
                    branch = "Electrical Engineering";
                }
                if(branchCode.equals("06")){
                    branch = "Electronics Engineering";
                }
                if(branchCode.equals("07")){
                    branch = "Food Technology";
                }
                if(branchCode.equals("08")){
                    branch = "Information Technology";
                }
                if(branchCode.equals("09")){
                    branch = "Leather Technology";
                }
                if(branchCode.equals("10")){
                    branch = "Mechanical Engineering";
                }
                if(branchCode.equals("11")){
                    branch = "Plastic Technology";
                }
                if(branchCode.equals("12")){
                    branch = "Oil Technology";
                }
                if(branchCode.equals("13")){
                    branch = "Paint Technology";
                }

                roll_no = email.substring(0, 9);

                String yearCode = email.substring(0, 2);
                int yearCode1 = Integer.parseInt(yearCode)+4;
                year = ("20"+yearCode + "-" + "20"+yearCode1);

                User user = new User(name, email, branch, roll_no, year);
                mRef.setValue(user);
            }
        }
    }

    public boolean validateEmail(String email) {
        boolean endsWithDomain = email.endsWith("hbtu.ac.in");
        return endsWithDomain && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
package com.example.demo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_project.databinding.ActivityAllSettingsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class allSettingsActivity extends AppCompatActivity {
    //  ActivityAllSettingsBinding binding;
    ImageView backArrow;
    TextView settingsactivity_profile, settingsactivity_passwordreset, settingsactivity_about, settingsactivity_signout, settingsactivity_delete;
    private FirebaseAuth mauth;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_settings);
        getSupportActionBar().hide();
        mauth = FirebaseAuth.getInstance();
        backArrow = (ImageView) findViewById(R.id.settings_backarrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(allSettingsActivity.this, HomeActivity.class));
            }
        });
        //for profile
        settingsactivity_profile = (TextView) findViewById(R.id.settingsactivity_profile);
        settingsactivity_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(allSettingsActivity.this, SettingsActivity.class));
                Toast.makeText(allSettingsActivity.this, "profile", Toast.LENGTH_SHORT).show();
            }
        });
        //for password
        settingsactivity_passwordreset = (TextView) findViewById(R.id.settingsactivity_passwordreset);
        settingsactivity_passwordreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(allSettingsActivity.this, ResetPasswordActivity.class));
            }
        });
        //for about
        settingsactivity_about = (TextView) findViewById(R.id.settingsactivity_about);
        settingsactivity_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(allSettingsActivity.this, AboutActivity.class));
            }
        });

        //for logout
        settingsactivity_signout = (TextView) findViewById(R.id.settingsactivity_signout);
        settingsactivity_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mauth.signOut();
                startActivity(new Intent(allSettingsActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
                Toast.makeText(allSettingsActivity.this, "User Logged Out", Toast.LENGTH_SHORT).show();
            }
        });
        //for Delete Account
        settingsactivity_delete = (TextView) findViewById(R.id.settingsactivity_delete);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        settingsactivity_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(allSettingsActivity.this);
                dialog.setTitle("Are You Sure");
                dialog.setMessage("Deleting Account Will Delete All the details...");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()){
                                    Toast.makeText(allSettingsActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(allSettingsActivity.this,signinAvtivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }else {
                                    Toast.makeText(allSettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                });
                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog =dialog.create();
            }
        });
    }//on create
}
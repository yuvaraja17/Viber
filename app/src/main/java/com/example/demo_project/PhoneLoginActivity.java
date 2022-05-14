package com.example.demo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    EditText phoneactivity_phonum,phoneactivity_verificationcode;
    Button phoneactivity_sendbtn,phoneactivity_verifybtn;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    FirebaseAuth mAuth;
    ProgressDialog loadingbar;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        Initialize();
        getSupportActionBar().hide();
        phoneactivity_sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phonenumber=phoneactivity_phonum.getText().toString();
                if (TextUtils.isEmpty(phonenumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Enter the phone number", Toast.LENGTH_SHORT).show();
                }else {
                    phoneactivity_verificationcode.setEnabled(true);
                    phoneactivity_verifybtn.setEnabled(true);
                    loadingbar.setTitle("Phone verification");
                    loadingbar.setMessage("please wait for authentication");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phonenumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(PhoneLoginActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone number....", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                loadingbar.dismiss();
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(PhoneLoginActivity.this, "Verification code has been sent to the user", Toast.LENGTH_SHORT).show();
            }

        };
        phoneactivity_verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationcode=phoneactivity_verificationcode.getText().toString();
                if (TextUtils.isEmpty(verificationcode)){
                    Toast.makeText(PhoneLoginActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
                }else {
                    loadingbar.setTitle("verification code");
                    loadingbar.setMessage("please wait for verification");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }//on create

    private void Initialize() {
        phoneactivity_phonum=(EditText)findViewById(R.id.phoneactivity_phonum);
        phoneactivity_verificationcode=(EditText)findViewById(R.id.phoneactivity_verificationcode);
        phoneactivity_sendbtn=(Button)findViewById(R.id.phoneactivity_sendbtn);
        phoneactivity_verifybtn=(Button)findViewById(R.id.phoneactivity_verifybtn);
        mAuth=FirebaseAuth.getInstance();
        loadingbar=new ProgressDialog(this);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingbar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(PhoneLoginActivity.this,HomeActivity.class));
                            // Update UI
                        } else {
                            String message=task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "error"+message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }
}
package com.example.demo_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class signinAvtivity extends AppCompatActivity {

    EditText loginactivity_Emailid,loginactivity_Password;
    TextView loginactivity_SignupText,loginactivity_forgotPassword,loginactivity_Phonelogin;
    Button loginactivity_signinbtn;
    Button loginActivity_googlebt,loginactivity_facebookbtn;
    FirebaseAuth auth;
    //com.example.demo_project.databinding.ActivityLoginBinding binding;
    GoogleSignInClient mGoogleSignInClient;
    // int RC_SIGN_IN=0;
    DatabaseReference Rootref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_avtivity);
        loginactivity_SignupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendusertoRegisteractivity();
            }
        });
        loginactivity_signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowLogin();
            }
        });
        //if user is not null means the user is logged in
        if (auth.getCurrentUser()!=null){
            startActivity(new Intent(signinAvtivity.this,MainActivity.class));
        }
        //for reset password
        loginactivity_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signinAvtivity.this,ResetPasswordActivity.class));
            }
        });
        //for login with phone
        loginactivity_Phonelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   startActivity(new Intent(signinAvtivity.this,PhoneLoginActivity.class));
            }
        });

    }//end oncreate

    //to redirect to signupactivity
    private void sendusertoRegisteractivity() {
        Intent registerintent = new Intent(signinAvtivity.this,SignUpActivity.class);
        startActivity(registerintent);
    }
    //store the data in firebase
    private void AllowLogin() {
        if (loginactivity_Emailid.getText().toString().isEmpty()){
            loginactivity_Emailid.setError("email is required");
            if (loginactivity_Password.getText().toString().isEmpty()){
                loginactivity_Password.setError("Password is empty");
            }
        }else {
            // progressDialog.show();
            auth.signInWithEmailAndPassword(loginactivity_Emailid.getText().toString(), loginactivity_Password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                // sendusertoMainactivity();
                                startActivity(new Intent(signinAvtivity.this,HomeActivity.class));
                            } else {
                                Toast.makeText(signinAvtivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }//end sendusertoRegisteractivity

    // must to initialize the ids
    private void Initialize() {
        loginactivity_Emailid =(EditText)findViewById(R.id.loginactivity_Emailid);
        loginactivity_Password =(EditText)findViewById(R.id.loginactivity_Password);
        loginactivity_SignupText=(TextView)findViewById(R.id.loginactivity_newuserTxt);
        loginactivity_forgotPassword=(TextView)findViewById(R.id.loginactivity_forgotPassword);
        loginactivity_signinbtn=(Button)findViewById(R.id.loginactivity_Signinbtn);
        loginactivity_Phonelogin=(TextView)findViewById(R.id.loginactivity_Phonelogin);
        auth=FirebaseAuth.getInstance();
    }//initialize
}//end LoginActivity
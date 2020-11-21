package com.android.patient_portal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    EditText phoneNumber;
    ProgressBar pd;
    TextView submit,signUp;
    private CardView view;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        phoneNumber =  findViewById(R.id.et_reg_mob);

        submit =  findViewById(R.id.tv_reg_submit);
        signUp=findViewById(R.id.sign_up);
        view = findViewById(R.id.rl_main_view);
         pd=findViewById(R.id.pd);

        //signup
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        //signin
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean check = validateCredentials();
                if(check)
                {
                    final String phone = phoneNumber.getText().toString();
                    Intent intent=new Intent(LoginActivity.this, ValidateOtp.class);
                    intent.putExtra("phone",phone);
                    startActivity(intent);
                }

            }
        });


    }

    private Boolean validateCredentials() {
        if (!isNetworkAvailable()) {
            Snackbar.make(view, "Check your Internet Connection", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (phoneNumber.getText().toString().equals("")) {
            phoneNumber.setError("Enter a Phone Number");
            return false;
        }
        if (phoneNumber.getText().toString().length()<10) {
            phoneNumber.setError("Enter a valid Phone Number");
            return false;
        }


        return true;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
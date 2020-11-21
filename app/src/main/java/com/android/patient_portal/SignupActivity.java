package com.android.patient_portal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

public class SignupActivity extends AppCompatActivity {
    private EditText name, address,medical_history, mobile,gender,email,age;
    TextView login;
    private CardView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        name = (EditText) findViewById(R.id.et_reg_name);
        address = (EditText) findViewById(R.id.et_reg_email);
        mobile = (EditText) findViewById(R.id.et_reg_city);
        gender = (EditText) findViewById(R.id.et_reg_mob);

        age=(EditText)findViewById(R.id.et_reg_blood_group);
        medical_history=(EditText)findViewById(R.id.et_reg_patient_age);
        login=findViewById(R.id.login);
        view = findViewById(R.id.rl_main_view);

        ImageView imageView = (ImageView)findViewById(R.id.doctor_image);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this,LoginActivity.class));
                finish();
            }
        });

        TextView submit = (TextView) findViewById(R.id.tv_reg_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean checker = validateCredentials();
                if (checker) {

                            String name_text=name.getText().toString();
                            String medical_text =medical_history.getText().toString();
                            String phone_text=mobile.getText().toString();
                            String gender_text=gender.getText().toString();
                            String age_text=age.getText().toString();
                            String address_text=address.getText().toString();
                            //MDToast.makeText(PatientRegisterActivity.this, "Done!", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
                    Intent intent=new Intent(SignupActivity.this, ValidateOtpRegister.class);
                    intent.putExtra("name",name_text);

                    intent.putExtra("medical_history",medical_text);
                    intent.putExtra("phone",phone_text);
                    intent.putExtra("gender",gender_text);
                    intent.putExtra("age",age_text);
                    intent.putExtra("address",address_text);
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
        if (name.getText().toString().equals("")) {
            name.setError("Enter a User Name");
            return false;
        }


        if (address.getText().toString().equals("")) {
            address.setError("Please enter address");
            return false;
        }
        if (mobile.getText().toString().equals("")) {
            mobile.setError("Please enter Phone number");
            return false;
        }
        if (medical_history.getText().toString().equals("")) {
            medical_history.setError("Enter Medical History");
            return false;
        }

        if (gender.getText().toString().equals("")) {
            gender.setError("Enter a Gender");
            return false;
        }
        if (!Patterns.PHONE.matcher(mobile.getText().toString()).matches()) {
            mobile.setError("Enter a valid Phone Number");
            return false;
        }

        if (age.getText().toString().equals("")) {
            age.setError("Enter a Age");
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
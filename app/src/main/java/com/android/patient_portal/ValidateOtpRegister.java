package com.android.patient_portal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ValidateOtpRegister extends AppCompatActivity {
    String name, address, medical, mobil,gender,email,ag;

    EditText one,two,three,four,five,six;
    SharedPreferences sharedpreferences;

    ProgressBar pd;
    TextView verify_btn,resend;
    String  verificationCodeBySystem;
    private FirebaseAuth mAuth;
    Long mobile,age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_otp_register);
        Bundle extras = getIntent().getExtras();
        mobil=getIntent().getStringExtra("phone");
        mobile=Long.parseLong(mobil);



        address=getIntent().getStringExtra("address");
        name=getIntent().getStringExtra("name");

        ag=getIntent().getStringExtra("age");
        age=Long.parseLong(ag);
        medical=getIntent().getStringExtra("medical_history");
        gender=getIntent().getStringExtra("gender");

        verify_btn=findViewById(R.id.buttonverify);
        mAuth = FirebaseAuth.getInstance();
        sharedpreferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);

        one=findViewById(R.id.code1);
        two=findViewById(R.id.code2);
        three=findViewById(R.id.code3);
        four=findViewById(R.id.code4);
        five=findViewById(R.id.code5);
        six=findViewById(R.id.code6);
        TextView show=findViewById(R.id.shownumber);
        show.setText("+91- "+mobile);
        setup();
        sendVerificationCodeToUser(mobil);

        pd=findViewById(R.id.pd);
        resend=findViewById(R.id.resendotp);

        verify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(one.getText().toString().trim().isEmpty() || two.getText().toString().trim().isEmpty() || three.getText().toString().trim().isEmpty() || four.getText().toString().trim().isEmpty() || five.getText().toString().trim().isEmpty() || six.getText().toString().trim().isEmpty()   )
                {
                    Toast.makeText(ValidateOtpRegister.this,"Please enter valid OTP!!",Toast.LENGTH_SHORT).show();
                    return;
                }
                String code=one.getText().toString()+two.getText().toString()+three.getText().toString()+four.getText().toString()+five.getText().toString()+six.getText().toString();
                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(ValidateOtpRegister.this, "Please enter valid OTP!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.setVisibility(View.VISIBLE);
                verifyCode(code);
            }




        });

        //resend
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCodeToUser(mobil);

            }
        });


    }
    private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+mobile)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }private void verifyCode(String codeByUser) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);
        signInTheUserByCredentials(credential);

    }

    private void signInTheUserByCredentials(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(ValidateOtpRegister.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            Toast.makeText(ValidateOtpRegister.this, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show();
                            //token work
                            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUser.getIdToken(true)
                                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                                            if (task.isSuccessful()) {
                                                String idToken = task.getResult().getToken();
                                                Toast.makeText(ValidateOtpRegister.this, "token aya"+idToken, Toast.LENGTH_SHORT).show();
                                                try {
                                                    requestWithSomeHttpHeaders(idToken);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                Toast.makeText(ValidateOtpRegister.this, "token nhi gya", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });



                            //Perform Your required action here to either let the user sign In or do something required
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(ValidateOtpRegister.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    verificationCodeBySystem = s;

                }

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    String code = phoneAuthCredential.getSmsCode();
                    if (code != null) {
                        pd.setVisibility(View.VISIBLE);
                        verify_btn.setVisibility(View.INVISIBLE);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(ValidateOtpRegister.this,"Please enter valid OTP!!", Toast.LENGTH_SHORT).show();
                    pd.setVisibility(View.GONE);
                }
            };



    public void requestWithSomeHttpHeaders(String token) throws JSONException {
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("name", name);
        jsonBody.put("contactNumber",mobile );
        jsonBody.put("age",age );
        jsonBody.put("gender",gender);
        jsonBody.put("address",address);
        jsonBody.put("medicalHistory",medical);


        final String mRequestBody = jsonBody.toString();

        StringRequest request = new StringRequest(Request.Method.POST, "https://35a5ca6d1c57.ngrok.io/api/patient/signup", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Log.e("Your Array Response", response);
                    Toast.makeText(getApplicationContext(),"response aya ",Toast.LENGTH_LONG).show();
                    //parse
//                    try {
//                        JSONObject reader = new JSONObject(response);
//                        String getToken= (String) reader.get("token");
//                        Toast.makeText(getApplicationContext(),"response aya "+getToken,Toast.LENGTH_LONG).show();
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
//
//                        editor.putString("getToken", getToken);
//                        editor.commit();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }


                } else {
                    Log.e("Your Array Response", "Data Null");
                    Toast.makeText(getApplicationContext(),"response nhi aya",Toast.LENGTH_LONG).show();

                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);
                Toast.makeText(getApplicationContext(),"tati",Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization","Bearer" + " " + token );
//                params.put("Accept","application/json");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void setup()
    {
        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    two.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    three.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    four.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    five.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty())
                {
                    six.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}
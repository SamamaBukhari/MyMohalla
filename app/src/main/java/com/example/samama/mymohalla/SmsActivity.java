package com.example.samama.mymohalla;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.reqica.drilon.androidpermissionchecklibrary.CheckPermission;
import com.sinch.verification.CodeInterceptionException;
import com.sinch.verification.Config;
import com.sinch.verification.IncorrectCodeException;
import com.sinch.verification.InitiationResult;
import com.sinch.verification.InvalidInputException;
import com.sinch.verification.PhoneNumberUtils;
import com.sinch.verification.ServiceErrorException;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

public class SmsActivity extends AppCompatActivity {

    EditText editText;
    CheckPermission checkPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        editText = (EditText) findViewById(R.id.number);
        checkPermission = new CheckPermission(this);

    }

    public void verifyOnClick(View view) {
        Config config = SinchVerification.config().applicationKey(getString(R.string.sinchKey)).context(getApplicationContext()).build();
        VerificationListener listener = new VerificationListener() {

            @Override
            public void onInitiated(InitiationResult initiationResult) {

            }

            @Override
            public void onInitiationFailed(Exception e) {
                if (e instanceof InvalidInputException) {
                    // Incorrect number provided
                } else if (e instanceof ServiceErrorException) {
                    // Sinch service error
                } else {
                    // Other system error, such as UnknownHostException in case of network error
                }
            }
            @Override
            public void onVerified() {
                editText.setText("VERIFIED");
            }
            @Override
            public void onVerificationFailed(Exception e) {
                if (e instanceof InvalidInputException) {
                    // Incorrect number or code provided
                } else if (e instanceof CodeInterceptionException) {
                    // Intercepting the verification code automatically failed, input the code manually with verify()
                } else if (e instanceof IncorrectCodeException) {
                    // The verification code provided was incorrect
                } else if (e instanceof ServiceErrorException) {
                    // Sinch service error
                } else {
                    // Other system error, such as UnknownHostException in case of network error
                }
            }
        };
        String defaultRegion = PhoneNumberUtils.getDefaultCountryIso(getApplicationContext());
        String phoneNumberInE164 = PhoneNumberUtils.formatNumberToE164("+" + editText.getText().toString(), defaultRegion);
        Verification verification = SinchVerification.createSmsVerification(config, phoneNumberInE164, listener);
        verification.initiate();
    }

    public void smsOnClick(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission("android.permission.SEND_SMS")
                    != PackageManager.PERMISSION_GRANTED) {
                checkPermission.checkOne("android.permission.SEND_SMS", null);
            }
        }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("+" + editText.getText().toString(), null, "Test message sent from MyMohalla.", null, null);
            Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();

    }
}

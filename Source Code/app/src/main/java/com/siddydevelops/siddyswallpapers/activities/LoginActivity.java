package com.siddydevelops.siddyswallpapers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.siddydevelops.siddyswallpapers.R;

public class LoginActivity extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText number;
    Button next;

    ImageView appLoginLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appLoginLogo = findViewById(R.id.appLogin);
        appLoginLogo.setClipToOutline(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        countryCodePicker = findViewById(R.id.ccp);
        number = findViewById(R.id.editText_carrierNumber);
        next = findViewById(R.id.next);

        countryCodePicker.registerCarrierNumberEditText(number);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(number.getText().toString()))
                {
                    Toast.makeText(LoginActivity.this, "Please enter your phone number.", Toast.LENGTH_SHORT).show();
                }
                else if(number.getText().toString().replace(" ","").length() != 10)
                {
                    Toast.makeText(LoginActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                    intent.putExtra("number", countryCodePicker.getFullNumberWithPlus()
                    .replace(" ",""));
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
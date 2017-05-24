package com.group9.carcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.util.Random;


public class SettingsActivity extends AppCompatActivity {
    private EditText password, confirmPassword;
    private Button savePW, turnOffPW, randomPassword, btnChar, btnChar2;
    private ToggleButton letters, numbers;
    private Context mCtx;

    private int numberofChars = 4;
    boolean numbersOn;
    boolean lettersOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mCtx = getApplicationContext();

        password = (EditText) findViewById(R.id.passwordID);
        confirmPassword = (EditText) findViewById(R.id.confirmPasswordID);
        savePW = (Button) findViewById(R.id.savePWID);
        turnOffPW = (Button) findViewById(R.id.turnOffPWID);

        randomPassword = (Button) findViewById(R.id.randomPasswordID);
        btnChar = (Button) findViewById(R.id.charID);
        btnChar2 = (Button) findViewById(R.id.charID2);
        letters = (ToggleButton) findViewById(R.id.lettersID);
        numbers = (ToggleButton) findViewById(R.id.numbersID);

        savePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String pwd = password.getText().toString().trim();
                String cpwd = confirmPassword.getText().toString().trim();


                if(pwd.length()==0) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_LONG).show();

                }else if (cpwd.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter confirm password", Toast.LENGTH_LONG).show();


                }else if(!pwd.equals(cpwd)) {
                    Toast.makeText(getApplicationContext(), "Password and confirm password are not equal", Toast.LENGTH_LONG).show();


                }else {

                    SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(mCtx);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(AppUtils.KEY_PWD, pwd);

                    edit.commit();
                    finish();
                    Toast.makeText(getApplicationContext(), "Password added", Toast.LENGTH_LONG).show();

                }
            }
        });

        turnOffPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String pwd = "";

                    SharedPreferences pref= PreferenceManager.getDefaultSharedPreferences(mCtx);
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(AppUtils.KEY_PWD, pwd);

                    edit.commit();
                    finish();
                Toast.makeText(getApplicationContext(), "Password removed", Toast.LENGTH_LONG).show();

                }
        });


        numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numbersOn) {
                    numbersOn = false;
                } else {
                    numbersOn = true;
                }
            }
        });

        letters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lettersOn) {

                    lettersOn = false;
                } else {
                    lettersOn = true;
                }
            }
        });


        btnChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                numberofChars++;
                btnChar.setText("+Chars: " + numberofChars);
                btnChar2.setText("-Chars: " + numberofChars);


                if (numberofChars <= 1) {
                    btnChar2.setClickable(false);
                } else{
                    btnChar2.setClickable(true);
                }

                if (numberofChars == 20) {
                    btnChar.setClickable(false);
                } else{
                    btnChar.setClickable(true);
                }

            }
        });



        btnChar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                numberofChars--;
                btnChar2.setText("-Chars: " + numberofChars);
                btnChar.setText("+Chars: " + numberofChars);

                if (numberofChars <= 1) {
                    btnChar2.setClickable(false);
                } else{
                    btnChar2.setClickable(true);
                }

                if (numberofChars == 20) {
                    btnChar.setClickable(false);
                } else{
                    btnChar.setClickable(true);
                }
            }
        });


        randomPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {StringBuilder randomString2 = new StringBuilder();

                if (numbersOn == true && lettersOn == true) {
                   String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

                    Random rnd = new Random();
                    while (randomString2.length() < numberofChars) { // length of the random string.
                        int index = (int) (rnd.nextFloat() * randomString.length());
                        randomString2.append(randomString.charAt(index));
                    }
                }

                if (numbersOn == false && lettersOn == true) {
                    String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                    Random rnd = new Random();
                    while (randomString2.length() < numberofChars) { // length of the random string.
                        int index = (int) (rnd.nextFloat() * randomString.length());
                        randomString2.append(randomString.charAt(index));
                    }
                }
                if (numbersOn == true && lettersOn == false) {
                    String randomString = "1234567890";


                    Random rnd = new Random();
                    while (randomString2.length() < numberofChars) { // length of the random string.
                        int index = (int) (rnd.nextFloat() * randomString.length());
                        randomString2.append(randomString.charAt(index));
                    }
                }
                if (numbersOn == false && lettersOn == false) {
                    Toast.makeText(getApplicationContext(), "Select letters, numbers or both", Toast.LENGTH_LONG).show();
                }

                password.setText(randomString2);

            }
        });



    }



}

package com.group9.carcontroller;

/**
 * This class contains the settings
 *
 * @author Isak Magnusson
 */

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

        // Connect EditText, Button and ToggleButton to the GUI
        password = (EditText) findViewById(R.id.passwordID);
        confirmPassword = (EditText) findViewById(R.id.confirmPasswordID);
        savePW = (Button) findViewById(R.id.savePWID);
        turnOffPW = (Button) findViewById(R.id.turnOffPWID);

        randomPassword = (Button) findViewById(R.id.randomPasswordID);
        btnChar = (Button) findViewById(R.id.charID);
        btnChar2 = (Button) findViewById(R.id.charID2);
        letters = (ToggleButton) findViewById(R.id.lettersID);
        numbers = (ToggleButton) findViewById(R.id.numbersID);

        /*
         * This method saves the password the user entered
         */

        savePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String pwd = password.getText().toString().trim();
                String cpwd = confirmPassword.getText().toString().trim();


                /*
                * If the password contains no characters,
                * display a toast and don't let the user
                * progress
                */
                if(pwd.length()==0) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_LONG).show();


                /*
                * If the confirm password contains no characters,
                * display a toast and don't let the user
                * progress
                */

                }else if (cpwd.length()==0){
                    Toast.makeText(getApplicationContext(), "Enter confirm password", Toast.LENGTH_LONG).show();

                /*
                * If the password is not equal to the confirm password,
                * display a toast and don't let the user
                * progress
                */

                }else if(!pwd.equals(cpwd)) {
                    Toast.makeText(getApplicationContext(), "Password and confirm password are not equal", Toast.LENGTH_LONG).show();

                /*
                * If everything is fine, save the password,
                * display a toast and let the user progress
                */

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

                /*
                * Set the password as "", so the user
                * automatically enters the MainActivity
                * when starting the application
                */

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


        //Turn on or off numbers when getting a random password
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

        //Turn on or off letters when getting a random password
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


            /*
            * Add another character when getting
            * the random password, and update the UI
            */
        btnChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                numberofChars++;
                btnChar.setText("+Chars: " + numberofChars);
                btnChar2.setText("-Chars: " + numberofChars);

                /*
                * A random password can not have less
                * characters than one, and not more than
                * twenty.
                */
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


            /*
            * Remove a character when getting
            * the random password, and update the UI
            */
        btnChar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                numberofChars--;
                btnChar2.setText("-Chars: " + numberofChars);
                btnChar.setText("+Chars: " + numberofChars);

                /*
                * A random password can not have less
                * characters than one, and not more than
                * twenty.
                */

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

        //Generates the random password
        randomPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {StringBuilder randomString2 = new StringBuilder();

                /*
                * If both numbers and letters are on
                * you get a string containing both letters and numbers.
                */
                if (numbersOn == true && lettersOn == true) {
                   String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

                    //Generates the random password
                    Random rnd = new Random();
                    while (randomString2.length() < numberofChars) {
                        int index = (int) (rnd.nextFloat() * randomString.length());
                        randomString2.append(randomString.charAt(index));
                    }
                }

                /*
                * If numbers are off and letters are on
                * you get a string containing just letters
                * and not numbers.
                */
                if (numbersOn == false && lettersOn == true) {
                    String randomString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

                    //Generates the random password
                    Random rnd = new Random();
                    while (randomString2.length() < numberofChars) { // length of the random string.
                        int index = (int) (rnd.nextFloat() * randomString.length());
                        randomString2.append(randomString.charAt(index));
                    }
                }

                /*
                * If numbers are on and letters are off
                * you get a string containing just numbers
                * and not letters.
                */
                if (numbersOn == true && lettersOn == false) {
                    String randomString = "1234567890";

                    //Generates the random password
                    Random rnd = new Random();
                    while (randomString2.length() < numberofChars) { // length of the random string.
                        int index = (int) (rnd.nextFloat() * randomString.length());
                        randomString2.append(randomString.charAt(index));
                    }
                }

                /*
                * If neither numbers or letters are on
                * you get a toast.
                */
                if (numbersOn == false && lettersOn == false) {
                    Toast.makeText(getApplicationContext(), "Select letters, numbers or both", Toast.LENGTH_LONG).show();
                }

                //Puts the random password in the password TextField.
                password.setText(randomString2);

            }
        });



    }



}

package com.group9.carcontroller;

/**
 * This class contains the login
 *
 * @author Isak Magnusson
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText password;
    private Button login, forgotPassword;
    private Context mctx;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
        * Connect the buttons to the UI.
        */
        mctx = getApplicationContext();
        login = (Button) findViewById(R.id.btnLoginID);
        forgotPassword = (Button) findViewById(R.id.forgotPasswordID);
        password = (EditText) findViewById(R.id.passwordID);

        /*
        * Run doLogin. If there is no password saved
        * you will enter application automatically
        */
        doLogin();

        /*
        * Runs doLogin
        */
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                doLogin();
            }
        });

        /*
        * Takes you to the MainActivity, in case
        * you forgot the password
        */
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                Toast.makeText(getApplicationContext(), "It's okay!", Toast.LENGTH_LONG).show();


            }
        });

    }

        /*
        * This method checks if the user entered
        * the correct password.
        */

    private void doLogin() {

        String pwd = password.getText().toString().trim();
         {
         /*
         * If the password you entered is equal
         * to the password you saved.
         */
            int res = AppUtils.checkLogin(mctx, pwd);

             if(res == 1){

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

          /*
         * If the password you entered is not equal
         * to the password you saved.
         */

            }  else if(res == 2){

                 if (counter == 0){
                     Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
                     counter++;
                 }else {
                     Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_LONG).show();
                 }

             }


        }


    }


}

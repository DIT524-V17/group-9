package com.group9.carcontroller;

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
        mctx = getApplicationContext();
        login = (Button) findViewById(R.id.btnLoginID);
        forgotPassword = (Button) findViewById(R.id.forgotPasswordID);
        password = (EditText) findViewById(R.id.passwordID);

        doLogin();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                doLogin();
            }
        });


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

    private void doLogin() {

        String pwd = password.getText().toString().trim();
         {
            int res = AppUtils.checkLogin(mctx, pwd);

             if(res == 1){

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

            }  else if(res == 2){

                 if (counter == 0){
                     Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
                     counter++;
                 }else {
                     Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_LONG).show();
                 }

             } else {

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);

             }


        }


    }


}

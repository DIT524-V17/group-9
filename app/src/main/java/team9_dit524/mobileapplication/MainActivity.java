package com.example.isakmagnusson.switchmodebtn;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageButton changeTextImageButton;
    TextView message;
    int buttonCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        message = (TextView)findViewById(R.id.tvMessage);
        changeTextImageButton = (ImageButton)findViewById(R.id.imageBChangeText);
        changeTextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //message.setText("Auto");
                buttonCounter++;

                if (buttonCounter == 1) {
                    message.setText("Auto");

                }

                if (buttonCounter == 2) {
                    message.setText("Control");
                    buttonCounter = 0;
                }
            }

        });

    }
}

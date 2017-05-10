package com.group9.carcontroller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickScreen extends AppCompatActivity {

    Button btnBlink, btnStop;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystickscreen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                joystickControl();


            }
        }, 1500);} // Delays action for 1,5 seconds (1500 milliseconds)

       /* @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);


            // Checks the orientation of the screen
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setContentView(R.layout.activity_control);

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                setContentView(R.layout.activity_control);
            }

            carControl();

        }*/



    private void joystickControl() {
        setContentView(R.layout.activity_joystickscreen);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra("ADDRESS");

        new ConnectBT().execute(); //Call the class to connect

        btnBlink = (Button) findViewById(R.id.blinkLights);
        btnStop = (Button) findViewById(R.id.stopCar);

        // Connect button to GUI
        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {

                if((angle == 0) || (angle == 315)|| (angle == 360) ||
                        (angle > 0 && angle < 45) || (angle > 315 && angle < 360)){

                    setAction("r");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAction("q"); // Sends q after 2 seconds to stop car
                        }
                    }, 2000); // Delays action for 2 seconds (2000 milliseconds)

                } else if((angle == 45) || (angle > 45 && angle < 135)) {

                    setAction("f");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAction("q"); // Sends q after 2 seconds to stop car
                        }
                    }, 2000); // Delays action for 2 seconds (2000 milliseconds)

                } else if((angle == 135) || (angle > 135 && angle < 225)) {

                    setAction("l");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAction("q"); // Sends q after 2 seconds to stop car
                        }
                    }, 2000); // Delays action for 2 seconds (2000 milliseconds)

                } else if((angle == 225) || (angle > 225 && angle < 315)) {

                    setAction("b");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAction("q"); // Sends q after 2 seconds to stop car
                        }
                    }, 2000); // Delays action for 2 seconds (2000 milliseconds)
                }
            }
        });

        btnBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("j");
            }
        });

        btnStop.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("q");
            }
        }));
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case android.R.id.home:
                Disconnect();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
        finish(); //return to the first layout
    }


    private void setAction(String actionChar) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(actionChar.getBytes());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(JoystickScreen.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {

                Toast.makeText(getApplicationContext(), "Connection Failed. Is it a SPP Bluetooth? Try again", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }


}

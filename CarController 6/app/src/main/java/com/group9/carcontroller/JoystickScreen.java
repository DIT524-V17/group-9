package com.group9.carcontroller;

/**
 * This class allows the user to control the
 * car remotely using a virtual joystick.
 *
 * @author Kosara Golemshinska
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;
import io.github.controlwear.virtual.joystick.android.JoystickView;

public class JoystickScreen extends AppCompatActivity {

    /*
     * Declare the UI and the Bluetooth
     * connection elements.
     */
    Button btnBlink, btnStop, btnBack;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystickscreen);

        /*
         * Add a delay of 1,5 seconds before the
         * activity starts.
         */
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                joystickControl();


            }
        }, 1500);} // Delays action for 1,5 seconds (1500 milliseconds)

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

        /*
         * Connect the buttons to the UI.
         */
        btnBlink = (Button) findViewById(R.id.blinkLights);
        btnStop = (Button) findViewById(R.id.stopCar);
        btnBack = (Button) findViewById(R.id.goBack);

        /*
         * Initialize the joystick object.
         */
        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);

        /*
         * Depending on the angle, the app
         * sends certain instructions to the car.
         */
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {

                if((angle == 0) || (angle == 315)|| (angle == 360) ||
                        (angle > 0 && angle < 45) || (angle > 315 && angle < 360)){

                    /*
                     * Turn right.
                     */
                    setAction("r");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAction("q"); // Sends q after 2 seconds to stop car
                        }
                    }, 2000); // Delays action for 2 seconds (2000 milliseconds)

                }
                /*
                 * Move forward.
                 */
                else if((angle == 45) || (angle > 45 && angle < 135)) {

                    setAction("f");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAction("q"); // Sends q after 2 seconds to stop car
                        }
                    }, 2000); // Delays action for 2 seconds (2000 milliseconds)

                }

                /*
                 * Turn left.
                 */
                else if((angle == 135) || (angle > 135 && angle < 225)) {

                    setAction("l");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setAction("q"); // Sends q after 2 seconds to stop car
                        }
                    }, 2000); // Delays action for 2 seconds (2000 milliseconds)

                }
                /*
                 * Move backwards.
                 */
                else if((angle == 225) || (angle > 225 && angle < 315)) {

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

        /*
         * Below, are the blink and stop buttons,
         * as well as the button that returns to the
         * previous activity.
         */
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

        btnBack.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JoystickScreen.this, ControlActivity.class);
                i.putExtra("ADDRESS", address);
                startActivity(i);
                Disconnect();
                finish();
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

    /*
     * This method sends the appropriate character
     * to the Bluetooth Serial port of the car.
     * @param actionChar
     */
    private void setAction(String actionChar) {
        if (btSocket != null) {
            try {
                //Uncomment the line below to see the stream output.
                //Log.e("joystickscreen", actionChar);
                btSocket.getOutputStream().write(actionChar.getBytes());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
     * This class handles the Bluetooth connection
     * to the car.
     */
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

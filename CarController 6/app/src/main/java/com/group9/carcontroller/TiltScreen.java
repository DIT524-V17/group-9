package com.group9.carcontroller;

/**
 * This class allows the user to control the car
 * remotely tilting their phone.
 *
 * Created by Kosara Golemshinska.
 */

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.UUID;

public class TiltScreen extends AppCompatActivity implements SensorEventListener {

    /**
     * Declare the sensor, UI and Bluetooth
     * connection elements.
     */
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Handler Handler;
    private HandlerThread HandlerThread;
    Button btnBlink, btnStop, btnBack;
    TextView txt;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiltscreen);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra("ADDRESS");

        /**
         * Call the class to connect.
         */
        new TiltScreen.ConnectBT().execute();

        /**
         * Connect the buttons to the UI.
         */
        btnBlink = (Button) findViewById(R.id.blinkLights);
        btnStop = (Button) findViewById(R.id.stopCar);
        btnBack = (Button) findViewById(R.id.goBack);

        /**
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
                Intent i = new Intent(TiltScreen.this, ControlActivity.class);
                i.putExtra("ADDRESS", address);
                startActivity(i);
                Disconnect();
                finish();
            }
        }));

        /**
         * Retrieve accelerometer data using the sensor manager.
         */
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /**
         * Register a listener to monitor the accelerometer.
         */
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL, Handler);

        /**
         * Initialize and start the worker thread.
         */
        HandlerThread = new HandlerThread("Worker Thread");
        HandlerThread.start();

        /**
         * Initialize the handler and post the UI element changes to it.
         */
        Handler = new Handler(HandlerThread.getLooper());
        Handler.post(Run);

    }

    /**
     * Send the connection between the accelerometer
     * data and the UI to the Handler.
     */
    private Runnable Run = new Runnable(){
        @Override
        public void run ()
        {
            while(true)
            {
                try
                {
                    txt = (TextView) findViewById(R.id.txt);
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Failed to load angle.", Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    /**
     * Register the sensor manager listener
     * every time the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            /**
             * Register a listener to monitor the accelerometer.
             */
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL, Handler);

            /**
             * Initialize and start the worker thread.
             */
            HandlerThread = new HandlerThread("Worker Thread");
            HandlerThread.start();

            /**
             * Initialize the handler and post the UI element changes to it.
             */
            Handler = new Handler(HandlerThread.getLooper());
            Handler.post(Run);

        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Failed to register.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Unregister the sensor manager listener
     * every time the activity is paused.
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            /**
             * Unregister the accelerometer listener and quit the thread safely.
             */
            mSensorManager.unregisterListener(this);
            HandlerThread.quitSafely();

        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "Failed to unregister.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method is left blank intentionally.
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * This method selects a character to send
     * to the car based on the angle the phone is in.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        if (Math.abs(x) > Math.abs(y)) {
            /**
             * Turn right.
             */
            if (x < 0) {

                setAction("r");
                txt.setText("Right");

            }
            /**
             * Turn left.
             */
            if (x > 0) {

                setAction("l");
                txt.setText("Left");

            }
        }
        /**
         * Move forward.
         */
        else {
            if (y < 0) {

                setAction("f");
                txt.setText("Forth");

            }
            /**
             * Move backwards.
             */
            if (y > 0) {

                setAction("b");
                txt.setText("Back");

            }
        }
        /**
         * If the phone is lying flat, i.e.,
         * it's not tilted, stop the car.
         */
        if (x > (-2) && x < (2) && y > (-2) && y < (2)) {
            setAction("q");
            txt.setText("Flat");
        }
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

    /**
     * This method sends the appropriate character
     * to the Bluetooth Serial port of the car.
     * @param actionChar
     */
    private void setAction(String actionChar) {
        /**
         * If the connections is successful,
         * write the appropriate character to
         * the Bluetooth of the Arduino.
         */
        if (btSocket != null && isBtConnected) {
            try {
                Log.e("tiltscreen", actionChar);
                btSocket.getOutputStream().write(actionChar.getBytes());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This class handles the Bluetooth connection
     * to the car.
     */
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(TiltScreen.this, "Connecting...", "Please wait!!!");  //show a progress dialog
            progress.dismiss();
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
                // Blink works fine
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }


}

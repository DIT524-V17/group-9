/**
 * Class with all the main functions of the applications,
 * e.g. control buttons for manual mode, buttons for turning autonomous
 * and light recognition mode on/off, a button for switching to joystick
 * mode and a button for making the car lights blink.
 *
 * @author Isak : lines 525-558, 157-177, 187
 * @author Melinda : lines 140-169, 176-205, putting the handling of button presses in separate methods: 212-240, 393-474
 * @author Nina Uljanic : lines 136-137, 341, 355-391, 556-586
 * @author Kosara : lines 120-121, 227-261
 */
package com.group9.carcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ControlActivity extends AppCompatActivity {

    //Define buttons
    Button btnBlink;
    ImageButton btnUp, btnDown, btnStop, btnLeft, btnRight;
    ToggleButton autonomousSwitch, lightSwitch;

    boolean autonomousOn, followLightOn;

    TextView autoText, lightText, piCamText;

    private ConnectedThread mConnectedThread;

    Handler bluetoothIn;

    final int handlerState = 0; //used to identify handler message
    // private BluetoothAdapter btAdapter = null;
    private StringBuilder recDataString = new StringBuilder();

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                carControl();

            }
        }, 1500); // Delays action for 1,5 seconds (1500 milliseconds)
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_control);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_control);
        }
        carControl();
    }


    private void carControl() {
        setContentView(R.layout.activity_control);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra("ADDRESS");
        autonomousOn = false;
        followLightOn = false;

        //Call the class to connect
            /*We need an Asynchronous class to connect to not block te main thread(the Activity)*/
        new ConnectBT().execute();

        // Connect button to GUI
        btnUp = (ImageButton) findViewById(R.id.up);
        btnDown = (ImageButton) findViewById(R.id.down);
        btnLeft = (ImageButton) findViewById(R.id.left);
        btnRight = (ImageButton) findViewById(R.id.right);
        btnStop = (ImageButton) findViewById(R.id.stop);
        btnBlink = (Button) findViewById(R.id.blink);

        autonomousSwitch = (ToggleButton) findViewById(R.id.autonomous);
        lightSwitch = (ToggleButton) findViewById(R.id.light);

        lightText = (TextView) findViewById(R.id.lightTextID);
        autoText = (TextView) findViewById(R.id.autoTextID);

        piCamText = (TextView) findViewById(R.id.piCam);
        piCamText.setVisibility(View.GONE);

        enableKeys();

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressLeft();
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressRight();
            }
        });
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressDown();
            }
        });
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressUp();
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                //If message is what we want
                if (msg.what == handlerState) {
                    //Msg.arg1 = bytes from connect thread
                    String readMessage = (String) msg.obj;
                    //Append string
                    recDataString.append(readMessage);

                    System.out.println(recDataString.charAt(0));
                    //If it starts with c we know it is what we are looking for
                    if (recDataString.charAt(0) == 'c')
                    {
                        Toast.makeText(getApplicationContext(), "Obstacle is in front", Toast.LENGTH_SHORT).show();

                        /*Disabling and changing the colour of the up arrow when there's an obstacle ahead*/
                        disableKey(btnUp);
                        //If it starts with t we know it is what we are looking for
                    } else if (recDataString.charAt(0) == 't')
                    {
                        Toast.makeText(getApplicationContext(), "Obstacle is in back", Toast.LENGTH_SHORT).show();

                        /*Disabling and changing the colour of the down arrow when there's an obstacle behind*/
                        disableKey(btnDown);
                    } else if (recDataString.charAt(0) == 'x') {
                        enableKey(btnUp);
                    } else if (recDataString.charAt(0) == 'u') {
                        enableKey(btnDown);
                    }
                    //clear all string data
                    recDataString.delete(0, recDataString.length());
                }
            }
        };

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("q");
                btnUp.setImageResource(R.drawable.uparrow);
                btnDown.setImageResource(R.drawable.downarrow);
                btnLeft.setImageResource(R.drawable.leftarrow);
                btnRight.setImageResource(R.drawable.rightarrow);
                btnStop.setImageResource(R.drawable.stopbuttonclicked);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        btnStop.setImageResource(R.drawable.stopbutton);

                    }
                }, 1000); // Delays action for 1 seconds (1000 milliseconds)
            }
        });

        btnBlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("j");
            }
        });

        /*
         * This button opens the Joystick controls page.
         */

        /*btnJoystick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ControlActivity.this, JoystickScreen.class);
                i.putExtra("ADDRESS", address);
                startActivity(i);
                Disconnect();
                finish();
            }
        }); */

        /*
         * This button opens the tilt controls page.
         */
      /*  btnTilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ControlActivity.this, TiltScreen.class);
                i.putExtra("ADDRESS", address);
                startActivity(i);
                /*
                 * Force all buffered data to be written out
                 * before the output stream closes.
                 */
              /*  try {
                    btSocket.getOutputStream().flush();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Output error.", Toast.LENGTH_SHORT).show();
                }
                Disconnect();
                finish();
            }
        }); */


        autonomousSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autonomousOn) {
                    autonomousOn = false;
                    btnDown.setVisibility(View.VISIBLE);
                    btnUp.setVisibility(View.VISIBLE);
                    btnLeft.setVisibility(View.VISIBLE);
                    btnRight.setVisibility(View.VISIBLE);
                    btnStop.setVisibility(View.VISIBLE);
                    lightSwitch.setVisibility(View.VISIBLE);
                    lightText.setVisibility(View.VISIBLE);
                    setAction("s");
                } else {
                    //TURN it on
                    autonomousOn = true;
                    btnDown.setVisibility(View.GONE);
                    btnUp.setVisibility(View.GONE);
                    btnLeft.setVisibility(View.GONE);
                    btnRight.setVisibility(View.GONE);
                    btnStop.setVisibility(View.GONE);
                    lightSwitch.setVisibility(View.GONE);
                    lightText.setVisibility(View.GONE);
                    setAction("a");
                }
            }
        });

        lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followLightOn) {
                    followLightOn = false;
                    btnDown.setVisibility(View.VISIBLE);
                    btnUp.setVisibility(View.VISIBLE);
                    btnLeft.setVisibility(View.VISIBLE);
                    btnRight.setVisibility(View.VISIBLE);
                    btnStop.setVisibility(View.VISIBLE);
                    autonomousSwitch.setVisibility(View.VISIBLE);
                    autoText.setVisibility(View.VISIBLE);
                    setAction("w");

                    piCamText.setVisibility(View.GONE);

                } else {
                    //TURN it on
                    followLightOn = true;
                    btnDown.setVisibility(View.GONE);
                    btnUp.setVisibility(View.GONE);
                    btnLeft.setVisibility(View.GONE);
                    btnRight.setVisibility(View.GONE);
                    btnStop.setVisibility(View.GONE);
                    autonomousSwitch.setVisibility(View.GONE);
                    autoText.setVisibility(View.GONE);
                    setAction("o");

                    /**
                     * Nina Uljanic
                     * SEM V17
                     * group-9 : MENACE
                     * 11.05.2017.
                     */

                    //Add the picture/text and the toasts: there is an object and there is not
                    piCamText.setVisibility(View.VISIBLE);

                    //Await data from the car

                    bluetoothIn = new Handler() {
                        public void handleMessage(android.os.Message msg) {
                            if (msg.what == handlerState) { //if message is what we want
                                String readMessage = (String) msg.obj; // msg.arg1 = bytes from connect thread
                                recDataString.append(readMessage); //append string

                                //need a counter; after 3 pop the toast

                                if (recDataString.charAt(0) == 'z') //if it starts with r we know it is what we are looking for
                                {
                                    Toast.makeText(getApplicationContext(), "Red object detected.", Toast.LENGTH_SHORT).show();
                                }

                                if (recDataString.charAt(0) == 'n') //if it starts with t we know it is what we are looking for
                                {
                                    Toast.makeText(getApplicationContext(), "No red object detected.", Toast.LENGTH_SHORT).show();
                                }
                                recDataString.delete(0, recDataString.length()); //clear all string data
                            }
                        }
                    };
                }
            }
        });
    }

    /*Handle the back button action*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    /*Handle when left arrow is clicked*/
    private void pressLeft(){
        setAction("l");
        btnUp.setImageResource(R.drawable.uparrow);
        btnDown.setImageResource(R.drawable.downarrow);
        btnLeft.setImageResource(R.drawable.leftarrowclicked);
        btnRight.setImageResource(R.drawable.rightarrow);
        btnStop.setImageResource(R.drawable.stopbutton);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                btnLeft.setImageResource(R.drawable.leftarrow);

            }
        }, 1000); // Delays action for 1 seconds (2000 milliseconds)
    }

    /*Handle when right arrow is clicked*/
    private void pressRight(){
        setAction("r");
        btnUp.setImageResource(R.drawable.uparrow);
        btnDown.setImageResource(R.drawable.downarrow);
        btnLeft.setImageResource(R.drawable.leftarrow);
        btnRight.setImageResource(R.drawable.rightarrowclicked);
        btnStop.setImageResource(R.drawable.stopbutton);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                btnRight.setImageResource(R.drawable.rightarrow);

            }
        }, 1000); // Delays action for 1 seconds (1000 milliseconds)
    }

    /*Handle when up arrow is clicked*/
    private void pressUp(){
        setAction("f");
        btnUp.setImageResource(R.drawable.uparrowclicked);
        btnDown.setImageResource(R.drawable.downarrow);
        btnLeft.setImageResource(R.drawable.leftarrow);
        btnRight.setImageResource(R.drawable.rightarrow);
        btnStop.setImageResource(R.drawable.stopbutton);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                setAction("q"); // Sends q after 2 seconds to stop car
                btnUp.setImageResource(R.drawable.uparrow);

            }
        }, 2000); // Delays action for 2 seconds (2000 milliseconds)
    }

    /*Handle when down arrow is clicked*/
    private void pressDown(){
        setAction("b");
        btnUp.setImageResource(R.drawable.uparrow);
        btnDown.setImageResource(R.drawable.downarrowclicked);
        btnLeft.setImageResource(R.drawable.leftarrow);
        btnRight.setImageResource(R.drawable.rightarrow);
        btnStop.setImageResource(R.drawable.stopbutton);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                setAction("q"); // Sends q after 2 seconds to stop car
                btnDown.setImageResource(R.drawable.downarrow);

            }
        }, 2000); // Delays action for 2 seconds (2000 milliseconds)
    }

    /*Disconnect (bluetooth socket)*/
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


    /*Send a specific action to the Car*/
    private void setAction(String actionChar) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(actionChar.getBytes());
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*This is an Asynchronous Task class used to handle the bluetooth connection
     * This avoid the application to crash by not running the connection process on the main thread */
    private class ConnectBT extends AsyncTask<Void, Void, Void> {

        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(ControlActivity.this, "Connecting...", "Please wait!!!"); //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter(); //get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address); //connects to the device's address and checks if it's available

                    //create a RFCOMM (SPP) connection
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect(); //start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false; //if the try failed, you can check the exception here
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

                mConnectedThread = new ConnectedThread(btSocket);
                mConnectedThread.start();
            }
            progress.dismiss();
        }
    }

    /**
     * @author Isak Magnusson
     */

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    //Read bytes from input buffer
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }

    /**
     * @authod Nina & Melinda
     */

    public void enableKeys() {

        btnLeft.setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);
        btnLeft.setEnabled(true);

        btnRight.setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);
        btnRight.setEnabled(true);

        btnUp.setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);
        btnUp.setEnabled(true);

        btnDown.setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);
        btnDown.setEnabled(true);

    }

    public void disableKey(ImageButton key){
        key.setEnabled(false);
        key.setColorFilter(0xff000000, PorterDuff.Mode.MULTIPLY);
    }

    public void enableKey(ImageButton key){
        key.setEnabled(true);
        key.setColorFilter(0xffffffff, PorterDuff.Mode.MULTIPLY);
    }
}





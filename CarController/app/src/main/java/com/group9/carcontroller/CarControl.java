package com.group9.carcontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class CarControl extends AppCompatActivity {
    //Define buttons
    Button btnAtuomatic, btnStop, btnForward, btnBack, btnRight,btnLeft,btnAlert ;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_control);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //receive the address of the bluetooth device
        Intent newint = getIntent();
        address = newint.getStringExtra("ADDRESS");


        new CarControl.ConnectBT().execute(); //Call the class to connect

        // set the logo
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Connect buttons to GUI
        btnAtuomatic= (Button) findViewById(R.id.automatic);
        btnStop= (Button) findViewById(R.id.stop);
        btnAlert =(Button) findViewById(R.id.alert);


        btnForward = (Button) findViewById(R.id.forward);
        btnBack = (Button) findViewById(R.id.backwards);
        btnRight = (Button) findViewById(R.id.right);
        btnLeft = (Button) findViewById(R.id.left);





        // Set Button Action as an id, to connect Ardoino with the GUI
        btnAtuomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("A");
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("s");
            }
        });
        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("a");
            }
        });
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("f");
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("b");
            }
        });
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("r");
            }
        });
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAction("l");
            }
        });


    }


//
//        @Override
//        public boolean onOptionsItemSelected (MenuItem item){
//            // Take appropriate action for each action item click
//            switch (item.getItemId()) {
//                case android.R.id.home:
//                    Disconnect();
//                    finish();
//                    return true;
//                default:
//                    return super.onOptionsItemSelected(item);
//            }
//        }

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
        progress = ProgressDialog.show(CarControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
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

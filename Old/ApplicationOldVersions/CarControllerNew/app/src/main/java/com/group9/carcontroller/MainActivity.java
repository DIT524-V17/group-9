package com.group9.carcontroller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

public class MainActivity extends AppCompatActivity {
    Button btnPaired,btnPair;
    ListView devicelist;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Connect buttons to GUI
        btnPaired = (Button)findViewById(R.id.button);
        btnPair = (Button)findViewById(R.id.pair);
        devicelist = (ListView)findViewById(R.id.listView);



        /*In Marshmallow we should ask for the "Critical" permissions*/
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of permissions granted.
                turnOnBluetooth();
            }else {
            //Otherwise we should degrade the functionality
                Toast.makeText(getApplicationContext(),"Bluetooth permission is required to connect to the car",Toast.LENGTH_SHORT).show();
            }
        }else{
            turnOnBluetooth();
        }



        btnPaired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                  /*In Marshmallow we should ask for the "Critical" permissions*/
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkAndRequestPermissions()) {
                        // carry on the normal flow, as the case of permissions granted.
                        pairedDevicesList(); //method that will be called
                    }else {
                        //Otherwise we should degrade the functionality
                        Toast.makeText(getApplicationContext(),"Bluetooth permission is required to connect to the car",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    pairedDevicesList();
                }
            }
        });




        btnPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                  /*In Marshmallow we should ask for the "Critical" permissions*/
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkAndRequestPermissions()) {
                        Intent intentBluetooth = new Intent();
                        intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intentBluetooth);
                    }else {
                        //Otherwise we should degrade the functionality
                        Toast.makeText(getApplicationContext(),"Bluetooth permission is required to connect to the car",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Intent intentBluetooth = new Intent();
                    intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intentBluetooth);
                }
            }
        });

    }


    private void turnOnBluetooth(){
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        }
        else
        {
            if (myBluetooth.isEnabled())
            { }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }
    }

    private void pairedDevicesList()
    {
        pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);
        devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }


    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            // Make an intent to start next activity.   /// ControlActivity. class was changed into Modeselection
            Intent i = new Intent(MainActivity.this, ModeSelection.class);
            //Change the activity.
            i.putExtra("ADDRESS", address); //this will be received at ledControl (class) Activity
            startActivity(i);
        }
    };

    /*This part is Marshmallow specific as we have to ask for the permission on real-time*/
    private boolean checkAndRequestPermissions() {
        int bluetoothPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothAdminPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
//---------------------------------------------------
        List<String> listPermissionsNeeded = new ArrayList<>();
//---------------------------------------------

        if (bluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
//---------------------------------
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 2);
            return false;
        }
        return true;
    }

}

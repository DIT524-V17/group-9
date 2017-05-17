/*
 * This Activity was created as a home page to achieve the Bluetooth paring process,
 * by initializing 2 buttons, "Pair a car":  checks and request a "Critical" permission,
 * And it should be requested in SDK>=23 (in Marshmallow)
 * which REQUIRES : BLUETOOTH_ADMIN permission(to change the bluetooth configuration and turing on the BT device).
 * "Cars' list": Checks the paired devices list and request a "Critical" permission,
 * which REQUIRES : BLUETOOTH permission(to use the bluetooth connection).
 *
 * @author - Rema Salman
 */

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
    Button btnList, btnPair;
    ListView devicelist;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set support bar logo and enable home button
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // Connect buttons to GUI
        btnList = (Button) findViewById(R.id.btnListCars);
        btnPair = (Button) findViewById(R.id.pairCar);
        devicelist = (ListView) findViewById(R.id.listView);
        devicelist.setVisibility(View.GONE);


        /*
        * Checking if the device have a bluetooth device, when the application is launched
        * and Turn on the bluetooth if so..
        * Check  turnOnBluetooth() and checkAndRequestPermissions()
        * REQUIRES : BLUETOOTH_ADMIN permission
        * */

        /* Ask for the "Critical" permissions, in Marshmallow*/
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkAndRequestPermissions()) {
                // carry on the normal flow, as the case of permissions granted.
                turnOnBluetooth();
            } else {
                // Degrade the functionality
                Toast.makeText(getApplicationContext(), "Bluetooth permission is required to connect to the car", Toast.LENGTH_SHORT).show();
            }
        } else {
            turnOnBluetooth();
        }

        /*
        * click listener for listing the paired devices
        * Check pairedDevicesList() and checkAndRequestPermissions()
        * REQUIRES : BLUETOOTH permission
        * */

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicelist.setVisibility(View.VISIBLE);
                  /* Ask for the "Critical" permissions, in Marshmallow*/
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkAndRequestPermissions()) {
                        // carry on the normal flow, as the case of permissions granted.
                        pairedDevicesList(); //method that will be called
                    } else {
                        // Degrade the functionality
                        Toast.makeText(getApplicationContext(), "Bluetooth permission is required to connect to the car", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pairedDevicesList();
                }
            }
        });


        /*
        * click listener for pairing new devices
        * checkAndRequestPermissions()
        * REQUIRES : BLUETOOTH_ADMIN permission
        * */

        btnPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  /* Ask for the "Critical" permissions, in Marshmallow*/
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkAndRequestPermissions()) {
                        Intent intentBluetooth = new Intent();
                        intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intentBluetooth);
                    } else {
                        // Degrade the functionality
                        Toast.makeText(getApplicationContext(), "Bluetooth permission is required to connect to the car", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intentBluetooth = new Intent();
                    intentBluetooth.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivity(intentBluetooth);
                }
            }
        });

    }

    /*
    * This method checks if the bluetooth device is available on the phone first
    * Then it will try to enable the bluetooth connection if its already OFF
    * Note: This requires BLUETOOTH_ADMIN permission to change the bluetooth config
    * And should be requested in SDK>=23
    * */

    private void turnOnBluetooth() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (myBluetooth == null) {
            //Show a message, that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk (Never finish the app its not user friendly)
            //finish();
        } else {
            if (myBluetooth.isEnabled()) {
                // every thing is working as intended
            } else {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }
    }

    /* This method will:
    *   - Get a list of all paired devices
    *   - Display them in the listview
    *   - Add an action for each item
    *   - Action is to open new Controller intent for the device clicked on
    *
    * REQUIRES : BLUETOOTH permission
    */

    private void pairedDevicesList() {
        // Request the paired devices list from the bluetooth settings
        pairedDevices = myBluetooth.getBondedDevices();


        ArrayList list = new ArrayList();
        if (pairedDevices.size() > 0) {
            //Iterate through the bluetooth devices and add them to a list
            for (BluetoothDevice bt : pairedDevices) {
                //Get the device's name and the address
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        // Prepare the list adapter to list the Java list in a list view
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        devicelist.setAdapter(adapter);

        //Method is called on an item(device) in the list view is clicked on
        devicelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String listItemText = ((TextView) view).getText().toString();
                // Get the device MAC address, the last 17 chars in the View
                String address = listItemText.substring(listItemText.length() - 17);

                // Make an intent to start next activity.
                // The "controller" for this specific car
                Intent i = new Intent(MainActivity.this, ControlActivity.class);

                //this will be received at ControlActivity (holding the address info)
                i.putExtra("ADDRESS", address);
                startActivity(i);
            }
        });

    }



    /*
    This part is Marshmallow specific, that asks for the permission in real-time
    ----------------------
    * In Android versions 23 and above .. the application should request the user's permission
    * before using the corresponding device or a service
    * --------------------
    * In this application, only two permissions are used:
    * BLUETOOTH(Use the bluetooth connection) and
    * BLUETOOTH_ADMIN(for changing configuration and turing on the BT device)
    * They are declared in the application's (manifest)*/

    private boolean checkAndRequestPermissions() {
        int bluetoothPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothAdminPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        //---------------------------------------------------
        List<String> listPermissionsNeeded = new ArrayList<>();
        //---------------------------------------------

        if (bluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }
        //---------------------------------
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 2);
            return false;
        }
        return true;
    }


}

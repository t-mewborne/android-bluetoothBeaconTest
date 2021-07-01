package com.example.bluetoothnearbydevicebeacontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;


// Android Docs Official Tutorial
// https://developer.android.com/guide/topics/connectivity/bluetooth/setup
public class MyBluetoothManager extends MainActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<BluetoothObject> devices = new ArrayList<BluetoothObject>();
    //private int REQUEST_ENABLE_BT = 99; // Any positive integer should work.
    private Boolean deviceBluetoothEnabled = false;
    private Boolean discoveryMode = false;
    private Boolean receiverRegistered = false;
    private Boolean activityStarted = false;
    private String debugTag = "MyBluetoothManager";
    private Context context;


    public MyBluetoothManager(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context=context;
        Log.d(debugTag,"MyBluetoothManager -- Object Created");
    }


    public boolean startDiscovery() {
        Log.d(debugTag, "startDiscovery -- function called");

        if (deviceBluetoothEnabled && !discoveryMode) {
            Log.d(debugTag,"startDiscovery -- attempting to enable discovery mode");
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            if (!receiverRegistered) context.registerReceiver(myReceiver, filter);
            receiverRegistered = true;
            Log.d(debugTag, "startDiscovery -- receiver registered");
            discoveryMode = bluetoothAdapter.startDiscovery();
            if (discoveryMode) Log.d(debugTag, "startDiscovery -- discovery started");
            else Log.e(debugTag, "startDiscovery -- Failed to enable discovery mode. OS Rejected start request\n"+booleanStatus());
        } else if (!deviceBluetoothEnabled) {
            Log.d(debugTag,"startDiscovery -- attempting to enable bluetooth...");
            enableBluetoothOnDevice();
            if(deviceBluetoothEnabled) startDiscovery();
            /*
            String message = "startDiscovery -- cannot enter discovery mode, failed to enable bluetooth\n" + booleanStatus();
            Log.e(debugTag,message);
            super.searchFailure(message);*/
        }
        return discoveryMode;
    }

    //Enable bluetooth on the device (if possible)
    private void enableBluetoothOnDevice()
    {
        Log.d(debugTag,"enableBluetoothOnDevice -- Function Called");
        if (bluetoothAdapter == null) {
            String message = "This device does not have a bluetooth adapter";
            Log.e(debugTag,message);
            super.searchFailure(message);
            return;
        }

        deviceBluetoothEnabled = bluetoothAdapter.isEnabled();
        if(!deviceBluetoothEnabled)
        {
            Log.d(debugTag,"enableBluetoothOnDevice -- Adapter not enabled. Enabling now...");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBtIntent);
        }
        if(deviceBluetoothEnabled) Log.d(debugTag,"enableBluetoothOnDevice -- successfully enabled bluetooth");
        else Log.e(debugTag,"enableBluetoothOnDevice -- failed to enable bluetooth");
    }

    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        //Log.d(debugTag,"myReceiver -- created");
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //Discovery Has Found a Device!
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BluetoothObject deviceData = new BluetoothObject(device);
                deviceData.setRSSI(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                devices.add(deviceData);
                MyBluetoothManager.super.updateDeviceTable(devices);
                Log.d(debugTag,"myReceiver -- Device found named \"" + device.getName() + "\"");
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(debugTag,"onActivityResult -- function called");
        super.onActivityResult(requestCode, resultCode, data);
        activityStarted = resultCode == RESULT_OK;
        if (!activityStarted) Log.e(debugTag, "onActivityResult -- invalid result code. Unable to continue.");
        else {
            Log.d(debugTag,"onActivityResult -- Result OK");
            if(!discoveryMode) startDiscovery();
            discoveryMode=true;
        }
    }

    public void stopDiscovery() {
        if (discoveryMode) {
            bluetoothAdapter.cancelDiscovery();
            discoveryMode=false;
        }
        if (receiverRegistered) {
            try {
                context.unregisterReceiver(myReceiver);
                Log.d(debugTag,"stopDiscovery -- Receiver Unregistered");
            } catch (NullPointerException e){
                Log.e(debugTag,"stopDiscovery -- Failed to unregister receiver with error:\n"+e);
            }
            receiverRegistered = false;
        }
        deviceBluetoothEnabled = false;
        activityStarted = false;
    }

    public String booleanStatus() {
        return "deviceBluetoothEnabled...." + deviceBluetoothEnabled + '\n' +
               "discoveryMode............." + discoveryMode + '\n' +
               "receiverRegistered........" + receiverRegistered + '\n' +
               "activityStarted..........." + activityStarted;
    }

    protected void onDestroy() {
        super.onDestroy();
        stopDiscovery();
    }
}

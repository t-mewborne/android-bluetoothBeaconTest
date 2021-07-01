package com.example.bluetoothnearbydevicebeacontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

@Deprecated
public class NearbyBluetoothDevices {
    BluetoothAdapter bluetoothAdapter;
    ArrayList arrayOfFoundBTDevices;
    Boolean active = false;
    public NearbyBluetoothDevices(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
        ArrayList arrayOfFoundBTDevices = new ArrayList<BluetoothDevice>();
    }

    public ArrayList<BluetoothDevice> getBlutoothDevices() {
        return arrayOfFoundBTDevices;
    }

    public ArrayList<BluetoothDevice> updateBluetoothDevices()
    {

        bluetoothAdapter.startDiscovery(); // start looking for bluetooth device
        active=true;
        // Discover new devices
        // Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the “RSSI” to get the signal strength as integer,
                    // but should be displayed in “dBm” units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);

                    // Create the device object and add it to the arrayList of devices
                    /*
                    BluetoothDevice bluetoothDevice = new BluetoothDevice;
                    bluetoothDevice.setName(device.getName());
                    bluetoothDevice.setAddress(device.getAddress());
                    bluetoothDevice.setState(device.getBondState());
                    bluetoothDevice.setUUIDS(device.getUuids());
                    bluetoothDevice.setRSSI(rssi);

                    arrayOfFoundBTDevices.add(bluetoothDevice);*/

                    // 1. Pass context and data to the custom adapter
                    //FoundBTDevicesAdapter adapter = new FoundBTDevicesAdapter(getApplicationContext(), arrayOfFoundBTDevices);

                    // 2. setListAdapter
                    //setListAdapter(adapter);
                }


            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        //registerReceiver(mReceiver, filter);
        return arrayOfFoundBTDevices;
    }

    public void stopDiscovery() {
        active=false;
        bluetoothAdapter.cancelDiscovery();
    }


}

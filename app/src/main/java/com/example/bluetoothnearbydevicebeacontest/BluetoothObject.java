package com.example.bluetoothnearbydevicebeacontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;

public class BluetoothObject {
    private String deviceName;
    private String macAddress;
    private int state;
    private ParcelUuid[] UUIDS;
    private int RSSI;

    public BluetoothObject(BluetoothDevice device) {
        setName(device.getName());
        setMacAddress(device.getAddress());
        setState(device.getBondState());
        setUUIDS(device.getUuids());
    }


    public void setName(String deviceName) {this.deviceName = deviceName;}
    public void setMacAddress(String macAddress) {this.macAddress = macAddress;}
    public void setState(int state) {this.state = state;}
    public void setUUIDS(ParcelUuid[] UUIDS) {this.UUIDS = UUIDS;}
    public void setRSSI(int RSSI) {this.RSSI = RSSI;} //Stored in dBm

    public String getName() {
        if (deviceName == null) return "-";
        return deviceName;
    }

    public String getMacAddress() {
        if (macAddress == null) return "-";
        return macAddress;
    }

    public int getState() {
        return state;
    }

    public ParcelUuid[] getUUIDS() {
        //TODO null check
        return UUIDS;
    }

    public int getRSSI() {
        return RSSI;
    }

    public boolean compareAddress(String macAddress) {
        return this.macAddress.equals(macAddress);
    }
}
//Used following tutorial:
//https://stgconsulting.com/scanning-for-bluetooth-devices-in-android/


package com.example.bluetoothnearbydevicebeacontest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyBluetoothManager deviceManager;
    private TableLayout devicesTable;
    private Button startButton;
    private Button stopButton;
    private Boolean active;
    private String debugTag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        active = false;

        deviceManager = new MyBluetoothManager(this);

        devicesTable=findViewById(R.id.tblDevices);
        startButton=findViewById(R.id.btnStart);
        stopButton=findViewById(R.id.btnStop);

        startButton.setOnClickListener(v->btnClickStart());
        stopButton.setOnClickListener(v->btnClickStop());

        setTableToInactiveMode();
    }

    private void btnClickStop() {
        Log.d(debugTag,"Stop clicked (active:"+active+")");
        if (active) {
            setTableToInactiveMode();
            deviceManager.stopDiscovery();
            active = !active;
        }
    }

    private void btnClickStart() {
        Log.d(debugTag,"Start clicked (active:"+active+")");

        if (!active) {
            boolean discoveryStarted = deviceManager.startDiscovery();
            if (discoveryStarted) {
                setTableToSearchingMode();
                active = !active;
            } else {
                Log.e(debugTag,"Discovery start failure\n"+deviceManager.booleanStatus());
                Toast.makeText(this, "Failed to enter discovery mode", Toast.LENGTH_LONG).show();
            }
        } else Log.d(debugTag,deviceManager.booleanStatus());
    }

    public void updateDeviceTable(ArrayList<BluetoothObject> data) {
        Log.d(debugTag,"updateDeviceTable -- function called");
        devicesTable.removeAllViews();
        if (data == null) {
            makeARow("Error: Data is null","");
            return;
        }
        if (data.size()==0) {
            setTableToSearchingMode();
        }
        for (BluetoothObject device : data) {
            makeARow(device.getName(),device.getRSSI()+" dBm");
        }
    }



    private void makeARow(CharSequence textA, CharSequence textB) {
        TableRow row = new TableRow(this);
        TextView textViewA = new TextView(this);
        TextView textViewB = new TextView(this);
        textViewA.setText(textA);
        textViewB.setText(textB);
        row.addView(textViewA);
        row.addView(textViewB);
        devicesTable.addView(row);
    }

    private void setTableToSearchingMode(){
        Log.d(debugTag,"setTableToSearchingMode -- function called");
        devicesTable.removeAllViews();
        TableRow row = new TableRow(this);
        ProgressBar progressBar = new ProgressBar(this);
        row.addView(progressBar);
        row.setVerticalGravity(2);
        devicesTable.addView(row);
    }

    private void setTableToInactiveMode() {
        //https://stackoverflow.com/questions/17031006/how-add-and-delete-rows-to-table-layout-in-java-programically
        Log.d(debugTag,"setTableToInactiveMode -- function called");
        try {
            devicesTable.removeAllViews();
            TableRow row = new TableRow(this);
            TextView text = new TextView(this);
            text.setText("Press Start to Begin Searching...");
            row.addView(text);
            devicesTable.addView(row);
        } catch (NullPointerException e) {
            Log.e(debugTag, "Failed to remove existing views from the table:\n"+e);
        }
    }

    public void searchFailure(String message) {
        Log.e(debugTag,"searchFailure -- function called with message: " + message);
        active = false;
        setTableToInactiveMode();
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //alertDialogBuilder.setTitle("Error");
        //alertDialogBuilder.setMessage(message);
        //AlertDialog alertDialog = alertDialogBuilder.create();
        //alertDialog.show();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (active) {
            setTableToInactiveMode();
            deviceManager.stopDiscovery();
            active = !active;
        }
    }
}
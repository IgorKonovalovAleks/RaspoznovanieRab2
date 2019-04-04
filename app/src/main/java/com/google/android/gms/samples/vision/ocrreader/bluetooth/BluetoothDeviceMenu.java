package com.google.android.gms.samples.vision.ocrreader.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.samples.vision.ocrreader.R;

import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

public class BluetoothDeviceMenu extends AppCompatActivity {


    ArrayList<String> pairedDeviceArrayList;

    ListView listViewPairedDevice;

    ArrayAdapter pairedDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_menu);
        listViewPairedDevice = findViewById(R.id.pairedlist);
        pairedDeviceArrayList = getIntent().getStringArrayListExtra("DEVICES");
        pairedDeviceAdapter = new ArrayAdapter<>(this, simple_list_item_1, pairedDeviceArrayList);
        listViewPairedDevice.setAdapter(pairedDeviceAdapter);
        listViewPairedDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent();
                i.putExtra("RESULT", listViewPairedDevice.getItemIdAtPosition(position));
                setResult(RESULT_OK, i);
                finish();
            }
        });


    }
}

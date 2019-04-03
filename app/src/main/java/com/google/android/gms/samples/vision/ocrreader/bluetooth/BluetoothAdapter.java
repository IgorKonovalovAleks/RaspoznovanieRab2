package com.google.android.gms.samples.vision.ocrreader.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public final class BluetoothAdapter {


    private static final int REQUEST_ENABLE_BT = 1;

    android.bluetooth.BluetoothAdapter bluetoothAdapter;


    ArrayAdapter<String> pairedDeviceAdapter;
    private UUID myUUID;

    ThreadConnectBTDevice myThreadConnectBTDevice;
    ThreadConnected myThreadConnected;
    Context context;
    AppCompatActivity parent;

    private StringBuilder sb = new StringBuilder();

    public BluetoothAdapter(Context context, AppCompatActivity parent){
        this.context = context;

        this.parent = parent;

        final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();

        String stInfo = bluetoothAdapter.getName() + " " + bluetoothAdapter.getAddress();

        onStart();

    } // END onCreate


    public void onStart() { // Запрос на включение Bluetooth

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE);
            parent.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();

    }

    private void setup() { // Создание списка сопряжённых Bluetooth-устройств

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) { // Если есть сопряжённые устройства

            ArrayList<BluetoothDevice> pairedDeviceList = new ArrayList<>();

            for (BluetoothDevice device : pairedDevices) { // Добавляем сопряжённые устройства - Имя + MAC-адресс
                pairedDeviceList.add(device);
            }

            myThreadConnectBTDevice = new ThreadConnectBTDevice(pairedDeviceList.get(0));
            myThreadConnectBTDevice.start();  // Запускаем поток для подключения Bluetooth
        }
    }

    public void stop() { // Закрытие приложения
        if (myThreadConnectBTDevice != null) myThreadConnectBTDevice.cancel();
    }


    private class ThreadConnectBTDevice extends Thread { // Поток для коннекта с Bluetooth

        private BluetoothSocket bluetoothSocket = null;

        private ThreadConnectBTDevice(BluetoothDevice device) {

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void run() { // Коннект

            boolean success = false;

            try {
                bluetoothSocket.connect();
                success = true;
            }

            catch (IOException e) {
                e.printStackTrace();

                try {
                    bluetoothSocket.close();
                }

                catch (IOException e1) {

                    e1.printStackTrace();
                }
            }

            if(success) {  // Если законнектились, тогда открываем панель с кнопками и запускаем поток приёма и отправки данных

                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start(); // запуск потока приёма и отправки данных
            }
        }


        public void cancel() {

            Toast.makeText(context, "Close - BluetoothSocket", Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            }

            catch (IOException e) {
                e.printStackTrace();
            }
        }

    } // END ThreadConnectBTdevice:


    //method for sending messages from another classes
    public void sendString(String s){
        byte[] bytesToSend = s.getBytes();

    }


    private class ThreadConnected extends Thread {    // Поток - приём и отправка данных

        private final OutputStream connectedOutputStream;

        private String sbprint;

        public ThreadConnected(BluetoothSocket socket) {

            OutputStream out = null;

            try {
                out = socket.getOutputStream();
            }

            catch (IOException e) {
                e.printStackTrace();
            }

            connectedOutputStream = out;
        }


        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }


}

package com.example.user.spirometer2;
/**
 * Created by keiichionishi on 12/7/14.
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends Activity {

    TextView txtArduino, txtString, txtStringLength, sensorView0;
    Handler bluetoothIn;

    final int handlerState = 0;        				 //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();


    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    DBAdapter myDb;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        openDB();
        //Link the buttons and textViews to respective views
        final SpeedometerView sp = new SpeedometerView(this);
        sp.addColoredRange(0,100,0xfaff0000);
        sp.addColoredRange(100,200,0xfa00ff00);

        sp.addColoredRange(200,1000,0xfa0000ff);
        sp.setMinorTicks(2);
        setContentView(R.layout.activity_main);

        LinearLayout layout=(LinearLayout)findViewById(R.id.speed);
        layout.addView(sp);

        txtString = (TextView) findViewById(R.id.txtString);
        txtStringLength = (TextView) findViewById(R.id.testView1);

        sensorView0 = (TextView) findViewById(R.id.sensorView0);
        //mConnectedThread.write("C");
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
               // mConnectedThread.write("C");
                if (msg.what == handlerState) {                                        //if message is what we want
                    String readMessage = (String) msg.obj;                            // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                    //keep appending to string until ~
                   /* int endOfLineIndex = recDataString.charAt(0);                    // determine the end-of-line
                    // make sure there data before ~
                    StringBuilder dataInPrint = recDataString.append(0);    // extract string
                    txtString.setText("Data Received = " + dataInPrint);
                    int dataLength = dataInPrint.length();                            //get length of data received
                    txtStringLength.setText("String Length = " + String.valueOf(dataLength));
                    */
                    if (recDataString.charAt(0) != '#')                                //if it starts with # we know it is what we are looking for
                    {
                        int reading;
                        String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5

                        sensorView0.setText(" Peak Flow Rate = " + sensor0 + "l/min");    //update the textviews with sensor values

                        sp.setSpeed(Double.parseDouble(sensor0));
                        System.out.println(sensor0);
                        reading = Integer.parseInt(sensor0);
                        long newId = myDb.insertReading(sensor0,getCondition(reading));
                        if(getCondition(reading)==1)
                            Toast.makeText(getBaseContext(), "Keep it up. Your respiration health is good", Toast.LENGTH_LONG).show();
                        else if(getCondition(reading)==2)
                            Toast.makeText(getBaseContext(), "You expiration health is getting improved", Toast.LENGTH_LONG).show();
                        else if(getCondition(reading)==3)
                            Toast.makeText(getBaseContext(), "You can do better than this. You need to be cautious", Toast.LENGTH_LONG).show();
                        else if(getCondition(reading)==4)
                            Toast.makeText(getBaseContext(), "Good relative to your previous readings. But still need to improve", Toast.LENGTH_LONG).show();
                        else if(getCondition(reading)==5)
                            Toast.makeText(getBaseContext(), "You show much improvement for your treatments", Toast.LENGTH_LONG).show();
                        else if(getCondition(reading)==6)
                            Toast.makeText(getBaseContext(), "You show much improvement for your treatments. But still you need treatment from hospital or doctor", Toast.LENGTH_LONG).show();
                        else if(getCondition(reading)==7)
                            Toast.makeText(getBaseContext(), "It is good for you if you meet the doctor and do a checkup", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getBaseContext(), "You must see the doctor and get treatment immediately", Toast.LENGTH_LONG).show();
                        mConnectedThread.write("R");
                    }
                    recDataString.delete(0, recDataString.length());                    //clear all string data
                    // strIncom =" ";
                    //dataInPrint = " ";
                }
            }

        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }

    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }


    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }
    private void closeDB() {
        myDb.close();
    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }
    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("R");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }


    //get condition
    public int getCondition(int reading){
        Cursor cursor = myDb.getAllDetails();
        int age=0,avg=0,good,last=0;
        float weight = 0, height = 0;
        String gender =null;
        try {
            if (cursor.moveToFirst()) {
                do {
                    // Process the data:
                    age = myDb.getAge();
                    weight = cursor.getInt(DBAdapter.COL_WEIGHT);
                    height = cursor.getInt(DBAdapter.COL_HEIGHT);
                    gender = cursor.getString(DBAdapter.COL_GENDER);
                    last = myDb.getLastValue();
                    if(myDb.getAverageValue()!=0){
                        avg = myDb.getAverageValue();
                    }
                    else
                        avg = myDb.getHighestValue();
                } while (cursor.moveToNext());
            }
            if (gender == "female") {
                if (age >= 55) {
                    if (height >= 175) {
                        good = 450;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else if(height >= 155){
                        good = 430;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else{
                        good = 400;
                        return getConditionNum(good,avg,last,reading);
                    }
                }
                else if (age >= 25) {
                    if (height >= 175) {
                        good = 490;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else if(height >= 155){
                        good = 470;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else{
                        good = 450;
                        return getConditionNum(good,avg,last,reading);
                    }
                }
                else{
                    if (height >= 175) {
                        good = 480;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else if(height >= 155){
                        good = 460;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else{
                        good = 440;
                        return getConditionNum(good,avg,last,reading);
                    }
                }
            }
            else{
                if (age >= 55) {
                    if (height >= 190) {
                        good = 580;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else if(height >= 175){
                        good = 560;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else{
                        good = 530;
                        return getConditionNum(good,avg,last,reading);
                    }
                }
                else if (age >= 25) {
                    if (height >= 195) {
                        good = 660;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else if(height >= 175){
                        good = 630;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else{
                        good = 610;
                        return getConditionNum(good,avg,last,reading);
                    }
                }
                else{
                    if (height >= 190) {
                        good = 565;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else if(height >= 175){
                        good = 545;
                        return getConditionNum(good,avg,last,reading);
                    }
                    else{
                        good = 525;
                        return getConditionNum(good,avg,last,reading);
                    }
                }
            }
        }
        catch (Exception e){
            Toast.makeText(this,"You haven't ender your details",Toast.LENGTH_LONG).show();
        }
        return 0;
    }

    private int getConditionNum(int good, int avg, int last, int reading){
        if(reading>=good && avg >= good ){
            return 1;
        }
        else if(reading>=good && avg >= good*.75){
            return 2;
        }
        else if(reading >= good*.75 && avg >= good){
            return 3;
        }
        else if(reading >= good*.75 && avg >= good*.75){
            return 4;
        }
        else if(reading >= good*.75 && reading > last){
            return 5;
        }
        else if(reading >= good*.5 && reading > last){
            return 6;
        }
        else if(reading >= good*.5 && reading < last){
            return 7;
        }
        else if(reading < good*.5){
            return 8;
        }
        else
            return 8;
    }
    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }
}


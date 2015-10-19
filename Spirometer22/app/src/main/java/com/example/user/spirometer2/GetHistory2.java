package com.example.user.spirometer2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * Steps to using the DB:
 * 1. [DONE] Instantiate the DB Adapter
 * 2. [DONE] Open the DB
 * 3. [DONE] use get, insert, delete, .. to change data.
 * 4. [DONE]Close the DB
 */

/**
 * Demo application to show how to use the 
 * built-in SQL lite database.
 */
public class GetHistory2 extends Activity {

    DBAdapter myDb;
    ArrayAdapter<String> details;
    ArrayList<String> messages = new ArrayList<String>();
    ArrayList<Integer> feedback = new ArrayList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_history2);
        System.out.println("done");
        openDB();
        Cursor cursor = myDb.getAllReadings();
        displayRecordSet(cursor);
    }
    @Override
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



    private void displayText(String message) {
        TextView textView = (TextView) findViewById(R.id.textDisplay);
        textView.setText(message);
    }

    //get feedback
    public String getFeedback(int condition){
        if(condition ==1)
            return "Wonderful";
        else if(condition == 2)
            return "Impressive";
        else if(condition == 3)
            return "Not Bad";
        else if(condition == 4)
            return "Good";
        else if(condition == 5)
            return "Nice";
        else if(condition == 6)
            return "Improved";
        else if(condition == 7)
            return "Not Good";
        else
            return "Worse";
    }

    public void onClick_ClearAll(View v) {
        displayText("Clicked clear all!");
        myDb.deleteAllDetails();
    }

    public void onClick_DisplayRecords(View v) {
        System.out.println(myDb.getValue(5));
        System.out.println(myDb.getNumRows());
        displayText("Clicked display record!");

        Cursor cursor = myDb.getAllReadings();
        displayRecordSet(cursor);
    }
    // Display an entire recordset to the screen.
    private void displayRecordSet(Cursor cursor) {
        String message = "";
        // populate the message from the cursor

        // Reset cursor to start, checking to see if there's data:
        if (cursor.moveToLast()) {
            do {
                // Process the data:
                long id = cursor.getLong(DBAdapter.COL_ROWID);
                int condition = cursor.getInt(DBAdapter.COL_CONDITION);
                String reading = cursor.getString(DBAdapter.COL_READING);
                String time = cursor.getString(DBAdapter.COL_TIME);


                // Append data to the message:
                message = "Reading = " + reading + "\n"
                        +"time = " + time;
                messages.add(message);
                feedback.add(condition);
            } while(cursor.moveToPrevious());

        } //*/
        System.out.println(messages);
        details = new ArrayAdapter<String>(this,R.layout.history,messages);
        // Find and set up the ListView for paired devices
        ListView detailList = (ListView) findViewById(R.id.listView);
        detailList.setAdapter(details);
        detailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt = (TextView) view;
                int fb = feedback.get(position);
                String fdbk = getFeedback(fb);
                //Toast.makeText(GetHistory2.this,fdbk,Toast.LENGTH_LONG).show();
                final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(GetHistory2.this);
                dlgAlert.setTitle(fdbk);
                if(fdbk=="Wonderful")
                    dlgAlert.setMessage("Keep it up. Your respiration health is good");
                else if(fdbk=="Impressive")
                    dlgAlert.setMessage("You expiration health is getting improved");
                else if(fdbk == "Not Bad")
                    dlgAlert.setMessage("You can do better than this. You need to be cautious");
                else if(fdbk == "Good")
                    dlgAlert.setMessage("Good relative to your previous readings. But still need to improve");
                else if(fdbk == "Nice")
                    dlgAlert.setMessage("You show much improvement for your treatments");
                else if(fdbk == "Improved")
                    dlgAlert.setMessage("You show much improvement for your treatments. But still you need treatment from hospital or doctor");
                else if(fdbk == "Not Good")
                    dlgAlert.setMessage("It is good for you if you meet the doctor and do a checkup");
                else
                    dlgAlert.setMessage("You must see the doctor and get treatment immediately");
                dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog

                    }
                });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        });
        // Close the cursor to avoid a resource leak.
        cursor.close();
    }

}











package com.example.user.spirometer2;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
public class EnterDetails extends Activity {
	
	DBAdapter myDb;
    EditText edit_Age;
    EditText edit_Weight;
    EditText edit_Height;
    RadioGroup radioGroup;
    //EditText edit_Gender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_details);
		
		openDB();
        Cursor cursor = myDb.getAllDetails();
        displayRecordSet(cursor);
        /* Initialize Radio Group and attach click handler */
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        //radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
            }
        });

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
	
	

	public void onClick_Submit(View v) {

		//displayText("Clicked add record!");
        edit_Age=(EditText)findViewById(R.id.editAge);
        edit_Weight=(EditText)findViewById(R.id.editWeight);
        edit_Height=(EditText)findViewById(R.id.editHeight);
        RadioButton rb = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        try{

            int weight = Integer.parseInt(edit_Weight.getText().toString());
            int height = Integer.parseInt(edit_Height.getText().toString());
            String gender = rb.getText().toString();
            String dob = edit_Age.getText().toString();
                onClick_ClearAll(v);
                long newId = myDb.insertDetails(dob, weight, height, gender);
                // Query for the record we just added.
                // Use the ID:
                Cursor cursor = myDb.getDetails(newId);
                displayRecordSet(cursor);
              Toast.makeText(this,"Successfully Updated",Toast.LENGTH_LONG);

        }
        catch (Exception e){
            Toast.makeText(this,"Please Fill the details correctly ",Toast.LENGTH_LONG).show();
        }
    }

	public void onClick_ClearAll(View v) {
		displayText("Clicked clear all!");
		myDb.deleteAllDetails();
	}
	
	// Display an entire recordset to the screen.
	private void displayRecordSet(Cursor cursor) {
		String message = "";
		// populate the message from the cursor
		
		// Reset cursor to start, checking to see if there's data:
		if (cursor.moveToFirst()) {
			do {
				// Process the data:
				int age = myDb.getAge();
                int weight = cursor.getInt(DBAdapter.COL_WEIGHT);
                int height = cursor.getInt(DBAdapter.COL_HEIGHT);
                String gender = cursor.getString(DBAdapter.COL_GENDER);
                String time = cursor.getString(DBAdapter.COL_TIME);
                int avg = myDb.getAverageValue();
				
				// Append data to the message:
				message +="Average Peak Flow rate : " + avg +"\n\n"
						  +"age       :  " + age +"\n"
						  +"height  :  " + height +"\n"
						  +"weight  :  " + weight +"\n"
						  +"gender  :  " + gender
                          +"\n";
			} while(cursor.moveToNext());
		} //*/

		// Close the cursor to avoid a resource leak.
		cursor.close();
		
		displayText(message);
	}
}











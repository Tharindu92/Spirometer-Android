package com.example.user.spirometer2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by USER on 8/12/2015.
 */
public class Main extends ActionBarActivity {

    DBAdapter myDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        try {
            openDB();
            int avg = myDb.getAverageValue();
            String pfr = "Average Peak Flow rate : " + avg;
            System.out.println(pfr);
            displayText(pfr);
        }
        catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }
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
        TextView textView = (TextView) findViewById(R.id.textAvg);
        textView.setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick_Connect(View V){
        Cursor c = myDb.getAllReadings();
        if(c.getCount()!=0){
            Intent i = new Intent(Main.this, DeviceListActivity.class);
            startActivity(i);
        }
        else{
            Toast.makeText(this,"Please Enter your details First",Toast.LENGTH_LONG).show();
        }

    }
    public void onClick_History(View V){
        Intent i = new Intent(Main.this, GetHistory2.class);
        startActivity(i);
    }
    public void onClick_Details(View V){
        Intent i = new Intent(Main.this, EnterDetails.class);
        startActivity(i);
    }
    public void onClick_Charts(View V){
        Intent i = new Intent(Main.this, HistoryChart.class);
        startActivity(i);
    }
}

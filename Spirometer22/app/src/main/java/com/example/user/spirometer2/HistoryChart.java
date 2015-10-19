package com.example.user.spirometer2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.sql.SQLOutput;

public class HistoryChart extends ActionBarActivity {

    DBAdapter myDb;
    private LinearLayout mainLayout;
    private LineChart mChart;
    //(234,173,154)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_chart);
        openDB();
        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        //create line chart
        mChart = new LineChart(this);
        //add to main layout
        mainLayout.addView(mChart, new LinearLayout.LayoutParams(-1,-1));

        //customize line chart
        mChart.setDescription("");
        mChart.setNoDataTextDescription("No data for moment");

        //enable value highlighting
        mChart.setHighlightEnabled(true);

        //enable touch gestures
        mChart.setTouchEnabled(true);

        //we want also enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(true);

        //enable pinch zoom to avoid scaling x and y asxis seperatly
        mChart.setPinchZoom(true);

        //alternative background colour
        mChart.setBackgroundColor(Color.LTGRAY);

        //now we work on data
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        //add data to line chart
        mChart.setData(data);

        //get legand object
        Legend l = mChart.getLegend();

        //customize legend
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis x1 = mChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);

        YAxis y1 = mChart.getAxisLeft();
        y1.setTextColor(Color.WHITE);
        y1.setAxisMaxValue(1000f);
        y1.setDrawGridLines(true);

        YAxis y12 = mChart.getAxisRight();
        y12.setEnabled(false);
        int count = myDb.getNumRows();
        System.out.println(count);
        for(int i = 1; i<=count;i++){
            addEntry(i); // chart is nortified of update in addEntry method
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

    /*@Override
    protected void onResume() {
        super.onResume();
        //now, we're going to simulate realtime data addition



    }// */

    private void addEntry(int id){
        LineData data = mChart.getData();
        int value;
        String date;
        //String newDate;
        if(data!= null){
            LineDataSet set = data.getDataSetByIndex(0);

            if(set == null){
                //creation if null
                set = createSet();
                data.addDataSet(set);
            }
            value = myDb.getValue(id);
            date = myDb.getDate(id);
            //newDate = date.substring(0,date.indexOf(" ")-1) +'\n'+date.substring(date.indexOf(" "));

            // add a new random value

            data.addXValue(date);
            //data.addXValue(date);
            data.addEntry(new Entry((float) value, set.getEntryCount()), 0);

            //notify chart data have changed
            mChart.notifyDataSetChanged();

            //Limitvnumber of visible entries
            mChart.setVisibleXRange(1,2);

            //scroll to the last entry
            mChart.moveViewToX(data.getXValCount()-7);

            //mChart.
        }
    }

    //method to create set
    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "Peak Flow Rate");
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleSize(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 177));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);
        //set.addEntry();
        return  set;
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
}

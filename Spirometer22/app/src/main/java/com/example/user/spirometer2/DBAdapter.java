
// TODO: Change the package to match your project.
package com.example.user.spirometer2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final String KEY_ROWID2 = "_id2";
    public static final int COL_ROWID = 0;
    public static final int COL_ROWID2 = 0;


    //* CHANGE 1:

    // TODO: Setup your fields in Details here:
    public static final String KEY_AGE = "age";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_GENDR = "gender";

    // TODO: Setup your fields in Readings here:
    public static final String KEY_READING = "reading";
    public static final String KEY_CONDITION = "condition";
    public static final String KEY_TIME = "time";

    //

    // TODO: Setup your field numbers here in DETAILS(0 = KEY_ROWID, 1=...)
    public static final int COL_AGE = 1;
    public static final int COL_WEIGHT = 2;
    public static final int COL_HEIGHT = 3;
    public static final int COL_GENDER = 4;

    // TODO: Setup your field numbers in READINGS here (0 = KEY_ROWID2, 1=...)
    public static final int COL_READING = 1;
    public static final int COL_CONDITION = 2;
    public static final int COL_TIME = 3;


    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_AGE, KEY_WEIGHT, KEY_HEIGHT, KEY_GENDR};
    public static final String[] ALL_KEYS2 = new String[]{KEY_ROWID2, KEY_READING, KEY_CONDITION, KEY_TIME};
    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "Spirometry";
    public static final String DATABASE_TABLE1 = "Details";
    public static final String DATABASE_TABLE2 = "Readings";
    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE1
                    + " (" + KEY_ROWID + " integer not null, "


                    // CHANGE 2:

                    // TODO: Place your fields here!
                    // + KEY_{...} + " {type} not null"
                    //	- Key is the column name you created above.
                    //	- {type} is one of: text, integer, real, blob
                    //		(http://www.sqlite.org/datatype3.html)
                    //  - "not null" means it is a required field (must be given a value).
                    // NOTE: All must be comma separated (end of line!) Last one must have NO comma!!
                    + KEY_AGE + " string not null, "
                    + KEY_WEIGHT + " integer not null, "
                    + KEY_HEIGHT + " integer not null, "
                    + KEY_GENDR + " string not null,"
                    + KEY_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    // Rest  of creation:
                    + ");";
    private static final String DATABASE_CREATE_SQL2 =
            "create table " + DATABASE_TABLE2
                    + " (" + KEY_ROWID2 + " integer primary key autoincrement, "


                    // CHANGE 2:

                    // TODO: Place your fields here!
                    + KEY_READING + " integer not null, "
                    + KEY_CONDITION + " integer not null, "
                    + KEY_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    // Rest  of creation:
                    + ");";
    // Context of application who uses us.
    private final Context context;
    private int count;
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertDetails(String dob, int weight, int height, String gender) {
        //
        //* CHANGE 3:
        //
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        System.out.println(weight);
        System.out.println(height);
        System.out.println(gender);
        System.out.println(dob);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, 1);
        initialValues.put(KEY_AGE, dob);
        initialValues.put(KEY_WEIGHT, weight);
        initialValues.put(KEY_HEIGHT, height);
        initialValues.put(KEY_GENDR, gender);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE1, null, initialValues);
    }


    public long insertReading(String reading, int condition) {
        //
        //* CHANGE 3:
        //
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        System.out.println(reading);
        System.out.println(condition);
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_READING, reading);
        initialValues.put(KEY_CONDITION, condition);
        // Insert it into the database.
        return db.insert(DATABASE_TABLE2, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteDetails(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE1, where, null) != 0;
    }

    public boolean deleteReading(long rowId) {
        String where = KEY_ROWID2 + "=" + rowId;
        return db.delete(DATABASE_TABLE2, where, null) != 0;
    }

    public void deleteAllDetails() {
        Cursor c = getAllDetails();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteDetails(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    public void deleteAllReadings() {
        Cursor c = getAllReadings();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID2);
        if (c.moveToFirst()) {
            do {
                deleteDetails(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }


    // Return all data in the database.
    public Cursor getAllDetails() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE1, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Return all data in the database.
    public Cursor getAllReadings() {
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE2, ALL_KEYS2,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        setNumRows(c.getCount());
        return c;
    }

    // Return all data in the database.
    public int getAverageValue() {
        String where = KEY_CONDITION + "=1";
        String[] Cloumn = {KEY_READING};
        int sum=0;
        int avg;
        Cursor c = db.query(true, DATABASE_TABLE2, Cloumn,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        if (c.moveToFirst()) {
            do {
                int readings = Integer.parseInt(c.getString(0));
                sum += readings;
            } while(c.moveToNext());
        }
        if(c.getCount()>0) {
            avg = (int)(sum/c.getCount());
            return avg;
        }
        else
            return 0;
    }

    // Return all data in the database.
    public int getHighestValue() {
        String where = null;
        String[] Cloumn = {KEY_READING};
        int sum=0;
        int avg;
        Cursor c = db.query(true, DATABASE_TABLE2, Cloumn,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        if (c.moveToFirst()) {
            do {
                int readings = Integer.parseInt(c.getString(0));
                if(sum < readings){
                    sum = readings;
                }
            } while(c.moveToNext());
        }
        return sum;
    }

    public int getLastValue() {
        String where = null;
        String[] Cloumn = {KEY_READING};
        int sum=0;
        int avg;
        Cursor c = db.query(true, DATABASE_TABLE2, Cloumn,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToLast();
        }
        if (c.moveToLast()) {
            int readings = Integer.parseInt(c.getString(0));
            sum = readings;
        }
        return sum;
    }

    public int getNumRows(){
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE2, ALL_KEYS2,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        setNumRows(c.getCount());
        return count;
    }

    public void setNumRows(int count){
         this.count = count;
    }

    // Get a specific row (by rowId)
    public Cursor getDetails(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = db.query(true, DATABASE_TABLE1, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getReadings(long rowId) {
        String where = KEY_ROWID2 + "=" + rowId;
        Cursor c = db.query(true, DATABASE_TABLE2, ALL_KEYS2,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public int getValue(int id){
        String where = KEY_ROWID2 + "=" + id;
        String[] Column = {KEY_READING};
        String value;
        int val;
        Cursor c = db.query(true, DATABASE_TABLE2, Column,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        value = c.getString(0);
        val = Integer.parseInt(value);
        System.out.println(val);
        return val;
    }
    //time stamp of readings
    public String getDate(int id){
        String where = KEY_ROWID2 + "=" + id;
        String[] Column = {KEY_TIME};
        String date;
        Cursor c = db.query(true, DATABASE_TABLE2, Column,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        date = c.getString(0);
        return date;
    }

    //calculate age
    public int getAge(){
        String[] Column = {KEY_AGE};
        String[] dob;
        String[] ctime;
        String date;
        int year,cyear, month, day;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Cursor c = db.query(true, DATABASE_TABLE1, Column,
                null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        date = c.getString(0);
        dob = date.split("-");
        year = Integer.parseInt(dob[0]);
        month = Integer.parseInt(dob[1]);
        day =  Integer.parseInt(dob[2]);
        date = df.format(calendar.getTime());
        ctime = date.split("-");
        cyear = Integer.parseInt(ctime[0]);
        return (cyear-year);
    }

    // Change an existing row to be equal to new data.
    public boolean updateDetails(long rowId, String dob, int weight, int height, String gender) {
        String where = KEY_ROWID + "=" + rowId;

        //
        // * CHANGE 4:
        //
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_ROWID, 1);
        newValues.put(KEY_AGE, dob);
        newValues.put(KEY_WEIGHT, weight);
        newValues.put(KEY_HEIGHT, height);
        newValues.put(KEY_GENDR, gender);

        // Insert it into the database.
        return db.update(DATABASE_TABLE1, newValues, where, null) != 0;
    }

    // Change an existing row to be equal to new data.
    public boolean updateReadings(long rowId, String reading, int condition) {
        String where = KEY_ROWID2 + "=" + rowId;

        //
        // * CHANGE 4:
        //
        // TODO: Update data in the row with new fields.
        // TODO: Also change the function's arguments to be what you need!
        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_READING, reading);
        newValues.put(KEY_CONDITION, condition);
        // Insert it into the database.
        return db.update(DATABASE_TABLE2, newValues, where, null) != 0;
    }

    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    ///
    //* Private class which handles database creation and upgrading.
    //* Used to handle low-level database access.
    //
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
            _db.execSQL(DATABASE_CREATE_SQL2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE1);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
